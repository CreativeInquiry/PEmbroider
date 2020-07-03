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
  size (700, 475);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_shape_hatching_5.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 
  E.noStroke(); 
  E.setStitch(3,40,0);
  
  E.hatchMode(SATIN);
  
  
  //-----------------------
  //All these shapes are filled with a SATIN hatch
  
  //the setStitch function behaves differently 
  //with SATIN and SPIRAL fills as stitch placement
  //is sample based.
  //Parameters operate as though:
  //setStitch(desiredStitchLength, maxStitchLength, noise);
  E.setStitch(10,20,0.5);
  E.hatchSpacing(3);
  E.circle( 125, 125, 200);

  E.setStitch(40,50,0.5);
  E.hatchSpacing(3);
  E.circle(350, 125, 200);

  E.setStitch(100,100,0.75);
  E.hatchAngle(radians(90));
  E.hatchSpacing(4);
  E.circle(575, 125, 200);


  E.hatchSpacing(3);
  E.setStitch(15,50,0.5);
  E.arc( 125, 350, 200,200, 0, PI*1.75, PIE);

  
  E.setStitch(60,60,0.8);
  E.hatchSpacing(2.5);
  E.rect(250, 250, 200, 200);

  E.setStitch(30,50,0.95);
  E.hatchAngle(radians(14));
  E.hatchSpacing(2);
  E.rect (475, 250, 200, 200);

  


  //-----------------------
  //E.optimize(); // slow but good and important
  E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_shape_hatching_4.png");
}


//--------------------------------------------
void draw() {
  ;
}
