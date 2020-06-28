// Test program for the PEmbroider library for Processing:
// Filled & stroked text rendering 

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  //noLoop(); 
  size (1050, 550);
  PFont myFont = createFont("Helvetica-Bold", 400);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_text_1.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();
  E.textAlign(CENTER, BASELINE);
  E.textFont(myFont);
  E.textSize(400);

  E.hatchSpacing(3);
  E.strokeSpacing(3);
  E.stitchLength(20); 

  E.beginOptimize(1,1,100);

  //-----------------------
  E.strokeMode(PEmbroiderGraphics.TANGENT);
  E.stroke(0); 
  E.noFill();
  E.strokeWeight(18);
  E.text("a", 150, 250);

  E.setRenderOrder(PEmbroiderGraphics.STROKE_OVER_FILL);
  E.strokeMode(PEmbroiderGraphics.TANGENT);
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.stroke(0); 
  E.fill(255); 
  E.strokeWeight(18);
  E.text("a", 400, 250);
  
  E.setRenderOrder(PEmbroiderGraphics.FILL_OVER_STROKE);
  E.strokeMode(PEmbroiderGraphics.TANGENT);
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.stroke(0); 
  E.fill(255); 
  E.strokeWeight(18);
  E.text("a", 650, 250);

  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.fill(255); 
  E.noStroke();
  E.text("a", 900, 250);


  //-----------------------
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.stroke(0); 
  E.noFill();
  E.strokeWeight(18);
  E.text("a", 150, 500);

  E.setRenderOrder(PEmbroiderGraphics.STROKE_OVER_FILL);
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.stroke(0); 
  E.fill(255); 
  E.strokeWeight(18);
  E.text("a", 400, 500);
  
  E.setRenderOrder(PEmbroiderGraphics.FILL_OVER_STROKE);
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.stroke(0); 
  E.fill(255); 
  E.strokeWeight(18);
  E.text("a", 650, 500);

  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.fill(255); 
  E.noStroke();
  E.text("a", 900, 500);

  E.endOptimize();
  //-----------------------
  //E.optimize(); // VERY SLOW -- can take MINUTES -- but ESSENTIAL!!!
  //E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_text_1.png");
}

void draw(){
  background(128);
  E.visualize(true,false,false,frameCount*10);
}
