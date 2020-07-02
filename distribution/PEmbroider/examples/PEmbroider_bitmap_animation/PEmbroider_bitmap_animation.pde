// Test program for the PEmbroider library for Processing:
// Embroidering the frames of an animation. 

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

  // Embroider each of the frames of the animation 

E.beginCull();
  E.setStitch(5, 40, 0);
  E.stroke(0, 0, 0);
  E.strokeWeight(1); 
  E.noFill(); 
  //E.fill(0);
  int pw = frames[0].width;
  int ph = frames[0].height;
  for (int i=0; i<nFrames; i++) {
    int px = (i%4) * (pw+25) + 25;
    int py = (i/4) * (ph+25) + 25;
    E.rect(px, py, pw, ph, 25);
  }


  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.strokeMode(PEmbroiderGraphics.TANGENT); 
  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.3333;
  E.setStitch(3, 40, 0);
  E.hatchAngleDeg(85); 
  E.hatchSpacing(2.0);
  E.strokeSpacing(1.0); 
  E.strokeWeight(1); 
  E.stroke(0, 0, 0);
  E.fill(0, 0, 0); 

  for (int i=0; i<nFrames; i++) {
    int px = (i%4) * (pw+25) + 25;
    int py = (i/4) * (ph+25) + 25;
    E.image(frames[i%nFrames], px, py, pw, ph);
  }
  E.endCull();


  //-------------------
  // E.optimize(); // slow, but good and important
  E.visualize();
  // E.endDraw(); // write out the file
  //E.printStats();
  //save("PEmbroider_bitmap_animation.png");

}


//--------------------------------------------
void draw() {
  image(frames[frameCount%nFrames], 1000, 700);
}
