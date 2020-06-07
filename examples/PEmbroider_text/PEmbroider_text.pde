// Test program for the PEmbroider library for Processing:
// Basic shapes

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (600, 400);
  PFont myFont = createFont("Helvetica-Bold", 100);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_text.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();

  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.hatchSpacing(4);
  E.strokeSpacing(4);
  
  E.textFont(myFont);
  E.fill(0);
  E.strokeWeight(12);
  E.textSize(250);
  E.text("ABC", 0, 0);

  //-----------------------
  E.visualize();
  //E.optimize(); // slow, but very good and important
  //E.endDraw(); // write out the file
}
