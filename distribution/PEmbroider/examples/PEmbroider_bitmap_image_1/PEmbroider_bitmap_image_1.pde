// Test program for the PEmbroider library for Processing:
// Filling a pre-loaded "image" using various hatching methods.

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  size(1000, 500); 
  noLoop(); 

  // Load the bitmap image to embroider. 
  // Should consist of white shapes on a black background. 
  PImage myImage = loadImage("broken_heart.png"); 

  // Create and configure the PEmbroider object, E
  E = new PEmbroiderGraphics(this, 500, 500);
  String outputFilePath = sketchPath("PEmbroider_bitmap_image_1.vp3");
  E.setPath(outputFilePath); 

  //-------------------
  // Initial setup of PEmbroider object.
  E.beginDraw(); 
  E.clear();
  E.fill(0, 0, 0); 
  E.noStroke();

  //-------------------
  // Parallel hatch 
  E.setStitch(5, 30, 0); 
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.hatchAngleDeg(15);
  E.hatchSpacing(3.0);
  E.image(myImage, 0, 0);

  //-------------------
  // Cross hatch 
  E.setStitch(5, 30, 0);
  E.hatchMode(PEmbroiderGraphics.CROSS); 
  E.HATCH_ANGLE = radians(30);
  E.HATCH_ANGLE2 = radians(0);
  E.hatchSpacing(4.0); 
  E.image(myImage, 250, 0);

  //-------------------
  // Dense concentric hatch 
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC); 
  E.hatchSpacing(2.0);
  E.setStitch(5, 20, 1.0);
  E.image(myImage, 0, 250);
  
  //-------------------
  // Sparse concentric hatch
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC); 
  E.hatchSpacing(7.5);
  E.image(myImage, 250, 250);

  //-------------------
  // Draw fat perpendicular stroke only, no fill. 
  E.noFill(); 
  E.stroke(0, 0, 0); 
  E.setStitch(5, 30, 1.0);
  E.strokeWeight(16); 
  E.strokeSpacing(4);
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.image(myImage, 500, 250);
  
  //-------------------
  // Draw fat parallel stroke only; no fill. 
  E.stroke(0, 0, 0); 
  E.noFill(); 
  E.strokeWeight(16); 
  E.setStitch(5, 30, 1.0);
  E.strokeMode(PEmbroiderGraphics.TANGENT);
  E.strokeSpacing(4);
  E.image(myImage, 750, 250);
  
  //-------------------
  // Draw "spine" hatch (experimental), which is 
  // based on distance transform & skeletonization.
  // Here, we use the (best) "vector field" (VF) version.
  E.pushMatrix();
  E.translate(500, 0);
  E.setStitch(5, 25, 0.0); 
  PEmbroiderHatchSpine.setGraphics(E);
  PEmbroiderHatchSpine.hatchSpineVF(myImage, 3.0); 
  E.popMatrix(); 

  //-------------------
  // Draw the original raster image (for reference).
  image(myImage, 750, 0);

  //-------------------
  // Be sure to un-comment E.optimize() and E.endDraw() below
  // when you want to actually export the embroidery file!!!
  // 
  E.optimize();    // really slow, but good and important
  E.visualize();
  // E.endDraw();  // write out the embroidery file
  E.printStats();  // tell us about it
}
