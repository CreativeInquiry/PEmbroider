// Test program for the PEmbroider library for Processing:
// strokeMode(E.ANGLED);

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  //noLoop(); 
  size (1000, 400);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_stroke_outlines_3.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw();

  background(200); 
  E.clear();

  E.noFill();
  E.stroke(0, 0, 0); 
  E.strokeWeight(30); 
  E.strokeSpacing(5);
  E.setStitch(5, 20, 0);
  
  // These are the 3 options:
   E.strokeMode(E.PERPENDICULAR);
   //E.strokeMode(E.TANGENT);
  //E.strokeMode(E.ANGLED);

  float ay = 50; 
  int nCurves = 11;
  for (int i=0; i<nCurves; i++) {
    float ax = map(i, 0, (nCurves-1), 0+50, width-200-50);
    float sa = map(i, 0, (nCurves-1), 35, -35); 
    E.strokeAngleDeg(sa);

    E.beginShape();
    E.vertex(ax, ay);
    E.quadraticVertex(ax+050, ay+025, ax+100, ay+150);
    E.quadraticVertex(ax+150, ay+275, ax+200, ay+300);
    E.endShape();
  }


  //-----------------------
  // E.optimize(); // slow but good and important
  //E.visualize();//true, false, false);
  // E.endDraw(); // write out the file
  // save("PEmbroider_stroke_outlines_3.png");
}
void draw(){
  background(255);
  E.visualize(true,true,false,frameCount);
}
