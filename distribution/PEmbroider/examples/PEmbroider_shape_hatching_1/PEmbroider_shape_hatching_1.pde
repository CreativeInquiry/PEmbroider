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
  //Shapes filled with PARALLEL hatch mode
  E.hatchMode(PARALLEL);
  E.hatchAngleDeg(45);
  E.hatchSpacing(4);
  E.circle( 125, 125, 200);

  E.hatchMode(PARALLEL);
  E.hatchAngle(radians(90));
  E.hatchSpacing(4);
  E.circle(350, 125, 200);

  E.hatchMode(PARALLEL);
  E.hatchAngle(radians(90));
  E.hatchSpacing(8);
  E.circle(575, 125, 200);


  //-----------------------
  //Shapes filled with CONCENTRIC hatch mode
  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(3);
  E.circle( 125, 350, 200);
  
  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(4);
  E.rect(250, 250, 200, 200);

  E.hatchMode(CONCENTRIC);
  E.hatchSpacing(8);
  E.rect (475, 250, 200, 200);


  //-----------------------
  //shapes filled with SPIRAL hatch mode
  
  //for SPIRAL 
  
  E.hatchMode(SPIRAL);
  E.hatchSpacing(15);
  E.circle( 125, 575, 200);

  E.hatchMode(SPIRAL);
  E.hatchSpacing(8);
  E.circle(350,575, 200);

  E.hatchMode(SPIRAL);
  E.hatchSpacing(8);
  E.rect(475, 475, 200, 200);
  
  //-----------------------
  //shapes filled with SATIN hatch mode
  E.hatchMode(SPIRAL);
  E.hatchSpacing(15);
  E.circle( 125, 575, 200);

  E.hatchMode(SPIRAL);
  E.hatchSpacing(8);
  E.circle(350,575, 200);

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
