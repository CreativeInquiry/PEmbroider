// Test program for the PEmbroider library for Processing:
// Filling an image

import processing.embroider.*;
// import static processing.embroider.PEmbroiderGraphics.*;

PEmbroiderGraphics E;
PGraphics PG;

void setup() {
  size(1000, 500); 
  noLoop(); 

  // Load image to embroider
  PImage myImage = loadImage("broken_heart.png"); 

  // Create PEmbroider object, E
  E = new PEmbroiderGraphics(this, 500, 500);
  String outputFilePath = sketchPath("PEmbroider_bitmap_image_1.vp3");
  E.setPath(outputFilePath); 

  // Initial setup of PEmbroider object.
  E.beginDraw(); 
  E.clear();
  E.fill(0, 0, 0); 
  E.noStroke();
  E.setStitch(2, 10, 0); 

  // Parallel hatch 
  E.setStitch(2, 20, 0); 
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.5; // not working yet ..?
  //E.EXPERIMENTAL_PARALLEL_RESAMPLE = true;
  E.hatchAngleDeg(15);
  E.hatchSpacing(3.0);
  E.image(myImage, 0, 0);

  // Cross hatch 
  E.hatchMode(PEmbroiderGraphics.CROSS); 
  E.HATCH_ANGLE = radians(30);
  E.HATCH_ANGLE2 = radians(0); 
  E.HATCH_SPACING = 4;
  E.image(myImage, 250, 0);

  // Dense concentric hatch 
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC); 
  E.hatchSpacing(2.0);
  E.setStitch(2, 10, 1.0);
  E.image(myImage, 0, 250);

  // Loose concentric hatch
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC); 
  E.hatchSpacing(7.5);
  E.image(myImage, 250, 250);
  E.image(myImage, 250, 250);

  // Draw fat perpendicular stroke only, no fill. 
  E.stroke(0, 0, 0); 
  E.noFill(); 
  E.strokeWeight(16); 
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.strokeSpacing(4);
  E.image(myImage, 500, 250);
  
  // Draw fat parallel stroke only, no fill. 
  E.stroke(0, 0, 0); 
  E.noFill(); 
  E.strokeWeight(16); 
  E.strokeMode(PEmbroiderGraphics.TANGENT);
  E.strokeSpacing(4);
  E.image(myImage, 750, 250);
  
  // Draw spine hatch (experimental)
  E.pushMatrix();
  E.translate(500, 0);
  PEmbroiderHatchSpine.setGraphics(E);
  PEmbroiderHatchSpine.hatchSpine(myImage, 3);
  E.popMatrix(); 


  // Draw the raster image (for reference)
  image(myImage, 750, 0);

  //-----------------------
  // E.optimize(); // slow, but good and important
  E.visualize();
  // E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
