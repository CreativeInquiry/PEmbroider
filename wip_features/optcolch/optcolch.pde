import java.util.*;

int N_LAYER = 12;
int W_LAYER = 128;
int W_VIS = 150;

int[] colorTable = {
  color(255,0,0),
  color(0,255,0),
  color(0,0,255),
  color(255,255,255),
  //color(0,255,255),
  //color(255,0,255),
};

class Layer{
  int id;
  int col;
  boolean[] data;
}

class Solution{
  Layer[] layers;
  int[] render;
  int score = Integer.MAX_VALUE;
}

void renderSolution(Solution sol){
  sol.render = new int[W_LAYER*W_LAYER];
  for (int i = 0; i < sol.layers.length; i++){
    for (int j = 0; j < sol.layers[i].data.length; j++){
      if (sol.layers[i].data[j]){
        sol.render[j] = sol.layers[i].col;
      }
    }
  }
}

boolean solutionEq(Solution sol0, Solution sol1){
  if (sol0.render == null){
    renderSolution(sol0);
  }
  if (sol1.render == null){
    renderSolution(sol1);
  }
  for (int i = 0; i < sol0.render.length; i++){
    if (sol0.render[i] != sol1.render[i]){
      return false;
    }
  }
  return true;
}

void drawRender(int[] render){
  for (int i = 0; i < render.length; i++){
    int x = i % W_LAYER;
    int y = i / W_LAYER;
    noStroke();
    fill(red(render[i]),green(render[i]),blue(render[i]));
    rect(x,y,1,1);
  }
}

Layer[] shuffleLayers(Layer[] layers){
  Layer[] a = new Layer[layers.length];
  for (int i = 0; i < layers.length; i++){
    a[i] = layers[i];
  }
  for (int i = a.length - 1; i > 0; i--) {
      int j = (int)floor(random((i + 1)));
      Layer x = a[i];
      a[i] = a[j];
      a[j] = x;
  }
  return a;
}

int countColorChange(Solution sol){
  int cc = 0;
  for (int i = 1; i < sol.layers.length; i++){
    if (sol.layers[i].col != sol.layers[i-1].col){
      cc++;
    }
  }
  return cc;
}

void scoreSolution(Solution sol){
  if (!solutionEq(standard,sol)){
    sol.score = Integer.MAX_VALUE;
    return;
  }
  sol.score = countColorChange(sol);
}

Solution newSolution(){
  Solution sol = new Solution();
  sol.layers = shuffleLayers(standard.layers);
  while (countColorChange(sol) >= standard.score){
    sol.layers = shuffleLayers(standard.layers);
  }
  scoreSolution(sol);
  return sol;
}

void drawSolution(Solution sol){
  drawRender(sol.render);
  text("# color change = "+sol.score,0,W_LAYER+20);
  String permstr = "";
  for (int j = 0; j < sol.layers.length; j++){
    permstr += hex(sol.layers[j].id,1);
  }
  text(permstr,0,W_LAYER+10);
  for (int j = 0; j < sol.layers.length; j++){
    fill(sol.layers[j].col);
    stroke(0);
    strokeWeight(1);
    rect(W_LAYER,j*4,W_VIS-W_LAYER,4);
  }
  
}

Solution standard;
int numTested = 0;
ArrayList<Solution> solutions;

void setup(){
  size(1500,900);
  standard = new Solution();
  standard.layers = new Layer[N_LAYER];
  
  for (int i = 0; i < standard.layers.length; i++){
    Layer l = new Layer();
    l.id = i+1;
    PGraphics pg = createGraphics(W_LAYER,W_LAYER);
    pg.beginDraw();
    pg.background(0);
    pg.fill(255);
    pg.noStroke();
    pg.triangle(random(pg.width), random(pg.height),
                random(pg.width), random(pg.height),
                random(pg.width), random(pg.height));
    pg.loadPixels();
    l.data = new boolean[pg.pixels.length];
    for (int j = 0; j < pg.pixels.length; j++){
      l.data[j] = (pg.pixels[j]&255)>128;
      
    }
    pg.endDraw();
    
    l.col = colorTable[(int)random(colorTable.length)];
    while (i != 0 && l.col == standard.layers[i-1].col){
      l.col = colorTable[(int)random(colorTable.length)];
    }
    standard.layers[i] = l;
  }
  scoreSolution(standard);
  solutions = new ArrayList<Solution>();
  solutions.add(standard);
  drawSolution(standard);
  println("original score:",standard.score);
  
}

void draw(){

  for (int i = 0; i < 5000; i++){
    Solution sol = newSolution();
    if (sol.score <= solutions.get(solutions.size()-1).score){
      //println("score: ",sol.score);
      int ro = solutions.size() / (width/W_VIS);
      int co = solutions.size() % (width/W_VIS);
      pushMatrix();
      translate(co*W_VIS,ro*W_VIS);
      drawSolution(sol);
      popMatrix();
      solutions.add(sol);
    }
    numTested++;
  }
  surface.setTitle("tested permutations: "+numTested);
}
