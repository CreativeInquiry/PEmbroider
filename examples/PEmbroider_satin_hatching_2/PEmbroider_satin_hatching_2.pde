// Test program for the PEmbroider library for Processing:
// SATIN hatching, interactive demo

import processing.embroider.*;
PEmbroiderGraphics E;

//-----------------------------------------------------
void setup() {
  // noLoop(); 
  size (900, 600);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_satin_hatching_2.vp3");
  E.setPath(outputFilePath);
}

//-----------------------------------------------------
void draw() {
  background(200); 

  E.beginDraw(); 
  E.clear();

  E.strokeWeight(1); 
  E.fill(0, 0, 0); 

  E.hatchSpacing(6);
  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.5;
  E.hatchAngleDeg(mouseX/10.0);
  E.hatchMode(PEmbroiderGraphics.SATIN);
  E.setStitch(10, 50, 0); 


  E.beginShape();
  E.vertex(100, 100); 
  E.vertex(160, 110); 
  E.vertex(250, 170);
  E.vertex(300, 250);
  E.vertex(330, 290); 
  E.vertex(270, 295);
  E.vertex(150, 180); 
  E.endShape(CLOSE);

  //-----------------------
  // Shapes can mix straight and curved sides
  E.beginShape();
  E.vertex(365, 175);
  E.quadraticVertex(530, 130, 450, 200);
  E.quadraticVertex(400, 230, 560, 275);
  E.vertex(560, 130);
  E.vertex(360, 130);
  E.endShape(CLOSE);


  //-----------------------
  // Shapes can be "compound" and include holes, 
  // i.e. have multiple contours
  E.beginShape();
  E.vertex(600, 160);
  E.vertex(850, 130);
  E.vertex(800, 275);
  E.vertex(620, 275);

  E.beginContour();
  E.vertex(650, 220);
  E.vertex(710, 220);
  E.vertex(735, 205);
  E.vertex(690, 175);
  E.vertex(643, 175);
  E.endContour();

  E.endShape(CLOSE);

  //-----------------------
  E.optimize(); // slow, but very good and very important
  E.visualize(true, true, true);
}

void keyPressed() {
  if ((key == 's') || (key == 'S')) {
    E.endDraw();
  }
}
