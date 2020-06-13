// Test program for the PEmbroider library for Processing:
// Filling an image

import processing.embroider.*;
import static processing.embroider.PEmbroiderGraphics.*;

PEmbroiderGraphics E;
PGraphics PG;

void setup() {
  size(750, 500); 
  noLoop(); 

  E = new PEmbroiderGraphics(this, 500, 500);
  PG = createGraphics(250, 250);

  String outputFilePath = sketchPath("PEmbroider_bitmap_image_1.vp3");
  E.setPath(outputFilePath); 

  // Draw bitmap image into offscreen graphics buffer.
  PImage myImage = loadImage("broken_heart.png");
  PG.beginDraw();
  PG.background(0);
  PG.image(myImage, 0, 0); 
  // we need black on white for image() to work
  // maybe there will be a setting later,
  // but let's just use the convenient INVERT for now
  PG.filter(INVERT);
  PG.endDraw(); 

  E.beginDraw(); 
  E.clear();
  E.fill(0, 0, 0); 
  E.noStroke();
  E.ellipseMode(CENTER); 
  E.setStitch(2, 20, 0); 

  // Draw a parallel-hatched heart
  E.hatchMode(PARALLEL);
  E.hatchAngleDeg(10);
  E.hatchSpacing(4.0);
  E.setStitch(2, 20, 0.0); 
  E.image(PG, 0, 0, PG.width, PG.height);

  // Draw a cross-hatched heart
  E.hatchMode(PEmbroiderGraphics.CROSS); 
  E.HATCH_ANGLE = radians(20);
  E.HATCH_ANGLE2 = radians(0); 
  E.HATCH_SPACING = 4;
  E.image(PG, 250, 0, PG.width, PG.height);

  // Draw a densely-hatched, concentric-hatched
  E.hatchMode(CONCENTRIC); 
  E.hatchSpacing(2.0);
  E.setStitch(2,10,1.0);
  E.image(PG, 0, 250, PG.width, PG.height);

  // Draw a loosely-hatched, concentric-hatched
  E.hatchMode(CONCENTRIC); 
  E.hatchSpacing(8.0);
  E.image(PG, 250, 250, PG.width, PG.height);
  // E.stroke(0, 0, 0);
  // E.noFill(); 
  // E.strokeWeight(1); 
  // E.image(PG, 250, 250, PG.width, PG.height);
  

  // Draw the outline of a heart
  E.stroke(0, 0, 0); 
  E.noFill(); 
  E.strokeWeight(15); 
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.strokeSpacing(4);
  E.image(PG, 500, 250, PG.width, PG.height);


  // draw the raster graphics (for reference)
  image(PG, 500, 0);

  //-----------------------
  //E.optimize(); // slow, but good and important
  E.visualize();
  // E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
