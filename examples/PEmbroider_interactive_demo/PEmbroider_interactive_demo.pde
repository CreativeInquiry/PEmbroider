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

  E.beginDraw(); 
  E.clear();

  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.HATCH_SPARSITY = max(2.5, mouseX/20.0);
  E.HATCH_ANGLE = millis()/15000.0; 
  E.HATCH_ANGLE2 = frameCount*0.001;
  E.fill(0, 0, 255);
  E.rect(50, 50, 150, 150);

  float dx = mouseX - width/2;
  float dy = mouseY - height/2;
  E.HATCH_ANGLE = HALF_PI + atan2(dy, dx); 
  E.HATCH_SPARSITY = 8;
  randomSeed(5);
  E.circle(width/2-120, height/2-120, 240);

  E.HATCH_MODE = PEmbroiderGraphics.CROSS;
  E.noStroke();
  E.ellipse(300, 50, 150, 100);
  
  E.text("Space bar to save!", 90, 400);

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
