// Test program for the PEmbroider library for Processing:
import processing.embroider.*;
PEmbroiderGraphics E;

import static processing.embroider.PEmbroiderGraphics.*; 
// Importing this allows you to use shorthand codes. 
// For example, instead of writing: PEmbroiderGraphics.PARALLEL
// you can just write: PARALLEL

float shapeCoords[][] = {
  {579, 51}, {712, 59}, {716, 182}, {744, 300}, {748, 345},
  {749, 389}, {732, 470}, {716, 510}, {692, 551}, {648, 618},
  {585, 670}, {471, 699}, {372, 729}, {282, 711}, {169, 643},
  {88, 562}, {50, 226}, {120, 159}, {252, 183}, {244, 350},
  {137, 234}, {137, 350}, {323, 487}, {293, 103}, {365, 78},
  {508, 459}, {496, 279}, {575, 276}, {665, 382}, {628, 119},
  {598, 228}, {474, 154}};


//--------------------------------------------
void setup() {
  size (1024, 768);
  pixelDensity(1); // needed for Processing 4.4+
  noLoop();
  
  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_AxiDraw_plotter.svg");
  E.setPath(outputFilePath);

  E.beginDraw();
  E.clear();
  E.strokeWeight(1);
  E.fill(0, 0, 0);
  E.stroke(0,0,0);
  
  //===========================
  // PLOTTER-SPECIFIC COMMANDS: 
  // Set this to false so that there aren't superfluous waypoints on straight lines:
  E.toggleResample(false);
  // Set this to false so that there aren't connecting lines between shapes. 
  // Note that you'll still be able to pre-visualize the connecting lines 
  // (the plotter path) if you set E.visualize(true, true, true);
  E.toggleConnectingLines(false);
  // This affects the visual quality of inset/offset curves for CONCENTRIC fills:
  E.CONCENTRIC_ANTIALIGN = 0.0;
  //===========================
 
  // Set important stitch properties
  E.hatchSpacing(8);
  E.setStitch(15, 20, 0);

  // Draw custom shape
  E.hatchMode(CONCENTRIC);
  E.pushMatrix();
  E.translate(200, 0);
  E.beginShape();
  for (int i=0; i<shapeCoords.length; i++) {
    E.vertex(shapeCoords[i][0], shapeCoords[i][1]);
  }
  E.endShape(CLOSE);
  E.popMatrix();

  // Draw some circles in different ways
  E.hatchMode(CONCENTRIC);
  E.circle(125, 125, 200);
  
  E.hatchMode(SPIRAL);
  E.toggleResample(true);
  E.circle(125, 350, 200);
  
  E.hatchMode(PARALLEL);
  E.hatchAngleDeg(60);
  E.noStroke();
  E.circle(125, 575, 200);
  
  
  //-----------------------
  E.optimize(); // This is slow, but good and important!
  E.visualize(true, true, true);
  
  // Write out the SVG file for the plotter
  E.endDraw(); // write out the file
  
  // Save a screenshot
  save("PEmbroider_AxiDraw_plotter.png");
}


//--------------------------------------------
void draw() {
  ;
}
