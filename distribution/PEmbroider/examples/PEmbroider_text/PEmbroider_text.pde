// Test program for the PEmbroider library for Processing:
// Basic shapes

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (1000, 1000);
  PFont myFont = createFont("Helvetica-Bold", 100);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_text.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();

  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.hatchSpacing(4);
  E.strokeSpacing(4);
  
  E.textFont(myFont);
  E.fill(0);
  E.strokeWeight(10);
  E.textSize(200);
  
  E.textAlign(LEFT,BOTTOM);
  E.text("Ag", 0, 300);
  
  E.textAlign(LEFT,BASELINE);
  E.text("Cx", 300, 300);

  E.textAlign(LEFT,TOP);
  E.text("Ef", 600, 300);
  
  E.textAlign(CENTER,BASELINE);
  E.text("Cx", 300, 600);
  
  E.textAlign(RIGHT,BASELINE);
  E.text("Cx", 300, 900);
  
  
  
  // --------------------------
  // Draw some annotation
  
  line(0,300,width,300);
  line(300,0,300,height);

  fill(0);
  textAlign(LEFT,TOP);
  text("DESCENT",0,300);
  text("BASELINE",300,300);
  textAlign(LEFT,BOTTOM);
  text("ASCENT",600,300);
  
  text("LEFT",300,150);
  textAlign(CENTER);
  text("CENTER",300,450);
  textAlign(RIGHT);
  text("RIGHT",300,750);
  
  //-----------------------
  E.visualize();
  //E.optimize(); // slow, but very good and important
  //E.endDraw(); // write out the file
}
