// Test program for the PEmbroider library for Processing:
// Basic shapes

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  noLoop(); 
  size (950, 950);
  
  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_strokes.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();
  E.CIRCLE_DETAIL = 60;
  E.setRenderOrder(PEmbroiderGraphics.FILL_OVER_STROKE);
  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = .33;

  //-----------------------
  // Shapes can have 
  // (1) a stroke and no fill; 
  // (2) a fill and no stroke;
  // (3) both stroke and fill.
  E.hatchSpacing(2);
  
  E.noFill();
  E.stroke(0); 
  E.strokeWeight(1); 
  E.circle (125, 125, 200); // (1)
  
  E.fill(0);
  E.noStroke(); 
  E.strokeWeight(1); 
  E.circle (125, 350, 200); // (2)
  
  E.fill(0);
  E.stroke(0);
  E.strokeWeight(1); 
  E.circle (125, 570, 200); // (3)

  
  //-----------------------
  // In "PERPENDICULAR" mode, strokes are  
  // rendered with perpendicular stitches. 
  E.noFill();
  E.stroke(0); 
  E.strokeMode(E.PERPENDICULAR);
  E.setStitch(2,25,0);
  
  E.strokeWeight(8); 
  E.strokeSpacing(4);
  E.circle (340, 125, 200);

  E.strokeWeight(20); 
  E.strokeSpacing(4);
  E.circle (570, 125, 200);
  
  E.strokeWeight(20); 
  E.strokeSpacing(8);
  E.circle (800, 125, 200);
  
  
  //-----------------------
  // In "TANGENT" mode, strokes are  
  // rendered with concentric stitches.
  E.noFill();
  E.stroke(0); 
  E.strokeMode(E.TANGENT);
  
  E.strokeWeight(8); 
  E.strokeSpacing(4);
  E.circle (340, 350, 200);

  E.strokeWeight(20); 
  E.strokeSpacing(4);
  E.circle (570, 350, 200);
  
  E.strokeWeight(20); 
  E.strokeSpacing(8);
  E.circle (800, 350, 200);
  

  //-----------------------
  // Here's a complex polygon with
  // both a thick stroke and a fill. 
  E.strokeMode(E.PERPENDICULAR);
  E.stroke(0); 
  E.fill(0); 
  
  E.beginShape();
  E.strokeWeight(20); 
  E.strokeSpacing(4);
  E.vertex(250, 470);
  E.vertex(525, 550);
  E.vertex(900, 470); 
  E.vertex(900, 650); 
  E.vertex(250, 650); 
  E.endShape(CLOSE); 
  
  //-------------------------
  // Here are more strokes on rectangles.
  E.noFill();
  
  
  //"TANGENT" Strokes
  E.STROKE_MODE = PEmbroiderGraphics.TANGENT;
  E.strokeWeight(8); 
  E.strokeSpacing(2);
  E.rect(50, 700, 175,175);
  
  E.strokeWeight(15); 
  E.strokeSpacing(10);
  E.rect(275, 700, 175,175);
  
  //"PERPENDICULAR" Strokes
  E.strokeMode(E.PERPENDICULAR);
  E.strokeWeight(10); 
  E.strokeSpacing(4);
  E.rect(500, 700, 175,175);
  
  E.strokeSpacing(8);
  E.rect(725, 700, 175,175);
   
  //-----------------------
  //E.optimize(); // VERY SLOW, but important for file output!
  E.visualize();
  //E.endDraw(); // write out the embroidery file
  //save("PEmbroider_strokes.png"); //saves a png of design from canvas
}
