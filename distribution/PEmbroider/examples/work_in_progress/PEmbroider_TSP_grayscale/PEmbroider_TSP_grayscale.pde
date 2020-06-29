import processing.embroider.*;

PEmbroiderGraphics E;

void dot(float x, float y){
  E.beginRawStitches();
  E.rawStitch(x,y);
  E.rawStitch(x+1,y+1);
  E.endRawStitches();
}
void dot(PVector p){
  dot(p.x,p.y);
}

ArrayList<PVector> dither(PImage im){
  ArrayList<PVector> pts = new ArrayList<PVector>();
  float[] tmp = new float[im.width*im.height];
  im.loadPixels();
  for (int i= 0; i < im.height; i++) {
    for (int j= 0; j < im.width; j++) {
       float o = (im.pixels[i*im.width+j]&255) + tmp[i*im.width+j];
       int n = o > 128 ? 255 : 0;
       float qe = o - n;

       if(j<im.width -1){         tmp[ i   *im.width+ j+1] += qe * 7.0/16.0; }
       if(i<im.height-1){if(j!=0){tmp[(i+1)*im.width+ j-1] += qe * 3.0/16.0; }
                                  tmp[(i+1)*im.width+ j  ] += qe * 5.0/16.0; 
       if(j<im.width -1){         tmp[(i+1)*im.width+ j+1] += qe * 1.0/16.0; }}
       if (n == 0){
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
  PImage im = loadImage("cameraman.bmp");
  ArrayList<PVector> pts = dither(im);
  for (int i = 0; i < pts.size(); i++){
    dot(pts.get(i).copy().mult(4));

  }
  E.optimize();
  E.visualize(true,true,true);
  E.endDraw();
}

void draw(){
}
