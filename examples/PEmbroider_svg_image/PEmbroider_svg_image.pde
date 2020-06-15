// Test program for the PEmbroider library for Processing:
// Filling a pre-loaded SVG image

import processing.embroider.*;
import static processing.embroider.PEmbroiderGraphics.*; // PEmbroiderGraphics.PARALLEL -> PARALLEL
PEmbroiderGraphics E;

PShape mySvgImage;

void setup() {
  size(1000, 350); 
  noLoop(); 
  
  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_svg_image.vp3");
  E.setPath(outputFilePath); 
  
  PShape mySvgImage = loadShape("duck.svg");
  E.fill(0,0,0);
  E.stroke(0,0,0); 
  E.strokeWeight(1);
  E.hatchSpacing(4); 
  E.setStitch(5, 15, 0); 
  
  E.hatchMode(PERLIN);
  E.shape(mySvgImage,50,50,250,250);
  
  E.hatchMode(PARALLEL);
  E.shape(mySvgImage,350,200);
  
  E.hatchMode(CROSS);
  E.shape(mySvgImage,500,200);
  
  E.hatchMode(CONCENTRIC);
  E.shape(mySvgImage,650,200);
  
  E.noFill();
  E.strokeWeight(10);
  E.strokeMode(PERPENDICULAR);
  E.shape(mySvgImage,800,200);

  //-----------------------
  // E.optimize(); // slow, but good and very important
  E.visualize();
  // E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
