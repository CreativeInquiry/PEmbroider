import processing.embroider.*;

PEmbroiderGraphics E;

class CMYK{
  float c;
  float m;
  float y;
  float k;
  CMYK(float _c,float _m,float _y,float _k){
    c = _c;
    m = _m;
    y = _y;
    k = _k;
  }
}

CMYK toCMYK(int rgb){
  //https://stackoverflow.com/questions/2426432/convert-rgb-color-to-cmyk
  float Red = (float)((rgb>>16) & 255)/255f;
  float Green = (float)((rgb>>8) & 255)/255f;
  float Blue = (float)((rgb) & 255)/255f;
  float Black   = min(1-Red,1-Green,1-Blue);
  if (Black == 1){//NaN!
    Black = 0.9999;
  }
  float Cyan    = (1-Red-Black)/(1-Black);
  float Magenta = (1-Green-Black)/(1-Black);
  float Yellow  = (1-Blue-Black)/(1-Black);
  return new CMYK(Cyan,Magenta,Yellow,Black);
}


void dot(float x, float y){
  E.beginRawStitches();
  E.rawStitch(x,y);
  E.rawStitch(x+1,y+1);
  E.endRawStitches();
}
void dot(PVector p){
  dot(p.x,p.y);
}

// Floyd Steinberg dither
ArrayList<PVector> dither(float[] im, int w, int h){
  ArrayList<PVector> pts = new ArrayList<PVector>();
  float[] tmp = new float[w*h];
  for (int i= 0; i < h; i++) {
    for (int j= 0; j < w; j++) {
       
       float o = im[i*w+j] + tmp[i*w+j];
       float n = o > 0.5 ? 1 : 0;
       float qe = o - n;

       if(j<w -1){         tmp[ i   *w+ j+1] += qe * 7.0/16.0; }
       if(i<h -1){if(j!=0){tmp[(i+1)*w+ j-1] += qe * 3.0/16.0; }
                           tmp[(i+1)*w+ j  ] += qe * 5.0/16.0; 
       if(j<w -1){         tmp[(i+1)*w+ j+1] += qe * 1.0/16.0; }}
       if (n > 0.5){
         pts.add(new PVector(j,i));
       }
     }
  }
  return pts;
}

void setup(){
  size(1024,1024);
  smooth();
  E = new PEmbroiderGraphics(this);
  E.setPath(sketchPath("tsp-painting.vp3"));
  PImage im = loadImage("parrot.png");
  
  im.loadPixels();
  float[] C = new float[im.width*im.height];
  float[] M = new float[im.width*im.height];
  float[] Y = new float[im.width*im.height];
  float[] K = new float[im.width*im.height];
  for (int i = 0; i < im.width*im.height; i++){
    CMYK cmyk = toCMYK(im.pixels[i]);
    C[i] = cmyk.c;
    M[i] = cmyk.m;
    Y[i] = cmyk.y*0.25;
    K[i] = cmyk.k*0.7;
    // each channel is given a multiplier because upper layers tend to occulde lower layers too much
    // the values are empirical by looking at visualization software. TODO: derive better values from real hardware
  }
  
  ArrayList<PVector> pC = dither(C,im.width,im.height);
  ArrayList<PVector> pM = dither(M,im.width,im.height);
  ArrayList<PVector> pY = dither(Y,im.width,im.height);
  ArrayList<PVector> pK = dither(K,im.width,im.height);
  

  E.stroke(0,255,255);
  for (int i = 0; i < pC.size(); i++){
    dot(pC.get(i).copy().mult(4).add(2,2));
  }
  E.stroke(255,0,255);
  for (int i = 0; i < pM.size(); i++){
    dot(pM.get(i).copy().mult(4).add(2,0));
  }
  E.stroke(255,255,0);
  for (int i = 0; i < pY.size(); i++){
    dot(pY.get(i).copy().mult(4).add(0,2));
  }
  E.stroke(0,0,0);
  for (int i = 0; i < pK.size(); i++){
    dot(pK.get(i).copy().mult(4));
  }
  
  E.optimize(1,999);
  E.visualize(true,false,false);
  E.endDraw();
}

void draw(){
}
