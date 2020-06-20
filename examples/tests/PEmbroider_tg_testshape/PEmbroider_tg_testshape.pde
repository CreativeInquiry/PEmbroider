// Test program for the PEmbroider library for Processing:
// Testing Theodore Gray's shape from 
// http://home.theodoregray.com/stitchblog/2015/7/10/embroidered-animation-finally

import processing.embroider.*;
PEmbroiderGraphics E;

float tgShapeCoords[][] = {
  {579, 51}, {712, 59}, {716, 182}, {744, 300}, {748, 345}, 
  {749, 389}, {732, 470}, {716, 510}, {692, 551}, {648, 618}, 
  {585, 670}, {471, 699}, {372, 729}, {282, 711}, {169, 643}, 
  {88, 562}, {50, 226}, {120, 159}, {252, 183}, {244, 350}, 
  {137, 234}, {137, 350}, {323, 487}, {293, 103}, {365, 78}, 
  {508, 459}, {496, 279}, {575, 276}, {665, 382}, {628, 119}, 
  {598, 228}, {474, 154}};

//-----------------------------------------------------
void setup() {
  noLoop(); 
  size (800, 800);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("testshape.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();

  E.noStroke(); 
  E.fill(0, 0, 0); 
  E.hatchSpacing(5.0);
  E.hatchAngleDeg(45);
  E.hatchMode(PEmbroiderGraphics.PARALLEL);
  E.setStitch(10, 50, 0); 

  E.beginShape();
  for (int i=0; i<tgShapeCoords.length; i++) {
    float px = tgShapeCoords[i][0];
    float py = tgShapeCoords[i][1];
    E.vertex(px, py);
  }
  E.endShape(CLOSE);
  

  E.optimize(); // slow, but very good and very important
  E.visualize(true, true, true);
  //E.endDraw(); // write out the file
}
