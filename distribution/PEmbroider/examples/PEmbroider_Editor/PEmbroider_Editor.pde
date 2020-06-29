import processing.embroider.*;
PEmbroiderGraphics E;

int W = 800;
int H = 800;

int PX = 40;

void defaultSettings(PEmbroiderGraphics E){
  // put additional settings for each layer here
  // e.g. E.hatchAngleDeg(30);
}

static final int LIN = 1;
static final int PLY = 2;
static final int TXT = 3;

static final int TOOL_FREEHAND = 1;
static final int TOOL_VERTEX = 2;
static final int TOOL_PAINT = 3;
static final int TOOL_FATPAINT = 4;
static final int TOOL_TEXT = 5;
static final int TOOL_EDIT = 6;

String[] tooltip = {
  "",
  "FREEHAND: Drag mouse to draw polygon, lift mouse to finish.",
  "VERTEX: Click to add polygon vertex, double-click to finish.",
  "PAINT: Drag mouse to paint curves.",
  "FATPAINT: Drag mouse to paint fat curves.",
  "TEXT: Click to add text to location.",
  "EDIT: Click and drag vertex to move it.",
};

int tool = TOOL_FREEHAND;
int currentLayer = 0;
boolean needsUpdate = true;

int editState = 0;
int editI = 0;
int editJ = 0;

PApplet app = this;

ArrayList<Layer> layers;

ArrayList<PVector> polyBuff;

PGraphics render;

class Element{
  int type;
  ArrayList<PVector> data;
  float paramF0;
  float paramF1;
  String paramS0;
  Element(int _type){
    type = _type;
    data = new ArrayList<PVector>();
  }
}
class Layer{
  int hatchMode = PEmbroiderGraphics.CONCENTRIC;
  int strokeMode = PEmbroiderGraphics.TANGENT;
  int hatchColor = color(0,0,255);
  int strokeColor = color(255,0,0);
  float hatchSpacing = 4;
  float strokeWeight = 10;
  float paintWeight = 20;
  boolean visible = true;
  boolean cull = true;
  ArrayList<Element> elements;
  //PGraphics mask;
  PGraphics render;
  
  PEmbroiderGraphics E;
  
  Layer(){
    E = new PEmbroiderGraphics(app,W,H);
    defaultSettings(E);

    render = createGraphics(W,H);
    render.beginDraw();
    render.background(0);
    render.endDraw();
    
    elements = new ArrayList<Element>();
  }
}

void rasterizeLayer(Layer lay){
  PGraphics pg = lay.render;
  pg.beginDraw();
  pg.push();
  pg.background(0);
  for (int i = 0; i < lay.elements.size(); i++){
    Element elt = lay.elements.get(i);
    if (elt.type == TXT){
      pg.fill(255);
      pg.noStroke();
      pg.textSize(elt.paramF0);
      pg.text(elt.paramS0,elt.data.get(0).x,elt.data.get(0).y);
    }else{
      if (elt.type == LIN){
        pg.noFill();
        pg.strokeWeight(elt.paramF0);
        pg.stroke(255);
      }else if (elt.type == PLY){
        pg.noStroke();
        pg.fill(255);
      }
      pg.beginShape();
      for (int j = 0; j < elt.data.size(); j++){
        PVector p = elt.data.get(j);
        pg.vertex(p.x,p.y);
      }
      pg.endShape();
    }
  }
  pg.pop();
  pg.endDraw();
}

void stitchLayer(int idx){
  Layer lay = layers.get(idx);
  if (0==lay.elements.size()){
    if (E!=null){
      E.clear();
    }
    return;
  }
  rasterizeLayer(lay);
  if (lay.cull){
    lay.render.beginDraw();
    lay.render.blendMode(SUBTRACT);
    for (int i = idx+1; i < layers.size(); i++){
      rasterizeLayer(layers.get(i));
      lay.render.image(layers.get(i).render,0,0);
    }
    lay.render.blendMode(BLEND);
    lay.render.endDraw();
  }
  PEmbroiderGraphics E = lay.E;
  E.clear();
  E.strokeWeight(lay.strokeWeight);
  E.hatchSpacing(lay.hatchSpacing);
  if (lay.strokeColor == 0){
    E.noStroke();
  }else{
    E.stroke((lay.strokeColor>>16)&255, (lay.strokeColor>>8)&255, (lay.strokeColor)&255);
  }
  if (lay.hatchColor == 0){
    E.noFill();
  }else{
    E.fill((lay.hatchColor>>16)&255, (lay.hatchColor>>8)&255, (lay.hatchColor)&255);
  }
  E.hatchMode(lay.hatchMode);
  E.strokeMode(lay.strokeMode);
  E.image(lay.render,0,0);
}

