// Test program for the PEmbroider library for Processing:
// strokeMode(E.ANGLED);

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (1400, 500);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_stroke_outlines_3.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw();

  background(200); 
  E.clear();

  E.noFill();
  E.stroke(0, 0, 0); 
  E.strokeWeight(44); 
  E.strokeSpacing(2.0);
  E.setStitch(5, 66, 0);
  E.PERPENDICULAR_STROKE_CAP_DENSITY_MULTIPLIER = 0.4;
  
  // These are the 3 options:
   //E.strokeMode(E.PERPENDICULAR);
  // E.strokeMode(E.TANGENT);
  E.strokeMode(E.ANGLED);

  float ay = 50; 
  int nCurves = 11;
  for (int i=0; i<nCurves; i++) {
    float ax = map(i, 0, (nCurves-1), 0+50, width-200-50);
    float sa = map(i, 0, (nCurves-1), 30, -30); 
    println(sa);
    E.strokeAngleDeg(sa);

    E.beginShape();
    E.vertex(ax, ay);
    E.quadraticVertex(ax+075, ay+025, ax+100, ay+200);
    E.quadraticVertex(ax+125, ay+375, ax+200, ay+400);
    E.endShape();
  }

  //-----------------------
  //E.optimize(); // slow but good and important
  E.visualize(true,false,false);//true, false, false);
  //E.endDraw(); // write out the file
  //save("PEmbroider_stroke_outlines_3.png");
}
