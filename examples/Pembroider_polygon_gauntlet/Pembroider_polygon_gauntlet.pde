// Test program for the PEmbroider library for Processing:
// Testing PEmbroider with a variety of challenging polygons
// from https://github.com/LingDong-/interesting-polygon-archive.
// Press a key to see the next polygon, then wait a moment.

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  size(800, 800);
  noLoop();
  E = new PEmbroiderGraphics(this);
}


//---------------------------------------------
int i = 0;
void draw() {
  background(200);
  E.clear();
  println(POLYGON_NAMES[i]);
  PGraphics pg = createGraphics(800, 800);
  pg.beginDraw();
  pg.background(0);
  pg.noStroke();
  pg.fill(255);
  
  pg.beginShape();
  for (int j = 0; j < POLYGONS[i][0].length; j++) { 
    pg.vertex(POLYGONS[i][0][j][0], POLYGONS[i][0][j][1]);
  }
  for (int k = 1; k < POLYGONS[i].length; k++) {
    pg.beginContour();
    for (int j = 0; j < POLYGONS[i][k].length; j++) { 
      pg.vertex(POLYGONS[i][k][j][0], POLYGONS[i][k][j][1]);
    }
    pg.endContour();
  }
  pg.endShape();
  pg.endDraw();

  E.hatchSpacing(4);
  E.hatchMode(E.SATIN);
  E.satinMode(E.BOUSTROPHEDON);
  E.fill(0);
  E.image(pg, 0, 0);
  E.visualize();

  fill(0);
  noStroke();
  text("PRESS ANY KEY FOR NEXT POLYGON", 10, 20);
}

//---------------------------------------------
void keyPressed() {
  saveFrame("ouput-####.png"); 
  i = (i+1) % POLYGONS.length;
  redraw();
}