void visualize(PEmbroiderGraphics E){
  render.beginDraw();
  for (int i = 0; i < E.polylines.size(); i++) {
    render.stroke(app.red(E.colors.get(i)),app.green(E.colors.get(i)),app.blue(E.colors.get(i)));  
    render.strokeWeight(1);
    render.beginShape();
    render.noFill();
    for (int j = 0; j < E.polylines.get(i).size(); j++) {
      PVector p0 = E.polylines.get(i).get(j);
      render.vertex(p0.x,p0.y);
    }
    render.endShape();
  }
  render.endDraw();
}

void newElementFromPolyBuff(){
  Layer lay = layers.get(currentLayer);
  Element elt = new Element(((tool==TOOL_PAINT) || (tool==TOOL_FATPAINT))?LIN:PLY);
  elt.data = new ArrayList<PVector>(polyBuff);
  elt.paramF0 = tool == TOOL_FATPAINT ? 60 : 20;
  lay.elements.add(elt);
  polyBuff.clear();
  needsUpdate = true;
}

void switchTool(int what){
  tool = what;
  polyBuff.clear();
  needsUpdate = true;
  mousePressed = false;
}

void writeOut(String path){
  PEmbroiderGraphics E = new PEmbroiderGraphics(app,W,H);
  E.setPath(path);
  for (int i = 0; i < layers.size(); i++){
    E.polylines.addAll(layers.get(i).E.polylines);
    E.colors.addAll(layers.get(i).E.colors);
    E.cullGroups.addAll(layers.get(i).E.cullGroups);
  }
  E.optimize();
  E.endDraw();
}

