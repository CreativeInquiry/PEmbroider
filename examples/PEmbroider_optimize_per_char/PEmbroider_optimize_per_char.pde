// Test program for the PEmbroider library for Processing:
// Optimize each character in text individually. 

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  // noLoop(); 
  size (1050, 550);
  PFont myFont = createFont("Helvetica-Bold", 360);

  E = new PEmbroiderGraphics(this, width, height);
  E.TEXT_OPTIMIZE_PER_CHAR = true;
  // E.HATCH_BACKEND = E.FORCE_RASTER;

  E.beginDraw(); 
  E.clear();
  E.textAlign(CENTER, BASELINE);
  E.textFont(myFont);
  E.textSize(360);
  E.fill(0);
  E.stroke(0); 

  E.text("hello!", width/2, 400);
  E.optimize();
  E.visualize(true, false, true);

  boolean bDoExportEmbroideryFile = false;
  if (bDoExportEmbroideryFile) {
    String outputFilePath = sketchPath("PEmbroider_text_1.vp3");
    E.setPath(outputFilePath); 
    E.endDraw();
  }
}

void draw() {
  // background(200);
  // E.visualize(true,false,false,frameCount);
}

void keyPressed() {
  if (key == ' ') {
    save("PEmbroider_optimize_per_char.png");
  }
}
