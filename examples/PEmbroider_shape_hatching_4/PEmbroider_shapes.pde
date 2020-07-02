// Test program for the PEmbroider library for Processing:
// Basic shapes

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  //noLoop(); 
  size (900, 600);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_shapes.vp3");
  E.setPath(outputFilePath); 

  E.ellipseMode(CORNER);

  E.beginDraw(); 
  E.clear();
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 
  E.noStroke(); 

  E.hatchSpacing(5);
  E.hatchAngleDeg(50);
  E.hatchMode(PEmbroiderGraphics.SATIN);
  E.setStitch(2, 50, 0); 
  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.33;

  //-----------------------
  // Standard shapes are supported
  E.circle (50, 50, 150);
  E.square(225, 50, 150); 
  E.rect (400, 50, 100, 150);
  E.rect (525, 50, 150, 150, 25); 
  E.rect (700, 50, 150, 150, 0, 0, 0, 50); 
  
  
  E.quad (50, 250, 200, 250, 170, 400, 50, 400);
  E.ellipse(210, 250, 200, 150);
  E.triangle (410, 400, 460, 250, 510, 400); 
  E.arc (525, 250, 150, 150, 0, PI*1.25, PIE); 
  E.arc (700, 250, 150, 150, 0, PI*1.25, CHORD); 

  //-----------------------
  // Arbitrary polygons are supported
  E.beginShape();
  E.vertex(50, 450);
  E.vertex(50, 575);
  E.vertex( 330, 575);
  E.vertex( 330, 500);
  E.vertex(200, 500);
  E.vertex(175, 425);

  E.endShape(CLOSE);

  //-----------------------
  // Shapes can mix straight and curved sides
  E.beginShape();
  E.vertex(365, 475);
  E.quadraticVertex(530, 430, 450, 500);
  E.quadraticVertex(400, 530, 560, 575);
  E.vertex(560, 430);
  E.vertex(360, 430);
  E.endShape(CLOSE);

  //-----------------------
  // Shapes can be "compound" and include holes, 
  // i.e. have multiple contours
  E.beginShape();
  E.vertex(600, 460);
  E.vertex(850, 430);
  E.vertex(800, 575);
  E.vertex(620, 575);
  
  E.beginContour();
  E.vertex(650, 520);
  E.vertex(710, 520);
  E.vertex(735, 505);
  E.vertex(690, 475);
  
  E.vertex(643, 475);
  
  
  
  
  E.endContour();
  
  E.endShape(CLOSE);


  //-----------------------
  //E.optimize(); // slow, but very good and very important
  //E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_shapes.png");
}

void draw(){
  background(200);
  if (mousePressed){
    E.eraser(mouseX,mouseY,20);
  }
  E.visualize();
}
