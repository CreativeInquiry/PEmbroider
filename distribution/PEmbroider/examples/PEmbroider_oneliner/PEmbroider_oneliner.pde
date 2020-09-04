// Test program for the PEmbroider library for Processing:
// Trace the "skeleton" (medial axis) from a scan of a line  
// drawing, using Lingdong Huang's "TraceSkeleton.java" code
// from https://github.com/LingDong-/skeleton-tracing. 
// This produces a single-stroke vector version of the drawing.
// The drawing should have white lines on a black background.

import traceskeleton.*;
import onelinergraph.*;

import processing.embroider.*;
PEmbroiderGraphics E;

ArrayList<ArrayList<int[]>> lines;
OneLinerGraph OLG;
PImage img;

int numFrames = 0;

//--------------------------------------
void setup() {
  size (1200, 700);
  //noLoop();
  E = new PEmbroiderGraphics(this);

  img = loadImage("blobfish.png");
  int W = img.width;
  int H = img.height;
  boolean[] im = new boolean[W*H];
  img.loadPixels();
  for (int i=0; i<im.length; i++) {
    im[i] = (img.pixels[i]>>16 & 0xFF)>128;
  }
  
  // Trace the skeletons in the pixels.
  ArrayList<ArrayList<int[]>>  c;
  TraceSkeleton.thinningZS(im, W, H);
  c = TraceSkeleton.traceSkeleton(im, W, H, 0, 0, W, H, 10, 999, null);

  OLG = new OneLinerGraph(c);
  lines = OLG.multiSolve();


  for (int i = 0; i < lines.size(); i++) {
    E.beginShape();
    for (int j = 0; j < lines.get(i).size(); j++) {
      E.vertex(lines.get(i).get(j)[0], lines.get(i).get(j)[1]);
    }
    E.endShape();
  }
  
  // Optimize the shapes and display them. 
  E.optimize();

}

//--------------------------------------
void draw() {
  float scl = min(height/img.height,width/img.width);
  background(255);
  
  pushMatrix();
  strokeWeight(20/scl);
  scale(scl);

  for (int i = 0; i < lines.size(); i++){
    int n = min(numFrames,lines.get(i).size()-2);
    ArrayList<int[]> p = lines.get(i);
    
    for (int j = 0; j < n; j++){
      float t = constrain((float)(n-1-j),0,16)/16.0;
      stroke(lerpColor(color(255,0,0),color(128,0,255),t));
      line(p.get(j)[0],p.get(j)[1],p.get(j+1)[0],p.get(j+1)[1]);
      
    }
  }
  E.visualize();
  popMatrix();
  
  fill(0);
  text("press a key to restart",10,10);
  numFrames++;
}

void keyPressed(){
  numFrames = 0;
}
