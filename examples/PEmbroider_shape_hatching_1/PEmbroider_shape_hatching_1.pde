// Test program for the PEmbroider library for Processing:
// Different methods for filling (hatching) shapes with PEmbroider: 
// * PEmbroiderGraphics.PARALLEL
// * PEmbroiderGraphics.CONCENTRIC
// * PEmbroiderGraphics.SPIRAL


import processing.embroider.*;
import static processing.embroider.PEmbroiderGraphics.*; // PEmbroiderGraphics.PARALLEL -> PARALLEL

PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (700, 700);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_shape_hatching_1.pes");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  E.fill(0, 0, 0); 
  E.noStroke(); 
  E.setStitch(10, 25, 0); 


  //-----------------------
  E.hatchMode(PARALLEL);
  E.hatchAngleDeg(45);
  E.hatchSpacing(3);
  E.circle( 75, 75, 150);

  E.hatchMode(PARALLEL);
  E.hatchAngle(radians(90));
  E.hatchSpacing(3);
  E.circle(275, 75, 150);

  E.hatchMode(PARALLEL);
  E.hatchAngle(radians(90));
  E.hatchSpacing(8);
  E.circle(475, 75, 150);


  //-----------------------
  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(3);
  E.circle( 75, 275, 150);

  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(3);
  E.rect(275, 275, 150, 150);

  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(8);
  E.rect (475, 275, 150, 150);
  stroke(0,0,0);

  //-----------------------
  E.hatchMode(SPIRAL);
  E.hatchSpacing(3);
  E.circle( 75, 475, 150);

  E.hatchMode(SPIRAL);
  E.hatchSpacing(8);
  E.circle(275, 475, 150);

  E.hatchMode(SPIRAL);
  E.hatchSpacing(8);
  E.rect(475, 475, 150, 150);


  //-----------------------
  // E.optimize(); // slow but good and important
  E.visualize();
  // E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