void drawToolsGui(){
  push();
  textAlign(CENTER,CENTER);
  fill(tool==TOOL_FREEHAND?180:255);
  stroke(0);
  strokeWeight(1);
  rect(0,0,PX,PX);
  fill(0);
  textSize(30);
  text("S",PX/2,PX/2-5);
  if (!mouseOnCanvas() && mousePressed && 0 <= mouseX && mouseX <= PX && 0 <= mouseY && mouseY <= PX){
    switchTool(TOOL_FREEHAND);
  }
  
  fill(tool==TOOL_VERTEX?180:255);
  stroke(0);
  strokeWeight(1);
  rect(0,PX,PX,PX);
  fill(0);
  textSize(30);
  text("Z",PX/2,PX+PX/2-5);
  if (!mouseOnCanvas() && mousePressed && 0 <= mouseX && mouseX <= PX && PX <= mouseY && mouseY <= PX*2){
    switchTool(TOOL_VERTEX);
  }

  
  fill(tool==TOOL_PAINT?180:255);
  stroke(0);
  strokeWeight(1);
  rect(0,PX*2,PX,PX);
  fill(0);
  textSize(30);
  text("o",PX/2,PX*2+PX/2-5);
  if (!mouseOnCanvas() && mousePressed && 0 <= mouseX && mouseX <= PX && PX*2 <= mouseY && mouseY <= PX*3){
    switchTool(TOOL_PAINT);
  }
  
  fill(tool==TOOL_FATPAINT?180:255);
  stroke(0);
  strokeWeight(1);
  rect(0,PX*3,PX,PX);
  fill(0);
  textSize(30);
  text("O",PX/2,PX*3+PX/2-5);
  if (!mouseOnCanvas() && mousePressed && 0 <= mouseX && mouseX <= PX && PX*3 <= mouseY && mouseY <= PX*4){
    switchTool(TOOL_FATPAINT);
  }
  
  fill(tool==TOOL_TEXT?180:255);
  stroke(0);
  strokeWeight(1);
  rect(0,PX*4,PX,PX);
  fill(0);
  textSize(30);
  text("T",PX/2,PX*4+PX/2-5);
  if (!mouseOnCanvas() && mousePressed && 0 <= mouseX && mouseX <= PX && PX*4 <= mouseY && mouseY <= PX*5){
    switchTool(TOOL_TEXT);
  }
  
  fill(tool==TOOL_EDIT?180:255);
  stroke(0);
  strokeWeight(1);
  rect(0,PX*5,PX,PX);
  fill(0);
  textSize(30);
  text("E",PX/2,PX*5+PX/2-5);
  if (!mouseOnCanvas() && mousePressed && 0 <= mouseX && mouseX <= PX && PX*5 <= mouseY && mouseY <= PX*6){
    switchTool(TOOL_EDIT);
  }
  
  fill(255);
  stroke(0);
  strokeWeight(1);
  rect(0,H-PX,PX,PX);
  fill(0);
  textSize(14);
  text("SAVE",PX/2,H-PX+PX/2-2);
  if (!mouseOnCanvas() && mousePressed && 0 <= mouseX && mouseX <= PX && H-PX <= mouseY && mouseY <= H){
    
    /* commented out file dialog because Swing's file chooser appears to be occassionally broken and hangs the app forever :(
     */
    //javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
    //fileChooser.setDialogTitle("Save embroidery file");   
    ////fileChooser.setCurrentDirectory(new File(sketchPath()));
    //int userSelection = fileChooser.showSaveDialog(null);
    //if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
    //    File fileToSave = fileChooser.getSelectedFile();
    //    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
    //    writeOut(fileToSave.getAbsolutePath());
    //}
    
    /* plain old input box */
    String path = (String)javax.swing.JOptionPane.showInputDialog(null,"Path is relative to the sketch folder, use / for starting absolute paths","Save Embroidery File",javax.swing.JOptionPane.QUESTION_MESSAGE,null,null,"untitled.vp3");
    if (path != null && path.length()>0){
        javax.swing.JOptionPane.showMessageDialog(null, "Optimizing stroke order and saving file, this might take a while...");
        writeOut(sketchPath(path));
        javax.swing.JOptionPane.showMessageDialog(null, "Embroidery file saved!");
    }
    mousePressed = false;
  }
  
  pop();
}

 
void drawLayersGui(){
  int ww = width-PX-W;
  pushStyle();
  pushMatrix();
  translate(PX+W,0);
  
  float oy = 0;
  for (int i = 0; i < layers.size(); i++){
    boolean clicked = false;
    Layer lay = layers.get(i);
    fill(i == currentLayer ? 180 : 255);
    stroke(0);
    strokeWeight(1);
    rect(0,oy,ww,50);
    rasterizeLayer(layers.get(i));
    image(layers.get(i).render,4,oy+4,42,42);
    

    fill((lay.hatchColor>>16)&255, (lay.hatchColor>>8)&255, (lay.hatchColor)&255);
    rect(50,oy+24,20,20);
    
    if (!mouseOnCanvas() && mousePressed && PX+W+50 <= mouseX && mouseX <= PX+W+70 && oy+24 <= mouseY && mouseY <= oy+44){
      java.awt.Color col = javax.swing.JColorChooser.showDialog(
        null,
        "Select hatch color",
        new java.awt.Color((lay.hatchColor>>16)&255, (lay.hatchColor>>8)&255, (lay.hatchColor)&255)
      );
      if (col != null){
        lay.hatchColor = color(col.getRed(),col.getGreen(),col.getBlue());
      }
      String rawInp = javax.swing.JOptionPane.showInputDialog("Enter hatch spacing",lay.hatchSpacing);
      if (rawInp != null){
        float n = Float.parseFloat(rawInp);
        lay.hatchSpacing=n;
        
        Object[] options = {"Parallel","Concentric"};
        int op = javax.swing.JOptionPane.showOptionDialog(null, "Select hatch mode", "Hatch Mode",
            javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE,
            null, options, lay.hatchMode==PEmbroiderGraphics.PARALLEL?"Parallel":"Concentric");
        if (op == 0) {
          lay.hatchMode = PEmbroiderGraphics.PARALLEL;
        }else if (op == 1) {
          lay.hatchMode = PEmbroiderGraphics.CONCENTRIC;
        }
      }
      needsUpdate = true;
      mousePressed = false;
      clicked = true;
    }
    
    fill(255);
    rect(70,oy+24,20,20);
    strokeWeight(4);
    stroke((lay.strokeColor>>16)&255, (lay.strokeColor>>8)&255, (lay.strokeColor)&255);
    line(74,oy+28,86,oy+40);
    
    if (!mouseOnCanvas() && mousePressed && PX+W+70 <= mouseX && mouseX <= PX+W+90 && oy+24 <= mouseY && mouseY <= oy+44){
      java.awt.Color col = javax.swing.JColorChooser.showDialog(
        null,
        "Select stroke color",
        new java.awt.Color((lay.strokeColor>>16)&255, (lay.strokeColor>>8)&255, (lay.strokeColor)&255)
      );
      if (col != null){
        lay.strokeColor = color(col.getRed(),col.getGreen(),col.getBlue());
        
      }
      String rawInp = javax.swing.JOptionPane.showInputDialog("Enter stroke weight",""+lay.strokeWeight);
      if (rawInp != null && rawInp.length()>0){
        float n = Float.parseFloat(rawInp);
  
        lay.strokeWeight=n;
        
        Object[] options = {"Tangent","Perpendicular"};
        int op = javax.swing.JOptionPane.showOptionDialog(null, "Select stroke mode", "Stroke Mode",
            javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE,
            null, options, lay.strokeMode==PEmbroiderGraphics.TANGENT?"Tangent":"Perpendicular");
        if (op == 0) {
          lay.strokeMode = PEmbroiderGraphics.TANGENT;
        }else if (op == 1) {
          lay.strokeMode = PEmbroiderGraphics.PERPENDICULAR;
        }
      }
      needsUpdate = true;
      mousePressed = false;
      clicked = true;
    }
    
    fill(255);
    stroke(0);
    strokeWeight(1);
    rect(90,oy+24,20,20);
    circle(98,oy+34,8);
    if (!lay.cull){
      noFill();
    }
    circle(103,oy+34,8);
    if (!mouseOnCanvas() && mousePressed && PX+W+90 <= mouseX && mouseX <= PX+W+110 && oy+24 <= mouseY && mouseY <= oy+44){
      lay.cull = !lay.cull;
      needsUpdate = true;
      mousePressed = false;
    }
    
    
    fill(255);
    stroke(0);
    strokeWeight(1);
    rect(110,oy+24,20,20);
    fill(0);
    textSize(12);
    textAlign(CENTER,CENTER);
    if (lay.visible){
      text("o",121,oy+32);
      text("<",116,oy+32);
      text(">",126,oy+32);
    }else{
      text("=",121,oy+32);
      text("-",116,oy+32);
      text("-",126,oy+32);
    }
    if (!mouseOnCanvas() && mousePressed && PX+W+110 <= mouseX && mouseX <= PX+W+130 && oy+24 <= mouseY && mouseY <= oy+44){
      lay.visible = !lay.visible;
      needsUpdate = true;
      mousePressed = false;
    }
    
    fill(255);
    stroke(0);
    strokeWeight(1);
    rect(130,oy+24,20,20);
    fill(0);
    textSize(12);
    textAlign(CENTER,CENTER);
    text("X",141,oy+33);
 
    if (!mouseOnCanvas() && mousePressed && PX+W+130 <= mouseX && mouseX <= PX+W+150 && oy+24 <= mouseY && mouseY <= oy+44){

      if (layers.size() <= 1){
        javax.swing.JOptionPane.showMessageDialog(null, "Cannot delete the only layer!");
        needsUpdate = true;
        mousePressed = false;
      }else{
        if (currentLayer == i){
          if (currentLayer > 0){
            currentLayer --;
          }else{
            currentLayer ++;
          }
        }
        layers.remove(i);
        while (currentLayer >= layers.size()){
          currentLayer--;
        }
        needsUpdate = true;
        mousePressed = false;
        popMatrix();
        popStyle();
        return;
      }
    }
    
    fill(0);
    noStroke();
    textSize(12);
    textAlign(LEFT,BASELINE);
    text("Layer "+i,50,oy+14);
    
    if (!clicked && !mouseOnCanvas() && mousePressed && PX+W <= mouseX && mouseX <= width && oy <= mouseY && mouseY <= oy+50){
      currentLayer = i;
    }
    
    oy +=50;
  }
  
  fill(255);
  stroke(0);
  strokeWeight(1);
  rect(0,oy,ww,30);
  fill(0);
  noStroke();
  textAlign(CENTER,CENTER);
  textSize(18);
  text("+",ww/2,oy+13);
  if (!mouseOnCanvas() && mousePressed && PX+W <= mouseX && mouseX <= width && oy <= mouseY && mouseY <= oy+30){
    Layer lay = new Layer();
    lay.strokeColor = color(random(255),random(255),random(255));
    lay.hatchColor = color(random(255),random(255),random(255));
    layers.add(lay);
    currentLayer = layers.size()-1;
    mousePressed = false;
  }
  
  popMatrix();
  popStyle();
}
void drawGui(){
  drawToolsGui();
  drawLayersGui();
  pushStyle();
  fill(200);
  stroke(0);
  strokeWeight(1);
  rect(0,H,width,height-H);
  fill(0);
  noStroke();
  textAlign(LEFT,BOTTOM);
  textSize(12);
  text(tooltip[tool%tooltip.length],5,height-2);
  popStyle();
}
void drawEditMode(){
  render.beginDraw();
  render.clear();
  render.background(255);
  
  Layer lay = layers.get(currentLayer);
  for (int i = 0; i < lay.elements.size(); i++){
    Element elt = lay.elements.get(i);
    
    render.noFill();
    render.stroke(0);
    render.beginShape();
    for (int j = 0; j < elt.data.size(); j++){
      render.vertex(elt.data.get(j).x,elt.data.get(j).y);
    }

    render.endShape();
    for (int j = 0; j < elt.data.size(); j++){
      boolean isSel = false;
      if (editState == 0){
        isSel = new PVector(mouseX-PX,mouseY).dist(elt.data.get(j))<10;
      }else{
        isSel = (i == editI) && (j == editJ);
      }
      if (isSel){
        render.fill(0,0,255);
        render.rect(elt.data.get(j).x-4,elt.data.get(j).y-4,8,8);
        if (editState == 0 && mousePressed){
          editState = 1;
          editI = i;
          editJ = j;
        }
      }else{
        render.noFill();
        render.rect(elt.data.get(j).x-2,elt.data.get(j).y-2,4,4);
      }
      
    }  
  }
  if (editState == 1){
    lay.elements.get(editI).data.get(editJ).x = mouseX-PX;
    lay.elements.get(editI).data.get(editJ).y = mouseY;
    if (!mousePressed){
      editState = 0;
    }
  }
  render.endDraw();
}

