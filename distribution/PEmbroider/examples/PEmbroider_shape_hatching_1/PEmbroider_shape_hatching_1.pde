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

  String outputFilePath = sketchPath("PEmbroider_shape_hatching_1.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 
  E.noStroke(); 
  E.setStitch(3,40,0);
  
  //-----------------------
  E.hatchMode(PARALLEL);
  E.hatchAngleDeg(45);
  E.hatchSpacing(4);
  E.circle( 25, 25, 200);

  E.hatchMode(PARALLEL);
  E.hatchAngle(radians(90));
  E.hatchSpacing(4);
  E.circle(250, 25, 200);

  E.hatchMode(PARALLEL);
  E.hatchAngle(radians(90));
  E.hatchSpacing(8);
  E.circle(475, 25, 200);


  //-----------------------
  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(3);
  E.circle( 25, 250, 200);
  
  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(4);
  E.rect(250, 250, 200, 200);

  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(8);
  E.rect (475, 250, 200, 200);


  //-----------------------
  //Not effective close together
  E.hatchMode(SPIRAL);
  E.hatchSpacing(15);
  E.circle( 25, 475, 200);

  E.hatchMode(SPIRAL);
  E.hatchSpacing(8);
  E.circle(250,475, 200);

  E.hatchMode(SPIRAL);
  E.hatchSpacing(8);
  E.rect(475, 475, 200, 200);


  //-----------------------
  //E.optimize(); // slow but good and important
  E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_shape_hatching_1.png");
}


//--------------------------------------------
void draw() {
  ;
}
