// Test program for the PEmbroider library for Processing:
// Filling an image with the experimental SPINE hatch

import processing.embroider.*;
PEmbroiderGraphics E;
PImage myImage;

void setup() {
  noLoop(); 
  size (600, 600);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_bitmap_image_2.vp3");
  E.setPath(outputFilePath); 

  // The image should consist of white shapes on a black background. 
  // The ideal image is an exclusively black-and-white .PNG or .GIF.
  myImage = loadImage("processing_logo.png");

  E.beginDraw(); 
  E.clear();
  
  // Use the Cull feature to make lines and strokes not overlap
  E.beginCull();
  
  // Draw it once, filled. 
  E.noStroke();
  E.fill(0, 0, 255); // Blue fill
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC); 
  E.hatchSpacing(4.0); 
  E.image(myImage, 0, 0); 
  
  // Draw it again, but just the stroke this time. 
  E.noFill();
  E.strokeWeight(30); 
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.strokeSpacing(3.0); 
  E.stroke(0, 0, 255); // Blue stroke
  E.image(myImage, 0, 0); 
  
  E.endCull();
  


  // Uncomment for the Alternative "Spine" rendering style:
  //PEmbroiderHatchSpine.setGraphics(E);
  //PEmbroiderHatchSpine.hatchSpineVF(myImage, 5);

  //-----------------------
  // E.optimize();   // slow, but good and important
  E.visualize();  // 
  E.printStats(); //
  // E.endDraw();    // write out the file
  // save("PEmbroider_bitmap_image_2.png");
}


//--------------------------------------------
void draw() {
  ;
}