void setup(){
  size(1000,820);
  render = createGraphics(W,H);
  layers = new ArrayList<Layer>();
  layers.add(new Layer());
  polyBuff = new ArrayList<PVector>();

}

boolean mouseOnCanvas(){
  return PX < mouseX && mouseX < PX+W && 0 < mouseY && mouseY < H;
}


void draw(){
  
  background(100);
  
  if (tool == TOOL_EDIT){
    drawEditMode();
  }else if (needsUpdate){
    render.beginDraw();
    render.clear();
    render.background(255);
    render.endDraw();
    for (int i = layers.size()-1; i >= 0; i--){
      if (layers.get(i).visible){
        stitchLayer(i);
        visualize(layers.get(i).E);
      }
    }
    needsUpdate = false;
  
  }
  
  image(render,PX,0);
  
  stroke(0);
  noFill();
  strokeWeight(1);
  rect(PX,0,W,H);
  beginShape();
  for (int i = 0; i < polyBuff.size(); i++){
    vertex(polyBuff.get(i).x+PX,polyBuff.get(i).y);
    rect(polyBuff.get(i).x-2+PX,polyBuff.get(i).y-2,4,4);
  }
  vertex(mouseX,mouseY);
  endShape();
  
  drawGui();
  
  if (mouseOnCanvas()){
    if (tool == TOOL_FREEHAND || tool == TOOL_PAINT || tool == TOOL_FATPAINT){
      if (mousePressed){
        PVector p = new PVector(mouseX-PX,mouseY);
        if (polyBuff.size() == 0 || polyBuff.get(polyBuff.size()-1).dist(p) > 10){
          polyBuff.add(p);
        }
      }else if (polyBuff.size() > 2){
        newElementFromPolyBuff();
      }
    }
  }
  
}
void mousePressed(MouseEvent evt){
  if (mouseOnCanvas()){
    if (tool == TOOL_VERTEX){
      if (evt.getCount() == 1){
        polyBuff.add(new PVector(mouseX-PX,mouseY));
      }else{
        newElementFromPolyBuff();
      }
    }else if (tool == TOOL_TEXT){
      String txt = javax.swing.JOptionPane.showInputDialog("Enter Text");
      String rawInp = javax.swing.JOptionPane.showInputDialog("Enter Text Size",128);
      if (txt != null && rawInp != null){
        float siz = Float.parseFloat(rawInp);
        Layer lay = layers.get(currentLayer);
        Element elt = new Element(TXT);
        elt.paramS0 = txt;
        elt.paramF0 = siz;
        polyBuff.add(new PVector(mouseX-PX,mouseY));
        elt.data = new ArrayList<PVector>(polyBuff);
        lay.elements.add(elt);
      }
      polyBuff.clear();
      needsUpdate = true;
    }
  }
}
