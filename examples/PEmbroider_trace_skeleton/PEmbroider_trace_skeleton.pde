// Test program for the PEmbroider library for Processing:
// trace skeleton

import traceskeleton.*;
import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  size (1200,700);

  E = new PEmbroiderGraphics(this);
  
  PImage img = loadImage("blobfish.png");
  
  boolean[] im = new boolean[img.width*img.height];
  img.loadPixels();
  for (int i = 0; i < im.length; i++){
    im[i] = (img.pixels[i]>>16&0xFF)>128;
  }
  ArrayList<ArrayList<int[]>>  c;

  int W = img.width;
  int H = img.height;

  TraceSkeleton.thinningZS(im,W,H);
  c = TraceSkeleton.traceSkeleton(im,W,H,0,0,W,H,10,999,null);

  for (int i = 0; i < c.size(); i++){
    E.beginShape();
    for (int j = 0; j < c.get(i).size(); j++){
      E.vertex(c.get(i).get(j)[0],c.get(i).get(j)[1]);
    }
    E.endShape();
  }
  E.optimize();
  E.visualize(false,true,true);
}

void draw(){

}
