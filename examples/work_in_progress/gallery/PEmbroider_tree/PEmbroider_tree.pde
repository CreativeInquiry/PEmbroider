// Recursive tree with leaves, using the PEmbroider library.
// THIS IS NOT READY FOR DOCUMENTATION YET -- GL

import processing.embroider.*;
PEmbroiderGraphics E;

//=====================================================
void setup() {
  noLoop(); 
  size (800, 600);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_shapes.vp3");
  E.setPath(outputFilePath); 

  
  E.beginDraw(); 
  E.clear();
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.strokeCap(SQUARE);
  E.noFill(); 
  
  float theta = radians(25);
  float initialLength = 150; 
  
  E.pushMatrix();
  E.translate(width*0.5, height*0.9);
  E.strokeWeight((initialLength * 0.15)/0.66); 
  E.line(0, 0, 0, -initialLength);
  E.translate(0, -initialLength);
  branch(initialLength, theta);
  E.popMatrix();
  

  //-----------------------
  // E.optimize(); // slow, but very good and very important
  E.visualize();
  // E.printStats(); 
  // E.endDraw(); // write out the file
}


//=====================================================
void branch (float h, float theta) {
  // Recursive tree, adapted from Dan Shiffman:
  // https://processing.org/examples/tree.html
  
  // Calculate the stroke width
  float sw = h * 0.15; 
  
  // Each branch will be 2/3rds the size of the previous one
  h *= 0.70;

  // All recursive functions must have an exit condition.
  // Here, ours is when the length of the branch is 2 pixels or less
  float minBranchLength = 40; 
  if (h > minBranchLength) {
    
    E.strokeWeight(sw);  
    E.pushMatrix();       // Save the current state of transformation 
    E.rotate(theta);      // Rotate by theta
    E.line(0, 0, 0, -h);  // Draw the branch
    E.translate(0, -h);   // Move to the end of the branch
    branch(h, theta);     // Call myself to draw two new branches
    E.popMatrix();        // Pop to restore the previous matrix state.

    // Repeat the same thing, only branch off to the "left" this time.
    E.strokeWeight(sw);  
    E.pushMatrix();
    E.rotate(-theta);
    E.line(0, 0, 0, -h);
    E.translate(0, -h);
    branch(h, theta);
    E.popMatrix();
  }
}
