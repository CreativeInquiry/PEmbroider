// Simple test program for the PEmbroider library for Processing. 
// Alters the design interactively, exporting the design when
// the user presses the space bar.


import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  size (500, 500);
  E = new PEmbroiderGraphics(this, width, height);
}


//--------------------------------------------
void draw() {
  background(200);
  randomSeed(5);

  E.beginDraw(); 
  E.clear();
  E.hatchMode(PEmbroiderGraphics.PARALLEL); 
  E.hatchSpacing( max(2.5, mouseX/20.0)); 
  E.hatchAngle (millis()/15000.0); 
  
  E.noStroke(); 
  E.fill(0, 0, 255);
  E.rect(50, 50, 150, 150);
  
  float dx = mouseX - width/2;
  float dy = mouseY - height/2;
  E.hatchAngle( HALF_PI + atan2(dy, dx)); 
  E.hatchSpacing(8);
  E.ellipseMode(CENTER); 
  E.circle(width/2, height/2, 240);

  fill(0,0,0);
  text("Press space bar to save!", 20, 40);

  if (!mousePressed) {
    // Very important function, produces optimized paths!
    E.optimize(); 
  }
  
  // params: colors, stitches, route
  E.visualize(true, true, true);
}


void keyPressed() {
  // Export the embroidery file when we press the space bar. 
  if (key == ' ') {
    String outputFilePath = sketchPath("PEmbroider_interactive_demo_" + frameCount + ".vp3");
    E.setPath(outputFilePath); 
    E.endDraw(); // write out the file
  }
}
