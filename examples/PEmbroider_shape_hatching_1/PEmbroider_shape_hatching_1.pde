// Test program for the PEmbroider library for Processing:
// Different methods for filling (hatching) shapes with PEmbroider: 
// * PEmbroiderGraphics.PARALLEL
// * PEmbroiderGraphics.CONCENTRIC
// * PEmbroiderGraphics.SPIRAL


import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (500, 500);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_shape_hatching_1.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 
  E.noStroke(); 


  //-----------------------
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.HATCH_ANGLE = radians(45);
  E.HATCH_SPARSITY = 4;
  E.circle( 50, 50, 100);

  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.HATCH_ANGLE = radians(90);
  E.HATCH_SPARSITY = 4;
  E.circle(200, 50, 100);

  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.HATCH_ANGLE = radians(90);
  E.HATCH_SPARSITY = 8;
  E.circle(350, 50, 100);


  //-----------------------
  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  E.HATCH_SPARSITY = 4;
  E.circle( 50, 200, 100);

  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  E.HATCH_SPARSITY = 4;
  E.rect(200, 200, 100, 100);

  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  E.HATCH_SPARSITY = 8;
  E.rect (350, 200, 100, 100);


  //-----------------------
  E.HATCH_MODE = PEmbroiderGraphics.SPIRAL;
  E.HATCH_SPARSITY = 4;
  E.circle( 50, 350, 100);

  E.HATCH_MODE = PEmbroiderGraphics.SPIRAL;
  E.HATCH_SPARSITY = 8;
  E.circle(200, 350, 100);

  E.HATCH_MODE = PEmbroiderGraphics.SPIRAL;
  E.HATCH_SPARSITY = 8;
  E.rect(350, 350, 100, 100);


  //-----------------------
  E.visualize();
  E.optimize(); // slow but good and important
  E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
