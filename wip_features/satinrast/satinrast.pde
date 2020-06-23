float shapeCoordsData[][] = {
  {579, 51}, {712, 59}, {716, 182}, {744, 300}, {748, 345}, 
  {749, 389}, {732, 470}, {716, 510}, {692, 551}, {648, 618}, 
  {585, 670}, {471, 699}, {372, 729}, {282, 711}, {169, 643}, 
  {88, 562}, {50, 226}, {120, 159}, {252, 183}, {244, 350}, 
  {137, 234}, {137, 350}, {323, 487}, {293, 103}, {365, 78}, 
  {508, 459}, {496, 279}, {575, 276}, {665, 382}, {628, 119}, 
  {598, 228}, {474, 154}};
  
//float shapeCoordsData[][] = {{119,490},{410,616},{410,616},{536,445},{721,447},{537,354},{473,207},{369,202},{413,383},{425,255},{466,263},{514,393},{406,436},{338,194},{441,135},{297,179}};

ArrayList<Pt> stitches;
float reso = 0.125;
Im srcImg;
Im cpyImg;
  
class Pt{
  int x;
  int y;
  Pt(int _x, int _y){
    x = _x;
    y = _y;
  }
  Pt(Pt p){
    x = p.x;
    y = p.y;
  }
  public String toString(){
    return "("+x+","+y+")";
  }
}

class Im{
  int[] data;
  int w;
  int h;
  Im (int _w, int _h){
    data = new int[_w*_h];
    w = _w;
    h = _h;
  }
  Im (PImage im){
    data = new int[im.width*im.height];
    im.loadPixels();
    for (int i = 0; i < im.width*im.height; i++){
      data[i] = ((im.pixels[i]&255) > 128) ? 1 : 0;
    }
    w = im.width;
    h = im.height;
  }
  Im (Im im){
    w = im.w;
    h = im.h;
    data = new int[w*h];
    for (int i = 0; i < w*h; i++){
      data[i] = im.data[i];
    }
  }
  int get(int x, int y){
    if (x < 0 || x >= w || y < 0 || y >= h){
      return 0;
    }
    return data[y*w+x];
  }
  int get(Pt p){
    return get(p.x,p.y);
  }
  boolean isOn(int x, int y){
    return get(x,y)>0;
  }
  boolean isOn(Pt p){
    return isOn(p.x,p.y);
  }
  void set(int x, int y, int v){
    data[y*w+x] = v;
  }
  void set(Pt p, int v){
    set(p.x,p.y,v);
  }
  PImage toPImage(){
    PImage im = createImage(w,h,RGB);
    im.loadPixels();
    for (int i = 0; i < data.length; i++){
      int g = (data[i]*127+128) & 255;
      im.pixels[i] = (g << 16) | (g << 8) | (g);
    }
    im.updatePixels();
    return im;
  }
  
}
  
Pt hiPt(Im im){
  for (int i = 0; i < im.h; i++){
    for (int j = 0; j < im.w; j++){
      if (im.isOn(j,i)){
        return new Pt(j,i);
      }
    }
  }
  return null;
}

Pt loPt(Im im){
  for (int i = im.h-1; i>=0; i--){
    for (int j = 0; j < im.w; j++){
      if (im.isOn(j,i)){
        return new Pt(j,i);
      }
    }
  }
  return null;
}
  
boolean floodfill(Im prev, Im src, Im dst, Pt p0, int[] areaOut){
  boolean t = false;
  if (prev.get(p0) == -1){
    t = true;
  }
  if (src.get(p0) != 1){
    return t;
  }
  if (dst.get(p0) == 1){
    return t;
  }
  dst.set(p0,1);
  src.set(p0,0);

  if (areaOut != null){
    areaOut[0]++;
  }
  
  t |= floodfill(prev,src,dst,new Pt(p0.x-1,p0.y),areaOut);
  t |= floodfill(prev,src,dst,new Pt(p0.x+1,p0.y),areaOut);
  t |= floodfill(prev,src,dst,new Pt(p0.x,p0.y-1),areaOut);
  t |= floodfill(prev,src,dst,new Pt(p0.x,p0.y+1),areaOut);
  
  return t;
}

int findArea(Im im){
  int a = 0;
  for (int i = 0; i < im.data.length; i++){
    if (im.data[i] > 0){
      a ++;
    }
  }
  return a;  
}

