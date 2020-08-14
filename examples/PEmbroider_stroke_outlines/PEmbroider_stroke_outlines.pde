// Test program for the PEmbroider library for Processing:
// When a shape has a thick stroke around it: 
// Is the stroke inside, outside, or centered on the shape's contour?
// ALSO SEE: the PEmbroider_strokeLocation example.
//
// PEmbroider provides some constants for doing this, for example: 
// E.strokeLocation(CENTER); // or INSIDE or OUTSIDE
// It's also possible to use a float between -1 and 1: 
// E.strokeLocation(0.33); // Slightly outside of center.

// Import the PEmbroider library
import processing.embroider.*;

// The next 'import' line allows quick access to PEmbroider constants. 
// By including it, you can simply write things like E.hatchMode(PARALLEL);
// instead of the more verbose E.hatchMode(PEmbroiderGraphics.PARALLEL);
import static processing.embroider.PEmbroiderGraphics.*; // 

// Declaration of the PEmbroider object
PEmbroiderGraphics E;

//===============================================
void setup() {
  noLoop(); 
  size (1150, 475);
  E = new PEmbroiderGraphics(this, width, height);

  String embroideryFileType = ".vp3"; // or ".dst", ".pes", ".jef" etc. 
  String outputFilePath = sketchPath("PEmbroider_stroke_outlines" + embroideryFileType);
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();

  
  //-----------------------
  // Stroke properties
  E.strokeMode(E.TANGENT);
  E.strokeWeight(15);
  E.strokeSpacing(3);
  E.noFill();

  //-----------------
  // Outline is positioned OUTSIDE the bounds of the shapes
  // NOTE: This is equivalent to E.strokeLocation(1);
  E.strokeLocation(OUTSIDE); //same as above
  E.beginComposite();
  E.composite.rect(125,175,200,200);
  E.composite.circle(125,225,150);
  E.composite.triangle(375,250,250, 60,150, 250);
  E.endComposite();
   
  //-----------------
  // Outline is CENTERed on the bounds of the shapes
  // NOTE: This is equivalent to E.strokeLocation(0);
  E.strokeLocation(CENTER);  //same as above
  E.beginComposite();
  E.composite.rect(475,175,200,200);
  E.composite.circle(475,225,150);
  E.composite.triangle(725,250,600, 60,500, 250);
  E.endComposite();    
  
  //-----------------
  // Outline is positioned INSIDE the bounds of the shapes
  // NOTE: This is equivalent to E.strokeLocation(-1);
  E.strokeLocation(INSIDE);  //same as above
  E.beginComposite();
  E.composite.rect(825,175,200,200);
  E.composite.circle(825,225,150);
  E.composite.triangle(1075,250,950, 60,850, 250);
  E.endComposite();    
    
    
  //-----------------
  // All of these shapes are drawn on the pixel-canvas 
  // (not rendered into the embroidery), as a "debug view".
  // This lets you see the differences.
  noFill();
  strokeWeight(3);
  stroke(255,125,200);
  
  rect(125,175,200,200);
  circle(125,225,150);
  triangle(375,250,250, 60,150, 250);
  rect(475,175,200,200);
  circle(475,225,150);
  triangle(725,250,600, 60,500, 250);
  rect(825,175,200,200);
  circle(825,225,150);
  triangle(1075,250,950, 60,850, 250);
  
  //-----------------
  // Uncomment E.endDraw() below to actually save the file!
  E.optimize(); // Slow, but good and important
  E.visualize();
  // E.endDraw(); // write out the embroidery file
  
  
  //-----------------
  // Should we also save a pixel screenshot?
  boolean bAlsoSaveScreenshot = false; 
  if (bAlsoSaveScreenshot){
    save("PEmbroider_stroke_outlines.png");
  }
}
