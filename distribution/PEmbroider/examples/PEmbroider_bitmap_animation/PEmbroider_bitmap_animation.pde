// Test program for the PEmbroider library for Processing:
// Embroidering the frames of an Eadweard Mubridge animation. 

import processing.embroider.*;
PEmbroiderGraphics E;

int nFrames = 15; 
PImage frames[];


void setup() {
  size(1325, 925); 
  frameRate(12);
  // noLoop(); 

  // Load the frames of the animated GIF to embroider. 
  // Should consist of white shapes on a black background. 
  // We have pre-separated into individual images using ezgif.com.
  // An alternative would be to load a single animated GIF, using
  // a library like https://extrapixel.github.io/gif-animation/.
  // We didn't do that here, in order to avoid external dependencies.
  //
  frames = new PImage[nFrames];
  for (int i=0; i<nFrames; i++) {
    String imageFilename = "muybridge_horse_" + nf(i, 2) + ".png";
    frames[i] = loadImage(imageFilename);
  }


  // Create and initialize the PEmbroiderGraphics object, E
  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_bitmap_animation.vp3");
  E.setPath(outputFilePath);
  E.beginDraw(); 
  E.clear();

  E.beginCull();
  E.setStitch(5, 40, 0);
  E.stroke(0, 0, 0);
  E.strokeWeight(1); 
  E.noFill(); 

  // Make a rounded rect around each animation frame.
  int pw = frames[0].width;
  int ph = frames[0].height;
  for (int i=0; i<nFrames; i++) {
    int px = (i%4) * (pw+25) + 25;
    int py = (i/4) * (ph+25) + 25;
    E.rect(px, py, pw, ph, 25);
  }

  // The (white) shapes will be filled with dense concentric 
  // stitching. Each shape will be bordered by a thin line. 
  //
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.strokeMode(PEmbroiderGraphics.TANGENT); 
  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.3333;
  E.setStitch(3, 40, 0);
  E.hatchAngleDeg(85); // not currently used (i.e. with CONCENTRIC hatch)
  E.hatchSpacing(2.0);
  E.strokeSpacing(1.0); 
  E.strokeWeight(1); 
  E.stroke(0, 0, 0);
  E.fill(0, 0, 0); 

  // Render each frame of the animation, in a 4x4 grid
  for (int i=0; i<nFrames; i++) {
    int px = (i%4) * (pw+25) + 25;
    int py = (i/4) * (ph+25) + 25;
    E.image(frames[i%nFrames], px, py, pw, ph);
  }
  E.endCull();


  //-------------------
  // Be sure to un-comment E.optimize() and E.endDraw() below
  // when you want to actually export the embroidery file!!!!
  //
  // E.optimize();   // slow, but good and very important
  E.visualize();
  // E.endDraw();    // write out the file
  // E.printStats(); // tell us about the embroidery
  // save("PEmbroider_bitmap_animation.png");
}


//--------------------------------------------
void draw() {
  image(frames[frameCount%nFrames], 1000, 700);
}
