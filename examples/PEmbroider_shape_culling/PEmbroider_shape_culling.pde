// Simple test program for the PEmbroider library for Processing

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (500, 500);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_shape_culling.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  E.HATCH_SPARSITY = 6;
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 

  //-----------------------
  // Two overlapping circles, with PARALLEL hatch
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.HATCH_ANGLE = radians(45);
  E.circle( 50, 50, 120);
  E.HATCH_ANGLE = radians(90);
  E.circle(100, 50, 120);

  // One circle culling another, with PARALLEL hatch
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.beginCull();
  E.HATCH_ANGLE = radians(45);
  E.circle( 50, 200, 120);
  E.HATCH_ANGLE = radians(90);
  E.circle(110, 200, 120);
  E.endCull();

  //-----------------------
  // Two overlapping circles, with CONCENTRIC hatch
  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  E.HATCH_ANGLE = radians(45);
  E.circle(270, 50, 120);
  E.HATCH_ANGLE = radians(90);
  E.circle(330, 50, 120);

  // One circle culling another, with CONCENTRIC hatch
  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  E.beginCull();
  E.HATCH_ANGLE = radians(45);
  E.circle(270, 200, 120);
  E.HATCH_ANGLE = radians(90);
  E.circle(330, 200, 120);
  E.endCull();

  //-----------------------
  E.visualize();
  E.optimize(); // slow but good and important
  E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
