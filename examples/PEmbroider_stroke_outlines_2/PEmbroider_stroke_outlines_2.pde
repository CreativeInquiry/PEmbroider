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

  String outputFilePath = sketchPath("PEmbroider_stroke_outlines_2.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();

  // ------------------------

  E.noStroke();
  //fill settings
  E.fill(0,0,0);
  E.hatchMode(E.SPIRAL);
  E.setStitch(5,20,0); 
  //cull settings
  E.CULL_SPACING = 25;
  
  E.beginCull();
  E.circle(320,250,200);
  E.circle(200,350,150);
  E.circle(200,200,250);
  E.endCull();
    
  E.noFill();
  //stroke settings
  E.strokeMode(E.TANGENT);
  //E.stroke(0);  //when i comment this out it works fine
  E.strokeWeight(10);
  E.strokeLocation(OUTSIDE);
  E.strokeSpacing(3);
  
  E.beginComposite();
  E.circle(320,250,200);
  E.circle(200,350,150);
  E.circle(200,200,250);
  E.endComposite(); 
  
  
  //-----------------------
  //E.optimize(); // slow but good and important
  E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_stroke_outlines_2.png");
}
