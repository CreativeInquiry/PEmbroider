// Test program for the PEmbroider library for Processing:
// Testing SATIN hatching, with Theodore Gray's shape from 
// http://home.theodoregray.com/stitchblog/2015/7/10/embroidered-animation-finally

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

//-----------------------------------------------------
void setup() {
  noLoop(); 
  size (800, 800);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_satin_hatching_1.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();

  // E.satinMode(E.ZIGZAG); // Original style
  // E.satinMode(E.SIGSAG); // Zigzag parallelized
  E.satinMode(E.BOUSTROPHEDON); // The new mode, back-and-forth!
  E.SATIN_RESAMPLING_OFFSET_FACTOR=0.2;

  E.noStroke(); 
  E.fill(0, 0, 0); 
  E.hatchSpacing(1.66);
  E.hatchAngleDeg(45);
  E.hatchMode(E.SATIN);
  E.setStitch(10, 60, 0); 
  

  E.beginShape();
  for (int i=0; i<shapeCoords.length; i++) {
    float px = shapeCoords[i][0];
    float py = shapeCoords[i][1];
    E.vertex(px, py);
  }
  E.endShape(CLOSE);
  
  //E.optimize(); // slow, but very important for file output!
  E.visualize(true, true, true);
  //E.endDraw(); // write out the file
  //save("PEmbroider_satin_hatching_1.png");
}
