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
  size (1150, 475);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_stroke_outlines.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();

  
  //-----------------------
  //stroke properties
   
  E.strokeMode(E.TANGENT);
  E.strokeWeight(15);
  E.strokeSpacing(3);
  E.noFill();
  //E.stroke
  
  //Outline is positioned outside the bounds of the shapes
  //E.strokeLocation(1);
  E.strokeLocation(OUTSIDE); //same as above
    
  E.beginComposite();
  E.composite.rect(125,175,200,200);
  E.composite.circle(125,225,150);
  E.composite.triangle(375,250,250, 60,150, 250);
  E.endComposite();
   
  //Outline is centered the bounds of the shapes
  //E.strokeLocation(0);
  E.strokeLocation(CENTER);  //same as above
    
  E.beginComposite();
  E.composite.rect(475,175,200,200);
  E.composite.circle(475,225,150);
  E.composite.triangle(725,250,600, 60,500, 250);
  E.endComposite();    
  
  //Outline is positioned inside the bounds of the shapes
  //E.strokeLocation(-1);
  E.strokeLocation(INSIDE);  //same as above

  E.beginComposite();
  E.composite.rect(825,175,200,200);
  E.composite.circle(825,225,150);
  E.composite.triangle(1075,250,950, 60,850, 250);
  E.endComposite();    
    
    
  //-------------------------------------
  //All shapes drawn on the canvas (not rendered into the embroidery)
  //this lets you see the differences
  noFill();
  strokeWeight(3);
  stroke(255,125,200);
  
  rect(125,175,200,200);
  circle(125,225,150);
  triangle(375,250,250, 60,150, 250);
  rect(475,175,200,200);
  circle(475,225,150);
  triangle(725,250,600, 60,500, 250);
  rect(825,175,200,200);
  circle(825,225,150);
  triangle(1075,250,950, 60,850, 250);
  
    
    
    
    
  //-----------------------
  //E.optimize(); // slow but good and important
  E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_stroke_outlines.png");
}
