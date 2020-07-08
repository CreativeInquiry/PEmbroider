// Test program for the PEmbroider library for Processing:
// Different methods for filling (hatching) shapes with PEmbroider: 
// Experiments with E.PARALLEL_RESAMPLING_OFFSET_FACTOR and
// E.setStitch (with or without noise).


import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  noLoop(); 
  size (700, 700);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_shape_hatching_3.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 
  E.noStroke(); 
  //E.ellipseMode(CENTER); 


  // float msl = 5.0;   // minimum stitch length
  // float  sl = 10.0;  // desirable stitch length
  // float  rn = 1.0;   // resample noise -- to avoid alignment patterns
  // E.setStitch( msl, sl, rn);

  //-----------------------
  // Hatches have a stitch length of 50. 
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.hatchAngleDeg(20.0); 
  E.hatchSpacing(2.0); 
  E.setStitch( 5, 50, 0);

  // WARNING NOTE: The value of 1.0 for the property
  // PARALLEL_RESAMPLING_OFFSET_FACTOR is only shown here
  // for illustrative purposes. In real life, you probably
  // do not want to use this value, as it can cause problems
  // in the physical embroidery.
  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 1.0;
  E.circle(125, 125, 200);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.5;
  E.circle(350, 125, 200);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.33333;
  E.circle(575, 125, 200);


  //-----------------------
  // Hatches have a stitch length of 40.
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.hatchAngleDeg(20.0); 
  E.hatchSpacing(2.5); 
  E.setStitch( 5, 40, 0);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 1.0;
  E.circle(125, 350, 200);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.5;
  E.circle(350, 350, 200);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.33333;
  E.circle(575, 350, 200);


  //-----------------------
  // Hatches have a noisy stitch length, of approximately 30.
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.hatchAngleDeg(20.0); 
  E.hatchSpacing(2.0); 
  E.setStitch(5, 30, 0.5);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 1.0;
  E.circle(125, 575, 200);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.5;
  E.circle(350, 575, 200);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.33333;
  E.circle(575, 575, 200);
  
 

  //-----------------------
  //E.optimize(); // slow, but good and important
  E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_shape_hatching_3.png");
}



//--------------------------------------------
void draw() {
  ;
}
