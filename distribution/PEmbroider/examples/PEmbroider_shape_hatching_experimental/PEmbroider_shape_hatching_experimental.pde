// Test program for the PEmbroider library for Processing:
// EXPERIMENTAL hatching modes: "spine" with PEmbroiderHatchSpine, 

import processing.embroider.*;
PEmbroiderGraphics E;

float shapeCoords[][] = {
  {579, 51}, {712, 59}, {716, 182}, {744, 300}, {748, 345}, 
  {749, 389}, {732, 470}, {716, 510}, {692, 551}, {648, 618}, 
  {585, 670}, {471, 699}, {372, 729}, {282, 711}, {169, 643}, 
  {88, 562}, {50, 226}, {120, 159}, {252, 183}, {244, 350}, 
  {137, 234}, {137, 350}, {323, 487}, {293, 103}, {365, 78}, 
  {508, 459}, {496, 279}, {575, 276}, {665, 382}, {628, 119}, 
  {598, 228}, {474, 154}};


void setup() {
  noLoop(); 
  size (1250, 500);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_shapes_hatching_experimental.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();
  E.ellipseMode(CORNER);
  E.strokeWeight(1); 
  E.stroke(0, 0, 0); 
  E.fill(0, 0, 0);  

  //-----------------------
  // Draw "spine" hatch mode (EXPERIMENTAL), which is 
  // based on distance transform & skeletonization.
  // Here, we use the (best) "vector field" (VF) version.
  //
  // Create a bitmap image by drawing to an offscreen graphics buffer.
  
  PGraphics pg = createGraphics(width, height); 

  pg.beginDraw();
  pg.background(0); 
  pg.ellipseMode(CORNER);
  pg.noStroke(); 
  pg.fill(255); 

  pg.beginShape();
  pg.vertex(50, 75);
  pg.vertex(50, 200);
  pg.vertex( 330, 200);
  pg.vertex( 330, 125);
  pg.quadraticVertex(250, 125, 200, 75);
  pg.vertex(175, 50);
  pg.endShape(CLOSE);

  pg.triangle (410, 200, 460, 050, 510, 400-200); 
  pg.arc      (525, 050, 150, 150, 0, PI*1.25, PIE); 
  pg.arc      (700, 050, 150, 150, 0, PI*1.25, CHORD); 
  pg.square   (300,250,200);
  pg.triangle (550,450, 750, 450, 650, 450-173);
  pg.rect     (770,250, 100, 200);

  pg.pushMatrix(); 
  pg.translate(875, 0); 
  pg.scale(0.45); 
  pg.beginShape();
  for (int i=0; i<shapeCoords.length; i++) {
    pg.vertex(shapeCoords[i][0], shapeCoords[i][1]);
  }
  pg.endShape(CLOSE);
  pg.popMatrix();
  
  pg.ellipse   (50,275,200,150);

  pg.endDraw(); 
  


  E.hatchSpacing(3);
  E.setStitch(10, 30, 0);
  PEmbroiderHatchSpine.setGraphics(E);
  PEmbroiderHatchSpine.hatchSpineVF(pg);


  //-----------------------
  //E.optimize(); // slow, but very good and very important
  E.visualize();
  //E.endDraw(); // write out the files
  //save("PEmbroider_shape_hatching_experimental.png");
  
}
