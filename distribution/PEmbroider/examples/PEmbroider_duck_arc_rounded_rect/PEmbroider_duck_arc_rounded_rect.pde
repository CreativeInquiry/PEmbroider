// Test program for the PEmbroider library for Processing:
// Filling a pre-loaded "image" using various hatches.

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  size(1000, 500); 
  noLoop(); 
  E = new PEmbroiderGraphics(this, width, height);

  PShape duck = loadShape("duck.svg");
  
  shape(duck,0,0);
  
  E.shape(duck,0,0);

  //-----------------------
  //E.optimize(); // slow, but good and important
  E.visualize();
  E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
