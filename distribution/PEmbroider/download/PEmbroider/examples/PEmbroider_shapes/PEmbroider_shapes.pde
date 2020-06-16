// Test program for the PEmbroider library for Processing:
// Basic shapes

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  noLoop(); 
  size (800, 500);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_shapes.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 
  E.noStroke(); 

  E.hatchSpacing(3.0);
  E.hatchAngleDeg(60);
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.setStitch(2, 16, 0); 
  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.5;

  //-----------------------
  // Standard shapes are supported
  E.circle (50, 50, 100);
  E.square(200, 50, 100); 
  E.rect (365, 50, 70, 100);
  E.rect (500, 50, 100, 100, 25); 
  E.rect (650, 50, 100, 100, 0, 0, 0, 50); 
  E.triangle (350, 300, 400, 200, 450, 300); 
  E.quad (50, 200, 150, 200, 120, 290, 50, 300);
  E.ellipse(175, 200, 150, 100);
  E.arc (500, 200, 100, 100, 0, PI*1.25, PIE); 
  E.arc (650, 200, 100, 100, 0, PI*1.25, CHORD); 

  //-----------------------
  // Arbitrary polygons are supported
  E.beginShape();
  E.vertex(130, 350);
  E.vertex(150, 450);
  E.vertex( 50, 450);
  E.vertex( 50, 400);
  E.vertex(100, 410);
  E.vertex(100, 350);
  E.endShape(CLOSE);

  //-----------------------
  // Shapes can mix straight and curved sides
  E.beginShape();
  E.vertex(200, 400);
  E.quadraticVertex(350, 350, 300, 400);
  E.quadraticVertex(250, 450, 350, 450);
  E.vertex(350, 350);
  E.vertex(200, 350);
  E.endShape(CLOSE);

  //-----------------------
  // Shapes can be "compound" and include holes, 
  // i.e. have multiple contours
  E.beginShape();
  E.vertex(400, 350);
  E.vertex(600, 350);
  E.vertex(550, 450);
  E.vertex(415, 450);
  
  E.beginContour();
  E.vertex(450, 375);
  E.vertex(500, 375);
  E.vertex(525, 400);
  E.vertex(510, 425);
  E.vertex(459, 425);
  E.endContour();
  
  E.endShape(CLOSE);


  //-----------------------
  E.optimize(); // slow, but very good and very important
  E.visualize();
  E.endDraw(); // write out the file
}
