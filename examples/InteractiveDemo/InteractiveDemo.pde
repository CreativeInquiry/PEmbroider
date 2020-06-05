// Simple test program for the PEmbroider library for Processing

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  size (480,480);
  E = new PEmbroiderGraphics(this, width, height);  
  
}


//--------------------------------------------
void draw() {
  background(200);
  
  E.HATCH_ANGLE = frameCount*0.1;
  E.HATCH_ANGLE2 = frameCount*0.05+0.2;
  
  E.clear();
  E.fill(0);
  E.circle(50, 50, 50);
  E.circle(120, 30, 120);
  E.quad(10, 10, 80, 3, 30, 60, 0, 40);

  E.HATCH_MODE = PEmbroiderGraphics.CROSS;
  
  
  E.noStroke();

  E.beginShape();
  E.vertex(120, 120);
  E.vertex(150, 250);
  E.vertex(50, 240);
  E.vertex(50, 200);
  E.vertex(100, 220);
  E.vertex(100, 120);
  E.endShape(CLOSE);

  E.ellipse(200, 60, 100, 50);

  E.HATCH_ANGLE = 0;
  E.HATCH_SPARSITY = 8;
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.triangle(250, 200, 150, 250, 300, 280);
  
  E.text("Click to Save!", 200,180);
  
  E.visualize();
}


void mouseClicked(){
  // Note: We use sketchPath() because a FULL path is necessary, 
  // e.g. "/Users/username/Documents/..." etcetera.
  // We can write .vp3 or .dst files.
  
  String outputFilePath = sketchPath("out-at-frame-"+frameCount+".vp3");
  E.setPath(outputFilePath); 
  
  E.endDraw(); // write out the file
}
