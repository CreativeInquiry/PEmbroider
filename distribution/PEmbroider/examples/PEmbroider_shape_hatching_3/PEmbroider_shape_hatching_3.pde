// Test program for the PEmbroider library for Processing:
// Different methods for filling (hatching) shapes with PEmbroider: 
// Experiments with E.PARALLEL_RESAMPLING_OFFSET_FACTOR and
// E.setStitch (with or without noise).


import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  noLoop(); 
  size (500, 500);
  E = new PEmbroiderGraphics(this, width, height);

  String outputFilePath = sketchPath("PEmbroider_shape_hatching_3.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  E.strokeWeight(1); 
  E.fill(0, 0, 0); 
  E.noStroke(); 
  E.ellipseMode(CENTER); 


  // float msl = 5.0;   // minimum stitch length
  // float  sl = 10.0;  // desirable stitch length
  // float  rn = 1.0;   // resample noise -- to avoid alignment patterns
  // E.setStitch( msl, sl, rn);

  //-----------------------
  // Hatches have a stitch length of 25. 
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.hatchAngleDeg(20.0); 
  E.hatchSpacing(4.0); 
  E.setStitch( 5, 25, 0);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 1.0;
  E.circle(100, 100, 120);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.5;
  E.circle(250, 100, 120);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.33333;
  E.circle(400, 100, 120);


  //-----------------------
  // Hatches have a stitch length of 10.
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.hatchAngleDeg(20.0); 
  E.hatchSpacing(4.0); 
  E.setStitch( 5, 10, 0);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 1.0;
  E.circle(100, 250, 120);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.5;
  E.circle(250, 250, 120);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.33333;
  E.circle(400, 250, 120);


  //-----------------------
  // Hatches have a noisy stitch length, of approximately 10.
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.hatchAngleDeg(20.0); 
  E.hatchSpacing(4.0); 
  E.setStitch(5, 10, 0.5);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 1.0;
  E.circle(100, 400, 120);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.5;
  E.circle(250, 400, 120);

  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.33333;
  E.circle(400, 400, 120);

  //-----------------------
  E.optimize(); // slow, but good and important
  E.visualize();
  E.endDraw(); // write out the file
}



//--------------------------------------------
void draw() {
  ;
}
