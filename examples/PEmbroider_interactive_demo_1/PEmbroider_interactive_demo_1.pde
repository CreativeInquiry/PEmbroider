// Simple test program for the PEmbroider library for Processing. 
// Alters the design interactively, exporting the design when
// the user presses the space bar.


import processing.embroider.*;
PEmbroiderGraphics E;
int fileNumber = 1;

void setup() {
  size (750, 750);
  E = new PEmbroiderGraphics(this, width, height);
  

}


//--------------------------------------------
void draw() {
  background(200);

  E.beginDraw(); 
  E.clear();

  //fill options
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.HATCH_SPACING = max(2.5, mouseX/20.0);
  E.HATCH_ANGLE = millis()/15000.0; 
  E.HATCH_ANGLE2 = frameCount*0.001;
  E.fill(0, 0, 255);
  E.rect(100, 100, 500, 250);

  //hatch angle is dependant on mouse position
  float dx = mouseX - width/2;
  float dy = mouseY - height/2;
  E.HATCH_ANGLE = HALF_PI + atan2(dy, dx); 
  E.HATCH_SPACING = 8;
  randomSeed(5);
  E.circle(width/2-120, 500, 240);


  E.HATCH_MODE = PEmbroiderGraphics.CROSS;
  E.noStroke();
  E.ellipse(500, 400, 150, 200);
  
  
  //This text is not part of the embroidery
  textAlign(CENTER,CENTER);
  textSize(40);
  text("Space bar to save!", width/2, height-100);

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
    String outputFilePath = sketchPath("PEmbroider_interactive_demo_" + fileNumber + ".vp3");
    save("PEmbroider_interactive_demo_" + fileNumber + ".vp3");
    E.setPath(outputFilePath); 
    E.endDraw(); // write out the file
    fileNumber += 1;
  }
}
