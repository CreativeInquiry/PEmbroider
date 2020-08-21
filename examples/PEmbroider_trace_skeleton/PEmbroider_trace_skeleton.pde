// Test program for the PEmbroider library for Processing:
// Trace the "skeleton" (medial axis) from a scan of a line  
// drawing, using Lingdong Huang's "TraceSkeleton.java" code
// from https://github.com/LingDong-/skeleton-tracing. 
// This produces a single-stroke vector version of the drawing.
// The drawing should have white lines on a black background.

import traceskeleton.*;
import processing.embroider.*;
PEmbroiderGraphics E;

//--------------------------------------
void setup() {
  size (1200, 700);
  noLoop();
  E = new PEmbroiderGraphics(this);

  PImage img = loadImage("blobfish.png");
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

  // Fetch every vertex from the arrays produced by the tracer;
  // Add them to some PEmbroider shapes. 
  for (int i = 0; i < c.size(); i++) {
    E.beginShape();
    for (int j = 0; j < c.get(i).size(); j++) {
      E.vertex(c.get(i).get(j)[0], c.get(i).get(j)[1]);
    }
    E.endShape();
  }
  
  // Optimize the shapes and display them. 
  E.optimize();
  E.visualize(false, true, true);
}

//--------------------------------------
void draw() {
  ;
}
