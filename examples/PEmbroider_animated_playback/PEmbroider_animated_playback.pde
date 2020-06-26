// Test program for the PEmbroider library for Processing:
// Animated playback (previewing) of stitches.

//-----------------------------------------------------
import processing.embroider.*;
PEmbroiderGraphics E;
int stitchPlaybackCount; 

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
  size (800, 800);
  stitchPlaybackCount = 0; 

  // Set up a new PEmbroiderGraphics object. 
  // In this demo, we just watch the stitch planning, 
  // so we don't bother setting up file output. 
  E = new PEmbroiderGraphics(this, width, height);
  E.beginDraw(); 
  
  // Initialize embroidery settings
  E.noStroke(); 
  E.fill(0, 0, 0); 
  E.hatchSpacing(6.0);
  E.setStitch(10, 40, 0); 
  E.hatchAngleDeg(90); 
  E.CONCENTRIC_ANTIALIGN = 0;

  // Choose one of these hatch modes
  E.hatchMode(PEmbroiderGraphics.SPIRAL);  // ?!?!?!?
  // E.hatchMode(PEmbroiderGraphics.SATIN);  
  // E.hatchMode(PEmbroiderGraphics.PARALLEL);
  // E.hatchMode(PEmbroiderGraphics.CONCENTRIC);

  // Add a complex shape to the scene
  E.beginShape();
  for (int i=0; i<shapeCoords.length; i++) {
    float px = shapeCoords[i][0];
    float py = shapeCoords[i][1];
    E.vertex(px, py);
  }
  E.endShape(CLOSE);
  
  // Optimize the embroidery path.
  // Can be slow, but very important.
  E.optimize(); 
}


//-----------------------------------------------------
void draw() {
  background(200); 
  renderShapeToScreen();

  E.visualize(false, true, true, stitchPlaybackCount);
  stitchPlaybackCount+=25;
}

//-----------------------------------------------------
void keyPressed() {
  if (key == ' ') {
    stitchPlaybackCount = 0;
  }
}

//-----------------------------------------------------
void renderShapeToScreen() {
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
