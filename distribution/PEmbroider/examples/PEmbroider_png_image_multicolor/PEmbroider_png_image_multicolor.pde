// Test program for the PEmbroider library for Processing:
// Making a multi-color embroidery 
// based on individually colored .PNGs

import processing.embroider.*;
PEmbroiderGraphics E;
PImage color_red;
PImage color_yellow;
PImage color_brown;
PImage color_blue;
PImage color_black;
PImage color_pink;
PImage color_green;


void setup() {
  noLoop(); 
  size (800, 800);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_multicolor_png.jef");
  E.setPath(outputFilePath); 
  
  // The image should consist of white shapes on a black background. 
  // The ideal image is an exclusively black-and-white .PNG or .GIF.
  color_red =    loadImage("red.png");
  color_yellow = loadImage("yellow.png");
  color_brown =  loadImage("brown.png");
  color_blue =   loadImage("blue.png");
  color_black =  loadImage("black.png");
  color_pink =   loadImage("pink.png");
  color_green =  loadImage("green.png");


  E.beginDraw(); 
  E.clear();

  // Stroke properties
  E.noStroke();

  // Fill properties
  E.hatchSpacing(4.0); 
  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.33;

  //Red fill is semi-sparse concentric
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC); 
  E.setStitch(10, 20, 0);
  E.fill(255, 0, 0);
  E.image(color_red, 0, 0); 

  //Yellow is perlin fill
  E.hatchMode(PEmbroiderGraphics.PERLIN);
  E.HATCH_SPACING = 1;
  E.fill(255, 255, 0);
  E.stroke(255, 255, 0);
  E.image(color_yellow, 0, 0);

  //Brown is Concentric fill
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC); 
  E.fill(45, 30, 0);
  E.noStroke();
  E.HATCH_SPACING = 2.5;
  E.image(color_brown, 0, 0);

  //Pink Fill is denser concentric
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.fill(255, 155, 155);
  E.HATCH_SPACING = 2;
  E.image(color_pink, 0, 0); 

  //Blue is cross fill
  E.hatchMode(PEmbroiderGraphics.CROSS);
  E.HATCH_ANGLE = radians(90); 
  E.HATCH_ANGLE2 = radians(75); 
  E.HATCH_SPACING = 4;
  E.fill(0, 0, 155);
  E.image(color_blue, 0, 0); 

  //Black fill is concentric
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC); 
  E.HATCH_SPACING = 2;
  E.fill(0, 0, 0);
  E.image(color_black, 0, 0); 


  //Green fill is parallel
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.hatchAngleDeg(90);
  E.hatchSpacing(2.5);
  E.setStitch(2, 40, 0);
  E.fill(0, 255, 0);
  E.image(color_green, 0, 0);

  //-----------------------
  //E.optimize();   // slow, but good and important

  //setting first vaule true shows colorized preview
  E.visualize(true, false, false);  // 
  // E.printStats(); //
  // E.endDraw();    // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
