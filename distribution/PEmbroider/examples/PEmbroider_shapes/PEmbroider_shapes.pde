// Test program for the PEmbroider library for Processing:
// Basic shapes

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  noLoop(); 
  size (500, 500);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_shapes.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 
  E.noStroke(); 

  E.HATCH_SPARSITY = 4;
  E.HATCH_ANGLE = radians(60);
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;

  //-----------------------
  // Standard shapes are supported
  E.circle (50, 50, 100);
  E.rect (200, 50, 100, 100);
  E.triangle (350, 150, 400, 50, 450, 150); 
  E.quad (50, 200, 150, 200, 120, 290, 50, 300);
  E.ellipse(175, 200, 150, 100);

  //-----------------------
  // Arbitrary polygons are supported
  E.beginShape();
  E.vertex(430, 200);
  E.vertex(450, 300);
  E.vertex(350, 290);
  E.vertex(350, 250);
  E.vertex(400, 270);
  E.vertex(400, 200);
  E.endShape(CLOSE);

  //-----------------------
  // Shapes can have curved sides
  E.beginShape();
  E.vertex(50, 400);
  E.quadraticVertex(200, 350, 150, 400);
  E.quadraticVertex(100, 450, 200, 450);
  E.vertex(200, 350);
  E.vertex(50, 350);
  E.endShape(CLOSE);

  //-----------------------
  // Shapes can be "compound" and include holes
  E.beginShape();
  E.vertex(250, 350);
  E.vertex(450, 350);
  E.vertex(400, 450);
  E.vertex(265, 450);
  
  E.beginContour();
  E.vertex(300, 375);
  E.vertex(350, 375);
  E.vertex(375, 400);
  E.vertex(360, 425);
  E.vertex(309, 425);
  E.endContour();
  
  E.endShape(CLOSE);


  //-----------------------
  E.visualize();
  E.optimize(); // slow, but good and important
  E.endDraw(); // write out the file
}
