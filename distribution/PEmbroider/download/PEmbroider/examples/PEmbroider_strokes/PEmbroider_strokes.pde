// Test program for the PEmbroider library for Processing:
// Basic shapes

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  noLoop(); 
  size (650, 500);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_strokes.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();
  E.CIRCLE_DETAIL = 60; 


  //-----------------------
  // Shapes can have 
  // (1) a stroke and no fill; 
  // (2) a fill and no stroke;
  // (3) both stroke and fill.
  E.HATCH_SPACING = 6;
  
  E.noFill();
  E.stroke(0); 
  E.strokeWeight(1); 
  E.circle (50, 50, 100); // (1)
  
  E.fill(0);
  E.noStroke(); 
  E.strokeWeight(1); 
  E.circle (50, 200, 100); // (2)
  
  E.fill(0);
  E.stroke(0);
  E.strokeWeight(1); 
  E.circle (50, 350, 100); // (3)

  
  //-----------------------
  // In "PERPENDICULAR" mode, strokes are  
  // rendered with perpendicular stitches. 
  E.noFill();
  E.stroke(0); 
  E.STROKE_MODE = PEmbroiderGraphics.PERPENDICULAR;
  
  E.strokeWeight(8); 
  E.STROKE_SPACING = 4;
  E.circle (200, 50, 100);

  E.strokeWeight(20); 
  E.STROKE_SPACING = 4;
  E.circle (350, 50, 100);
  
  E.strokeWeight(20); 
  E.STROKE_SPACING = 8;
  E.circle (500, 50, 100);
  
  
  //-----------------------
  // In "TANGENT" mode, strokes are  
  // rendered with concentric stitches.
  E.noFill();
  E.stroke(0); 
  E.STROKE_MODE = PEmbroiderGraphics.TANGENT;
  
  E.strokeWeight(8); 
  E.STROKE_SPACING = 4;
  E.circle (200, 200, 100);

  E.strokeWeight(20); 
  E.STROKE_SPACING = 4;
  E.circle (350, 200, 100);
  
  E.strokeWeight(20); 
  E.STROKE_SPACING = 8;
  E.circle (500, 200, 100);
  

  //-----------------------
  // Here's a complex polygon with
  // both a thick stroke and a fill. 
  E.STROKE_MODE = PEmbroiderGraphics.PERPENDICULAR;
  E.stroke(0); 
  E.fill(0); 
  
  E.beginShape();
  E.strokeWeight(20); 
  E.STROKE_SPACING = 4;
  E.vertex(200, 350);
  E.vertex(400, 375);
  E.vertex(600, 350); 
  E.vertex(600, 450); 
  E.vertex(200, 450); 
  E.endShape(CLOSE); 
  
 
  //-----------------------
  E.visualize();
  E.optimize(); // VERY SLOW, but important for file output!
  // E.endDraw(); // write out the embroidery file
}
