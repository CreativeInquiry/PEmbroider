// Test program for the PEmbroider library for Processing:

import processing.embroider.*;
PEmbroiderGraphics E;
PGraphics PG; 


void setup() {
  size(1000, 500); 
  noLoop(); 

  PG = createGraphics(500, 500);

  E = new PEmbroiderGraphics(this, 500, 500);
  E.setPath ( sketchPath("PEmbroider_shape_merging.vp3")); 

  renderRasterGraphics(); 
  generateEmbroideryFromRasterGraphics();

  // draw the raster graphics, for reference
  image(PG, 0, 0);

  pushMatrix(); 
  translate(500,0); 
  E.visualize();
  popMatrix(); 
  E.optimize(); // slow, but good and important
  // E.endDraw(); // write out the file
}


//--------------------------------------------
void generateEmbroideryFromRasterGraphics() {
  E.beginDraw(); 
  E.clear();
  E.fill(0,0,0);
  E.stroke(0); 

  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  E.HATCH_SPACING = 3;
  E.hatchRaster(PG, 0, 0);
}


//--------------------------------------------
void renderRasterGraphics() {
  PG.beginDraw();
  PG.background(0);

  PG.fill(255);
  PG.stroke(0);
  PG.strokeWeight(3);
  
  // Circle biting from a square
  PG.rect(50, 100, 100, 100);
  PG.circle(150, 100, 100);

  // Square biting from a circle
  PG.circle(400, 100, 100);
  PG.rect(300, 100, 100, 100);

  // Square and circle merged
  PG.noStroke();
  PG.rect(52, 302, 96, 96);
  PG.circle(152, 302, 96);

  // Merged shapes with holes
  PG.noStroke();
  PG.rect(250, 300, 150, 150);
  PG.circle(402, 302, 96);
  PG.fill(0); 
  PG.circle(405, 405, 40);
  PG.circle(300, 350, 40);

  PG.endDraw();
}


//--------------------------------------------
void draw() {
  ;
}
