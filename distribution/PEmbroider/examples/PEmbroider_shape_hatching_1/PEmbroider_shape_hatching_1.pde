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
  size (500, 500);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_shape_hatching_1.pes");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 
  E.noStroke(); 

  //E.toggleResample(false);
  //E.RESAMPLE_NOISE = 1;
  
  //-----------------------
  E.hatchMode(PARALLEL);
  E.hatchAngleDeg(45);
  E.hatchSpacing(4);
  E.circle( 50, 50, 100);

  E.hatchMode(PARALLEL);
  E.hatchAngle(radians(90));
  E.hatchSpacing(4);
  E.circle(200, 50, 100);

  E.hatchMode(PARALLEL);
  E.hatchAngle(radians(90));
  E.hatchSpacing(8);
  E.circle(350, 50, 100);


  //-----------------------
  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(4);
  E.circle( 50, 200, 100);

  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(4);
  E.rect(200, 200, 100, 100);

  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(8);
  E.rect (350, 200, 100, 100);


  //-----------------------
  E.hatchMode(SPIRAL);
  E.hatchSpacing(8);
  E.circle( 50, 350, 100);

  E.hatchMode(SPIRAL);
  E.hatchSpacing(8);
  E.circle(200, 350, 100);

  E.hatchMode(SPIRAL);
  E.hatchSpacing(8);
  E.rect(350, 350, 100, 100);


  //-----------------------
  // E.optimize(); // slow but good and important
  E.visualize();
  // E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
