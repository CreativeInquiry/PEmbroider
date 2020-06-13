// Test program for the PEmbroider library for Processing:
// Filling an image with the experimental SPINE hatch

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  noLoop(); 
  size (500, 500);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_svg_image.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  E.fill(0, 0, 0); 
  E.noStroke();   
  E.setStitch(2, 10, 0);
  
  PImage myImg = loadImage("matisse.png");
  PEmbroiderHatchSpine.setGraphics(E);
  PEmbroiderHatchSpine.hatchSpine(myImg,3);

  //-----------------------
  // E.optimize(); // slow, but good and important
  E.visualize();
  // E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
