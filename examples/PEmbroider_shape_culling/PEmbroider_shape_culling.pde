// Test program for the PEmbroider library for Processing:
// Different methods for overlapping shapes with PEmbroider

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (1250, 575);
  
  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_shape_culling.vp3");
  E.setPath(outputFilePath);
 
  E.beginDraw(); 
  E.clear();
  E.fill(0, 0, 0); 
  
  E.CULL_SPACING = 7;
  E.hatchSpacing(4);
  E.strokeWeight(1); 
  E.setStitch(5,20,0); 
 
  //-----------------------
  // Two overlapping circles, with PARALLEL hatch
  E.hatchMode(PEmbroiderGraphics.PARALLEL); 
  E.hatchAngleDeg(45); 
  E.circle( 150, 150, 250);
  E.hatchAngleDeg(90); 
  E.circle(275, 150, 250);

  // One circle culling another, with PARALLEL hatch
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.beginCull();
  E.hatchAngleDeg(45); 
  E.circle( 150, 425, 250);
  E.hatchAngleDeg(90); 
  E.circle(275, 425, 250);
  E.endCull();

  //-----------------------
  // Two overlapping circles, with CONCENTRIC hatch
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.hatchAngleDeg(45); 
  E.circle(550, 150, 250);
  E.hatchAngleDeg(90); 
  E.circle(675, 150, 250);

  // One circle culling another, with CONCENTRIC hatch
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.beginCull();
  E.hatchAngleDeg(45); 
  E.circle(550, 425, 250);
  E.hatchAngleDeg(90); 
  E.circle(675, 425, 250);
  E.endCull();
  
  //-----------------------
  // Occluding circles with mixed hatch modes
  E.hatchAngle(radians(90));
  
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.circle(950, 150, 250);
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.circle(1100, 150, 250);
  
  E.beginCull();
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.circle(950, 425, 250);
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.circle(1100, 425, 250);
  E.endCull();

  //-----------------------
  // E.optimize(); // slow but good and important
  E.visualize(); //true, true, true);
  // E.endDraw(); // write out the file
  //save("PEmbroider_shape_culling.png");
}

