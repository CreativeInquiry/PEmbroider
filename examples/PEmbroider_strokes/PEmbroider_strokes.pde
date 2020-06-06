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

  //-----------------------
  // Shapes can have 
  // a stroke and no fill; 
  // a fill and no stroke;
  // both stroke and fill.
  E.HATCH_SPARSITY = 8;
  E.noFill();
  E.stroke(0); 
  E.strokeWeight(1); 
  E.circle (50, 50, 100);
  
  E.fill(0);
  E.noStroke(); 
  E.strokeWeight(1); 
  E.circle (50, 200, 100);
  
  E.fill(0);
  E.stroke(0);
  E.strokeWeight(1); 
  E.circle (50, 350, 100);

  
  //-----------------------
  // In "NORMAL" mode, strokes are  
  // rendered with perpendicular stitches. 
  E.noFill();
  E.stroke(0); 
  E.STROKE_MODE = PEmbroiderGraphics.NORMAL;
  
  E.strokeWeight(5); 
  E.STROKE_SPARSITY = 4;
  E.circle (200, 50, 100);

  E.strokeWeight(10); 
  E.STROKE_SPARSITY = 4;
  E.circle (350, 50, 100);
  
  E.strokeWeight(10); 
  E.STROKE_SPARSITY = 8;
  E.circle (500, 50, 100);
  
  //-----------------------
  // In "TANGENT" mode, strokes are  
  // rendered with concentric stitches.
  E.noFill();
  E.stroke(0); 
  E.STROKE_MODE = PEmbroiderGraphics.TANGENT;
  E.STROKE_SPARSITY = 4;
  
  E.strokeWeight(5); 
  E.STROKE_SPARSITY = 4;
  E.circle (200, 200, 100);

  E.strokeWeight(10); 
  E.STROKE_SPARSITY = 4;
  E.circle (350, 200, 100);
  
  E.strokeWeight(10); 
  E.STROKE_SPARSITY = 8;
  E.circle (500, 200, 100);
  

  //-----------------------
  // Here's a complex polygon with
  // both a thick stroke and a fill. 
  E.STROKE_MODE = PEmbroiderGraphics.TANGENT;
  E.stroke(0); 
  E.fill(0); 
  
  E.beginShape();
  E.strokeWeight(10); 
  E.STROKE_SPARSITY = 4;
  E.vertex(200, 350);
  E.vertex(400, 375);
  E.vertex(600, 350); 
  E.vertex(600, 450); 
  E.vertex(200, 450); 
  E.endShape(CLOSE); 
  
 
  //-----------------------
  E.visualize();
  E.optimize(); // VERY SLOW, but important for file output!
  E.endDraw(); // write out the file
}
