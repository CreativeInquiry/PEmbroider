// Test program for the PEmbroider library for Processing:
// Filling a pre-loaded SVG image

import processing.embroider.*;
PEmbroiderGraphics E;

PShape mySvgImage;

void setup() {
  size(1200, 450); 
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
  
  E.hatchMode(E.PERLIN);
  E.shape(mySvgImage,50,50,350,350);
  
  E.hatchMode(E.PARALLEL);
  E.shape(mySvgImage,425,200,150,150);
  
  E.hatchMode(E.CROSS);
  E.shape(mySvgImage,600,200,150,150);
  
  E.hatchMode(E.CONCENTRIC);
  E.shape(mySvgImage,775,200,150,150);
  
  E.noFill();
  E.strokeWeight(10);
  E.strokeMode(E.PERPENDICULAR);
  E.shape(mySvgImage,950,200,150,150);

  //-----------------------
  //E.optimize(); // slow, but good and very important
  E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_svg_image.png"); //saves a png of design from canvas
}


//--------------------------------------------
void draw() {
  ;
}
