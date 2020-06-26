// Test program for the PEmbroider library for Processing:
// Text alignment

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (900, 700);
  PFont myFont = createFont("Helvetica-Bold", 200);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_text_2.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();

  E.strokeMode(PEmbroiderGraphics.TANGENT);
  E.noFill();
  E.hatchSpacing(1.5);
  E.strokeSpacing(4);
  E.stitchLength(30); 

  E.textFont(myFont);
  E.stroke(0);
  E.strokeWeight(10);
  E.textSize(200);

  E.textAlign(LEFT, BOTTOM);
  E.text("Dg", 0,   200);

  E.textAlign(LEFT, BASELINE);
  E.text("Lb", 300, 200);

  E.textAlign(LEFT, TOP);
  E.text("Ag", 600, 200);

  E.textAlign(CENTER, BASELINE);
  E.text("Cb", 300, 400);

  E.textAlign(RIGHT, BASELINE);
  E.text("Rb", 300, 600);



  // --------------------------
  // Draw some annotations in the Processing canvas.
  // These don't show up in the embroidery file :)
  stroke(0,0,0, 160);
  line(0, 200, width, 200);
  line(0, 400, 450, 400);
  line(0, 600, 450, 600);
  line(300, 0, 300, height);
  line(600, 0, 600, height);

  fill(0);
  textAlign(LEFT, TOP);
  text("DESCENT", 0, 200);
  text("BASELINE", 300, 210);
  textAlign(LEFT, BOTTOM);
  text("ASCENT", 600, 200);

  text("LEFT", 300, 45);
  textAlign(CENTER);
  text("CENTER", 300, 420);
  textAlign(RIGHT);
  text("RIGHT", 300, 620);

  //-----------------------
  //E.optimize(); // slow, but very good and important
  E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_text_2.png"); //saves a png of design from canvas
}
