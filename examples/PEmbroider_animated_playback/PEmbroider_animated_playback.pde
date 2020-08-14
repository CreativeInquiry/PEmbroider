// Test program for the PEmbroider library for Processing:
// Demonstrates animated playback (previewing) of stitches.

//-----------------------------------------------------
import processing.embroider.*;
PEmbroiderGraphics E;
int stitchPlaybackCount = 0; 


float shapeCoords[][] = { /* A fussy 2D polygon */
  {579, 51}, {712, 59}, {716, 182}, {744, 300}, {748, 345}, 
  {749, 389}, {732, 470}, {716, 510}, {692, 551}, {648, 618}, 
  {585, 670}, {471, 699}, {372, 729}, {282, 711}, {169, 643}, 
  {88, 562}, {50, 226}, {120, 159}, {252, 183}, {244, 350}, 
  {137, 234}, {137, 350}, {323, 487}, {293, 103}, {365, 78}, 
  {508, 459}, {496, 279}, {575, 276}, {665, 382}, {628, 119}, 
  {598, 228}, {474, 154}};

//-----------------------------------------------------
void setup() {
  size (800, 800); 

  // In this demo, we just animate the stitch planning, 
  // -- so we haven't bothered setting up file output. 
  //
  // Set up a new PEmbroiderGraphics object. 
  E = new PEmbroiderGraphics(this, width, height);
  E.beginDraw(); 

  // Initialize PEmbroider settings.
  E.hatchSpacing(6.0);
  E.setStitch(10, 40, 0); 
  E.CONCENTRIC_ANTIALIGN = 0;
  E.satinMode(E.BOUSTROPHEDON);

  // It's fun to tinker with this angle in SATIN mode. 
  // E.hatchAngleDeg(0); 

  // Choose one of these hatch modes by un-commenting it!
  E.hatchMode(PEmbroiderGraphics.SATIN);  
  // E.hatchMode(PEmbroiderGraphics.PARALLEL);
  // E.hatchMode(PEmbroiderGraphics.CONCENTRIC);

  // Add an interesting polygon to the scene. 
  E.noStroke(); 
  E.fill(0, 0, 0); 
  E.beginShape();
  for (int i=0; i<shapeCoords.length; i++) {
    float px = shapeCoords[i][0];
    float py = shapeCoords[i][1];
    E.vertex(px, py);
  }
  E.endShape(CLOSE);

  // Optimize the embroidery path.
  // This can be slow, but it's very important.
  E.optimize();
}

//-----------------------------------------------------
void draw() {
  // Draw the world
  background(200); 
  renderShapeToScreen();
  E.visualize(false, true, true, stitchPlaybackCount);

  // Advance the playback of the stitches.
  //
  int hurryUp = 25; // How many stitches do we skip between frames.
  if (stitchPlaybackCount > (E.getNumStitches()+hurryUp)) {
    stitchPlaybackCount = 0; // restart the animation. 
  } else {
    /* If you want to export frames to make an animated GIF, 
     * uncomment the following line of code: */
    // saveFrame("output/myDesign_####.png");
  }
  stitchPlaybackCount += hurryUp;
}

//-----------------------------------------------------
void keyPressed() {
  // Reset playback if you press the space bar.
  if (key == ' ') stitchPlaybackCount = 0;
}

//-----------------------------------------------------
void renderShapeToScreen() {
  // Just display the shape faintly in the background.
  noStroke(); 
  fill(255, 255, 255, 50); 
  beginShape();
  for (int i=0; i<shapeCoords.length; i++) {
    float px = shapeCoords[i][0];
    float py = shapeCoords[i][1];
    vertex(px, py);
  }
  endShape(CLOSE);
}
