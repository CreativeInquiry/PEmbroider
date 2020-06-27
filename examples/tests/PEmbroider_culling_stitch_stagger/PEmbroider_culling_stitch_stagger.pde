// Test program for the PEmbroider library for Processing:
// Different methods for overlapping shapes with PEmbroider

import processing.embroider.*;
PEmbroiderGraphics E;
PEmbroiderBooleanShapeGraphics BSG;

void setup() {
  noLoop(); 
  size (1250, 300);
  
  E = new PEmbroiderGraphics(this, width, height);
  BSG = new PEmbroiderBooleanShapeGraphics(width, height);
  String outputFilePath = sketchPath("PEmbroider_shape_culling.vp3");
  E.setPath(outputFilePath);
 
  E.beginDraw(); 
  E.clear();
  E.fill(0, 0, 0); 
  
  E.CULL_SPACING = 6;
  E.hatchSpacing(4.0);
  E.strokeWeight(1); 
  E.setStitch(10,20,0); 
  E.ellipseMode(CENTER);
 
  // One circle culling another, with PARALLEL hatch
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.beginCull();
  E.hatchAngleDeg(45); 
  E.circle(225, 150, 250);
  E.hatchAngleDeg(90); 
  E.circle(350, 150, 250);
  E.endCull();

  BSG.clear();
  BSG.ellipseMode(CENTER);
  BSG.circle(225, 150, 250);
  BSG.operator(PEmbroiderBooleanShapeGraphics.DIFFERENCE);
  BSG.circle(350, 150, 250);
  E.hatchAngleDeg(45); 
  E.image(BSG,500,0);
  E.hatchAngleDeg(90); 
  E.circle(850, 150, 250);
  

  //-----------------------
  // E.optimize(); // slow but good and important
  E.visualize(); //true, true, true);
  // E.endDraw(); // write out the file
  // save("PEmbroider_shape_culling.png");
}
