// Test program for the PEmbroider library for Processing:
// Different methods for filling (hatching) shapes with PEmbroider: 
// * PEmbroiderGraphics.PERLIN
// * PEmbroiderGraphics.CROSS
// * PEmbroiderGraphics.DRUNK
// * PEmbroiderGraphics.VECFIELD

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  noLoop(); 
  size (700, 700);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_shape_hatching_2.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 
  E.noStroke(); 


  //-----------------------
  E.HATCH_MODE = PEmbroiderGraphics.PERLIN;
  E.HATCH_SPACING = 4;
  E.HATCH_SCALE = 1.0;
  E.rect( 25, 25, 200, 200);

  E.HATCH_MODE = PEmbroiderGraphics.PERLIN;
  E.HATCH_SPACING = 8;
  E.HATCH_SCALE = 1.0;
  E.rect(250, 25, 200, 200);

  E.HATCH_MODE = PEmbroiderGraphics.PERLIN;
  E.HATCH_SPACING = 4;
  E.HATCH_SCALE = 4.5;
  E.rect(475, 25, 200, 200);


  //-----------------------
  // The "cross" hatch mode is a convenience mode, 
  // placing parallel lines of stitching at two orientations
  // specified by HATCH_ANGLE and HATCH_ANGLE2:
  E.HATCH_MODE = PEmbroiderGraphics.CROSS;
  E.HATCH_ANGLE = radians(90);
  E.HATCH_ANGLE2 = radians(0); 
  E.HATCH_SPACING = 4;
  E.circle(125, 350, 200);

  E.HATCH_MODE = PEmbroiderGraphics.CROSS;
  E.HATCH_ANGLE = radians(90); 
  E.HATCH_ANGLE2 = radians(75); 
  E.HATCH_SPACING = 8;
  E.circle(350, 350, 200);
  
  E.HATCH_MODE = PEmbroiderGraphics.PERLIN;
  E.HATCH_ANGLE = radians(90); 
  E.HATCH_ANGLE2 = radians(75); 
  E.HATCH_SPACING = 8;
  E.circle(575, 350, 200);


  /*
  // The purpose of the "drunk walk" hatch mode is 
  // not to be useful in itself -- but rather, it is 
  // a simple pedagogic example for how to code your
  // OWN hatch technique (in the PEmbroider source code), 
  // if you wanted to extend the PEmbroider library. 
  // This is commented out because in practice, the DRUNK 
  // hatch mode produces really bad snarls. Don't use it!
  //
  E.HATCH_MODE = PEmbroiderGraphics.DRUNK;
  E.circle(350, 200, 100);
  */
  

  //-----------------------
  // The VECFIELD hatch mode allows you to create a 
  // user-defined vector field, with a function that 
  // returns a 2D vector indicating the local orientation
  // of stitches at any given point (x,y). 
  // See the MyVecField class below.
  MyVecField mvf = new MyVecField();
  E.HATCH_MODE = PEmbroiderGraphics.VECFIELD;
  E.HATCH_VECFIELD = mvf;
  E.HATCH_SPACING = 4;
  E.ellipse ( 350, 575, 650, 200);


  //-----------------------
  //E.optimize(); // slow, but good and important
  E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_shape_hatching_2.png");
}


//--------------------------------------------
class MyVecField implements PEmbroiderGraphics.VectorField {
  public PVector get(float x, float y) {
    x*=0.05;
    return new PVector(1, 0.5*sin(x));
  }
}


//--------------------------------------------
void draw() {
  ;
}
