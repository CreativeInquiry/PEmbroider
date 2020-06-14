// Test program for the PEmbroider library for Processing:
// Different methods for overlapping shapes with PEmbroider

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (730, 370);
  
  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_shape_culling.vp3");
  E.setPath(outputFilePath);
 
  E.beginDraw(); 
  E.clear();
  E.fill(0, 0, 0); 
  
  E.hatchSpacing(6);
  E.strokeWeight(1); 
  E.stitchLength( 16.20); 
 
  //-----------------------
  // Two overlapping circles, with PARALLEL hatch
  E.hatchMode(PEmbroiderGraphics.PARALLEL); 
  E.hatchAngleDeg(45); 
  E.circle( 50, 50, 120);
  E.hatchAngleDeg(90); 
  E.circle(100, 50, 120);

  // One circle culling another, with PARALLEL hatch
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.beginCull();
  E.hatchAngleDeg(45); 
  E.circle( 50, 200, 120);
  E.hatchAngleDeg(90); 
  E.circle(110, 200, 120);
  E.endCull();

  //-----------------------
  // Two overlapping circles, with CONCENTRIC hatch
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.hatchAngleDeg(45); 
  E.circle(270, 50, 120);
  E.hatchAngleDeg(90); 
  E.circle(330, 50, 120);

  // One circle culling another, with CONCENTRIC hatch
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.beginCull();
  E.hatchAngleDeg(45); 
  E.circle(270, 200, 120);
  E.hatchAngleDeg(90); 
  E.circle(330, 200, 120);
  E.endCull();
  
  //-----------------------
  // Occluding circles with mixed hatch modes
  E.hatchAngle(radians(90));
  
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.circle(500, 50, 120);
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.circle(560, 50, 120);
  
  E.beginCull();
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.circle(500, 200, 120);
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.circle(560, 200, 120);
  E.endCull();

  //-----------------------
  E.optimize(); // slow but good and important
  E.visualize(true, true, true);
  // E.endDraw(); // write out the file
}


//--------------------------------------------
void draw2() {
  ;
}