ArrayList<Pt> satinStitches(Im prevIm, Im im, Pt p0, int d){
  //im.toPImage().save(random(1)+".png");
  
  ArrayList<Pt> pts = new ArrayList<Pt>();
  ArrayList<Pt> walk = new ArrayList<Pt>();
  pts.add(new Pt(p0));
  im.set(p0,-1);
  
  Pt p = new Pt(p0);
  int belowOn = -1;
  int belowOff = -1;
  int aboveOn = -1;

  int lineStart = p.x;
  
  boolean invBranch = false;
  
  for (int i = 0; i < Integer.MAX_VALUE; i++){

    if (im.get(p.x,p.y) != 0){
      if (im.isOn(p.x,p.y+d)){
        if (belowOn < 0){
          belowOn = p.x;
          //fill(0,255,64);
          //rect(p.x/reso,p.y/reso,1/reso,1/reso);
          //save("???.png"); 
        }else if (belowOff >= 0 && !invBranch){
          Pt q0 = new Pt(belowOn,p.y+d);
          Im mask = new Im(im.w,im.h);
          
          Im old = new Im(im);

          int[] area = {0};
          boolean touch = floodfill(prevIm,im,mask,q0,area);
          int at = findArea(im);
          //println(touch,area[0],at);
          if (touch || area[0] > at){
            invBranch = true;
            im = old;
          }else{
            
            if (d > 0){
              q0 = loPt(mask);
            }else{
              q0 = hiPt(mask);
            }
            if (q0!=null){
              pts.addAll(satinStitches(im,mask,q0,-d));
            }
            //pts.addAll(satinStitches(im,new Pt(p),abs(d)));
            //break;
            
            belowOn = -1;
            belowOff = -1;
            
          }
        }
      }else{
        if (belowOn >= 0 && belowOff < 0){
          belowOff = p.x;
        }
      }
      
      if (im.isOn(p.x,p.y-d)){
        if (aboveOn < 0){
          aboveOn = p.x;
        }
      }else{
        if (aboveOn >= 0){
          Pt q0 = new Pt(aboveOn,p.y-d);
          Im mask = new Im(im.w,im.h);
          floodfill(prevIm,im,mask,q0,null);
          
          if (d > 0){
            q0 = hiPt(mask);
          }else{
            q0 = loPt(mask);
          }
          if (q0 != null){
            pts.addAll(satinStitches(im,mask,q0,d));
          }
          aboveOn = -1;
        }
      }
    }
    

    p.x += 1;
    if (!im.isOn(p)){
      int lineEnd = p.x;
      if (invBranch){
        Pt q0 = new Pt(p.x,p.y+d);
        while(!im.isOn(q0)){
          q0.x--;
        }
        Im mask = new Im(im.w,im.h);
        floodfill(prevIm,im,mask,q0,null);
        
        if (d > 0){
          q0 = loPt(mask);
        }else{
          q0 = hiPt(mask);
        }
        if (q0 != null){
          pts.addAll(satinStitches(im,mask,q0,-d));
        }
        p.x = belowOn;
        p.y += d;
        
        belowOn = -1;
        aboveOn = -1;
        belowOff = -1;
        invBranch = false;
        
        continue;
      }else if (aboveOn >= 0){
        Pt q0 = new Pt(p.x,p.y-d);
        while(!im.isOn(q0)){
          q0.x--;
        }
        Im mask = new Im(im.w,im.h);
        floodfill(prevIm,im,mask,q0,null);
        
        if (d > 0){
          q0 = hiPt(mask);
        }else{
          q0 = loPt(mask);
        }
        if (q0 != null){
          pts.addAll(satinStitches(im,mask,q0,d));
        }
        
        belowOn = -1;
        aboveOn = -1;
        belowOff = -1;
      
      }
      p.y += d;
      belowOn = -1;
      belowOff = -1;
      aboveOn = -1;
      if (p.y >= im.h || p.y < 0){
        break;
      }
      while (im.isOn(p)){
        p.x+=1;
      }
      boolean once = false;
      for (int j = 0; j < im.w; j++){
        p.x -= 1;
        boolean on = im.isOn(p);
        if (on && !once){
          once = true;
        }
        if (!on && once){
          
          walk.add(new Pt((lineStart+lineEnd)/2,p.y));
          //walk.add(new Pt(p.x+2,p.y));
          lineStart = p.x+1;
          invBranch = false;
          break;
        }
      }
    }else{
      pts.add(new Pt(p));
      im.set(p,-1);
    }
  }
  for (int i = 0; i < walk.size(); i++){
    pts.add(0,walk.get(i));
  }
  return pts;
}
 
 
void rotateAndFit(float[][] poly, float ang, float w, float h){
  float[] c = {w/2,h/2};
  float xmin = Float.MAX_VALUE;
  float ymin = Float.MAX_VALUE;
  float xmax = -Float.MAX_VALUE;
  float ymax = -Float.MAX_VALUE;
  
  for (int i = 0; i < poly.length; i++){
    float[] p = poly[i];
    float a0 = atan2(p[1]-c[1],p[0]-c[0]);
    float a = a0 + ang;
    float d = (float)Math.hypot(p[0]-c[0],p[1]-c[1]);
    poly[i][0] = c[0]+cos(a)*d;
    poly[i][1] = c[1]+sin(a)*d;
    xmin = min(xmin,poly[i][0]);
    ymin = min(ymin,poly[i][1]);
    xmax = max(xmax,poly[i][0]);
    ymax = max(ymax,poly[i][1]);
  }
  
  float dx = (w-(xmax-xmin))/2-xmin;
  float dy = (h-(ymax-ymin))/2-ymin;
  
  for (int i = 0; i < poly.length; i++){
    poly[i][0]+=dx;
    poly[i][1]+=dy;
  }
  
}
 
