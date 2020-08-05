// Test program for the PEmbroider library for Processing:
// Optimize each charater in text individually

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  //noLoop(); 
  size (1050, 550);
  PFont myFont = createFont("Helvetica-Bold", 400);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_text_1.vp3");
  E.setPath(outputFilePath); 
  E.TEXT_OPTIMIZE_PER_CHAR = true;

  //E.HATCH_BACKEND = E.FORCE_RASTER;

  E.beginDraw(); 
  E.clear();
  E.textAlign(CENTER, BASELINE);
  E.textFont(myFont);
  E.textSize(400);
  E.fill(0);
  //E.stroke(0,0,255);
  
  E.text("hello!",width/2,400);
  
  E.optimize();
  
  E.visualize(true,false,true);
}

void draw(){
  background(128);
  E.visualize(true,false,false,frameCount);
  
}
