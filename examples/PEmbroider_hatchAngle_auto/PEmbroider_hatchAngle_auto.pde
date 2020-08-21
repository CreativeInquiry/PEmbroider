// Test program for the PEmbroider library for Processing:
// Set the orientation of stitches (the hatchAngle) automatically, 
// based on the orientation of a shape, using hatchAngle(E.AUTO).

import processing.embroider.*;
PEmbroiderGraphics E;

//------------------------------------------------
void setup() {
  size(500, 500);

  E = new PEmbroiderGraphics(this);
  E.noStroke();
  E.fill(0);
  E.hatchSpacing(8);
  E.hatchAngle(E.AUTO);
}

//------------------------------------------------
void draw() {
  background(255);
  
  E.clear();
  E.hatchMode(mousePressed ? E.PARALLEL : E.SATIN);
  
  E.beginShape(); 
  {
    E.vertex(100, 100); 
    E.vertex(160, 110); 
    E.vertex(250, 150); 
    E.vertex((300+mouseX)/2, (200+mouseY)/2); 
    E.vertex(mouseX, mouseY);
    E.vertex((250+mouseX)/2, (300+mouseY)/2);
    E.vertex(150, 250);
  }
  E.endShape(CLOSE);
  
  E.visualize();
}
