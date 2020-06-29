// Verify scaling of hatches and stitches, discussed in issue #30:
// https://github.com/CreativeInquiry/PEmbroider/issues/30

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (1000, 500);
  float cx = width/2; 
  float cy = height/2 - 40;

  //-------------------------
  E = new PEmbroiderGraphics(this, width, height);
  E.beginDraw(); 
  E.clear();  
  E.CIRCLE_DETAIL = 60;
  E.CONCENTRIC_ANTIALIGN = 0;
  E.ellipseMode(CENTER);
  E.rectMode(CENTER);
  E.fill(0, 0, 0); 
  E.stroke(0, 0, 0); 
  E.strokeWeight(1); 
  E.setStitch(10, 50, 0);
  E.hatchSpacing(5.0); 
  E.hatchAngleDeg(45); 

  //-------------------------
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);

  E.pushMatrix(); 
  E.translate(200, 100); 
  E.ellipse(0, 0, 100, 100); 
  E.popMatrix(); 

  E.pushMatrix(); 
  E.translate(200, 300); 
  E.scale(1.5, 2.5);
  E.ellipse(0, 0, 100, 100); 
  E.popMatrix(); 
  
  //-------------------------
  E.hatchMode(PEmbroiderGraphics.PARALLEL);

  E.pushMatrix(); 
  E.translate(400, 100); 
  E.ellipse(0, 0, 100, 100); 
  E.popMatrix(); 

  E.pushMatrix(); 
  E.translate(400, 300); 
  E.scale(1.5, 2.5);
  E.ellipse(0, 0, 100, 100); 
  E.popMatrix(); 
  
  //-------------------------
  E.hatchMode(PEmbroiderGraphics.SPIRAL);

  E.pushMatrix(); 
  E.translate(600, 100); 
  E.ellipse(0, 0, 100, 100); 
  E.popMatrix(); 

  E.pushMatrix(); 
  E.translate(600, 300); 
  E.scale(1.5, 2.5);
  E.ellipse(0, 0, 100, 100); 
  E.popMatrix(); 
  
  //-------------------------
  E.hatchMode(PEmbroiderGraphics.SATIN);

  E.pushMatrix(); 
  E.translate(800, 100); 
  E.ellipse(0, 0, 100, 100); 
  E.popMatrix(); 

  E.pushMatrix(); 
  E.translate(800, 300); 
  E.scale(1.5, 2.5);
  E.ellipse(0, 0, 100, 100); 
  E.popMatrix(); 
  

  //-------------------------
  // E.optimize(); 
  E.visualize();
}
