// Test program for the PEmbroider library for Processing:
// Lines and curves

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  noLoop(); 
  size (1075, 700);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("Pembroider_lines_1.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();
  E.noFill(); 
  
 
  //-----------------------
  // Vary stroke weight, 
  // show both PERPENDICULAR & TANGENT
  int nLines = 15; 
  int lineLength = 150;
  
  E.strokeSpacing(2.0);
  E.setStitch(5, 15, 0.0);
  E.PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.33;
  
  E.strokeCap(SQUARE); // NOTE: currently not working for PERPENDICULAR
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR); 
  
  //repeats a stitch at the end of each line drawn
  E.beginRepeatEnd(2);
  
  int lineX = 50;
  for (int i=0; i<nLines; i++) {
    float x0 = lineX; 
    float x1 = x0+lineLength;
    float y0 = map(i, 0, nLines-1, 50, height-50);
    E.strokeWeight(i+1); 
    E.line (x0, y0, x1, y0);
  }
  
  
  E.strokeCap(SQUARE);
  lineX += lineLength+50;
  E.strokeMode(PEmbroiderGraphics.TANGENT); 
  for (int i=0; i<nLines; i++) {
    float x0 = lineX; 
    float x1 = x0+lineLength;
    float y0 = map(i, 0, nLines-1, 50, height-50);
    E.strokeWeight(i+1); 
    E.line (x0, y0, x1, y0);
  }
  


  //-----------------------
  // Vary the stitch lengh
  
  E.strokeMode(PEmbroiderGraphics.TANGENT);
  E.strokeWeight(1); 
  
  lineX += lineLength+50;
  for (int i=0; i<nLines; i++) {
    float x0 = lineX; 
    float x1 = x0+lineLength;
    float y0 = map(i, 0, nLines-1, 50, height-50);
    float stitchLen = (x1-x0)/(nLines-i);
    E.setStitch(5, stitchLen, 0.0);
    E.line (x0, y0, x1, y0);
  }
  

  //-----------------------
  // Vary the strokeSpacing, 
  // show both PERPENDICULAR & TANGENT
  E.strokeCap(ROUND);
  E.strokeWeight(15); 
  E.setStitch(5, 15, 0.0);
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR); 
  
  lineX += lineLength+50;
  for (int i=0; i<nLines; i++) {
    float x0 = lineX; 
    float x1 = x0+lineLength;
    float y0 = map(i, 0, nLines-1, 50, height-50);
    E.strokeSpacing(1.0 + i);
    E.line (x0, y0, x1, y0);
  }
  
  
  E.strokeCap(ROUND);
  E.strokeWeight(15); 
  E.setStitch(5, 15, 0.0);
  E.strokeMode(PEmbroiderGraphics.TANGENT);
  lineX += lineLength+50;
  for (int i=0; i<nLines; i++) {
    float x0 = lineX; 
    float x1 = x0+lineLength;
    float y0 = map(i, 0, nLines-1, 50, height-50);
    E.strokeSpacing(1.0 + i);
    E.line (x0, y0, x1, y0);
  }

  E.endRepeatEnd();

  //-----------------------
  //E.optimize(); // slow, but very good and important
  E.visualize();
  //E.endDraw(); // write out the file
  //save("Pembroider_lines_1.png");
}
