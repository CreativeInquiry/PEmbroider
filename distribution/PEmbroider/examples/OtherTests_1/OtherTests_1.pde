// LANDFILL; ignore this program.
// Simple test program for the PEmbroider library for Processing

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  size (512, 512);
  E = new PEmbroiderGraphics(this, width, height);

  // Note: We use sketchPath() because a FULL path is necessary, 
  // e.g. "/Users/username/Documents/..." etcetera.
  // We can write .vp3 or .dst files.
  String outputFilePath = sketchPath("out.vp3");
  E.setPath(outputFilePath); 
  E.beginDraw(); 
  E.clear();
  
  test_hatch_parallel(); // OR see other possible tests, below
  // test_hatch_concentric(); 
  // test_cull();
  // test_cull2(); 
  // test_spiral(); 
  // test_image(); 
  // test_perlin(); 
  // test_perlin2(); 
  // test_field(); 

  E.visualize();
  //E.optimize(); // slow but good and important
  //E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}


//--------------------------------------------
void test_hatch_parallel() {
  E.fill(0);
  E.circle(50, 50, 50);
  E.circle(120, 30, 120);
  E.quad(10, 10, 80, 3, 30, 60, 0, 40);

  E.HATCH_MODE = PEmbroiderGraphics.CROSS;
  E.noStroke();

  E.beginShape();
  E.vertex(120, 120);
  E.vertex(150, 250);
  E.vertex(50, 240);
  E.vertex(50, 200);
  E.vertex(100, 220);
  E.vertex(100, 120);
  E.endShape(CLOSE);

  E.ellipse(200, 60, 100, 50);

  E.HATCH_ANGLE = 0;
  E.HATCH_SPACING = 8;
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.triangle(250, 200, 150, 250, 300, 280);
}

//--------------------------------------------
void test_hatch_concentric() {
  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  E.fill(0, 0, 255);
  E.stroke(0);
  E.beginShape();
  E.vertex(100, 200);
  E.vertex(150, 100);
  E.vertex(120, 50);
  E.vertex(150, 30);
  E.vertex(200, 50);
  E.vertex(180, 120);
  E.vertex(180, 180);
  E.endShape(CLOSE);
}


//--------------------------------------------
void test_cull() {
  E.beginCull();
  test_hatch_concentric();
  E.CIRCLE_DETAIL = 20;
  E.fill(255, 0, 0);
  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;

  E.circle(50, 20, 100);
  E.circle(50, 50, 40);

  E.endCull();
}


//--------------------------------------------
void test_cull2() {
  E.fill(0);
  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  E.beginCull();
  for (int i = 0; i < 100; i++) {
    float cD = random(50, 200-i); // diameter
    float cL = random(0, width - cD);
    float cT = random(0, height - cD); 

    E.circle(cL, cT, cD);
  }
  E.endCull();
}

//--------------------------------------------
void test_spiral() {
  E.HATCH_MODE = PEmbroiderGraphics.SPIRAL;
  E.HATCH_SPACING = 4;
  E.fill(0, 0, 255);
  E.noStroke();
  E.beginShape();
  E.vertex(100, 200);
  E.vertex(150, 100);
  E.vertex(120, 50);
  E.vertex(150, 30);
  E.vertex(200, 50);
  E.vertex(180, 120);
  E.vertex(180, 180);
  E.endShape(CLOSE);

  E.circle(30, 30, 100);

  E.beginShape();
  E.vertex(200, 180);
  E.vertex(230, 190);
  E.vertex(250, 240);
  E.vertex(200, 280);
  E.vertex(150, 240);
  E.endShape(CLOSE);

  E.rect(200, 30, 90, 70);
}


//--------------------------------------------
void test_image() {
  E.beginCull();
  stroke(0);

  E.fill(255, 100, 0);
  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  E.circle(0, 0, width);

  String imagePath = dataPath("matisse.png"); 
  println("imagePath = " + imagePath); 
  PImage img = loadImage(imagePath);
  E.fill(0, 0, 255);
  E.image(img, 0, 0, width, height);

  E.endCull();
}


//--------------------------------------------
void test_perlin() {
  E.HATCH_MODE = PEmbroiderGraphics.PERLIN;
  E.noStroke();
  E.fill(0);

  E.circle(100, 100, 500);
  E.quad(10, 10, 80, 20, 50, 90, 5, 30);
  E.rect(100, 5, 100, 100);
}


//--------------------------------------------
void test_perlin2() {
  E.HATCH_MODE = PEmbroiderGraphics.PERLIN;
  E.noStroke();
  E.fill(0);

  E.circle(0, 0, 200);

  E.HATCH_SPACING = 8;
  E.circle(200, 0, 200);

  E.HATCH_SPACING = 2;
  E.circle(0, 200, 200);

  E.HATCH_SPACING = 4;
  E.HATCH_SCALE = 0.2;
  E.circle(200, 200, 200);
}


//--------------------------------------------
void test_field() {
  class MyVecField implements PEmbroiderGraphics.VectorField {
    public PVector get(float x, float y) {
      x*=0.01;
      y*=0.01;
      return new PVector(1, noise(x, y)-0.5).mult(10);
    }
  }

  MyVecField mvf = new MyVecField();
  E.HATCH_MODE = PEmbroiderGraphics.VECFIELD;
  E.HATCH_VECFIELD = mvf;

  E.noStroke();
  E.fill(0);

  E.circle(100, 100, 500);
  E.quad(10, 10, 80, 20, 50, 90, 5, 30);
  E.rect(100, 5, 100, 100);
}