void remove1pxHoles(Im im){
  for (int i = 0; i < im.h; i++){
    for (int j = 0; j < im.w; j++){
      if (im.get(j,i) == 0){
        if (im.get(j-1,i) == 1
          &&im.get(j+1,i) == 1
          &&im.get(j,i-1) == 1
          &&im.get(j,i+1) == 1
        ){
          im.set(j,i,1);
        }
      }
    }
  }
  
}

void makeRaster(){
  PGraphics pg = createGraphics((int)(width*reso),(int)(height*reso));
  
  pg.beginDraw();
  pg.scale(reso);
  pg.background(0);
  pg.fill(255);
  pg.noStroke();
  pg.beginShape();
  for (int i=0; i<shapeCoordsData.length; i++) {
    float px = shapeCoordsData[i][0];
    float py = shapeCoordsData[i][1];
    pg.vertex(px, py);
  }
  pg.endShape();
  pg.endDraw();
  
  srcImg = new Im(pg);
  remove1pxHoles(srcImg);
  
  cpyImg = new Im(srcImg);
}
 
 
void setup(){
  size(800,800);
  
  for (int i = 0; i < shapeCoordsData.length; i++){
    shapeCoordsData[i][0] = (shapeCoordsData[i][0]-400)*0.9+400;
    shapeCoordsData[i][1] = (shapeCoordsData[i][1]-400)*0.9+400;
  }
  
  //rotateAndFit(shapeCoordsData,6.25f,width,height);
  
  //makeRaster();
  //stitches = satinStitches(cpyImg,srcImg,hiPt(srcImg),1);
}

int frame = 0;
float rot = 0;
void draw(){

  makeRaster();
  stitches = satinStitches(cpyImg,srcImg,hiPt(srcImg),1);
  
  background(100);
  noStroke();
  fill(255);
  
  for (int i = 0; i < cpyImg.h; i++){
    for (int j = 0; j < cpyImg.w; j++){
      if (cpyImg.get(j,i) != 0){
        rect(j/reso,i/reso,1/reso,1/reso);
      }
    }
  }
  
  noFill();
  stroke(255,64,0);
  beginShape();
  //for (int i = 0; i < stitches.size(); i++){
  for (int i = 0; i < min((frame*5),stitches.size()); i++){
    float x = stitches.get(i).x/reso+1/reso/2;
    float y = stitches.get(i).y/reso+1/reso/2;
    rect(x-1,y-1,3,3);
    vertex(x,y);
  }
  endShape();
  
  fill(0);
  text("LEFT/RIGHT rotate, UP/DOWN sample, SPACE restart anim, ENTER finish anim\n"
    +"Current rotation: "+nf(rot,1,2)+" rad",5,20);
  frame ++;
}

void keyPressed(){
  if (keyCode == LEFT){
    rotateAndFit(shapeCoordsData,-0.01f,width,height);
    rot -= 0.01;
  }else if (keyCode == RIGHT){
    rotateAndFit(shapeCoordsData,0.01f,width,height);
    rot += 0.01;
  }else if (key == ' '){
    frame = 0;
  }else if (keyCode == ENTER){
    frame = 99999;
  }else if (keyCode == UP){
    reso = min(0.25,reso*2);
  }else if (keyCode == DOWN){
    reso = max(0.03124,reso/2);
  }

}
