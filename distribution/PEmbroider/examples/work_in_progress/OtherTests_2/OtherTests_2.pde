import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {

  // class _ extends PEmbroiderGraphics{_(){super(null,0,0);}};

  size(500, 500);
  E =  new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("other_2.vp3");
  E.setPath(outputFilePath); 
  PEmbroiderHatchSpine.setGraphics(E);

  // test_perlin2(); 
  // test_cull2(); 
  // test_field();
  // test_tsp(); 
  // test_tsp2();
  // test_iso(); 
  // test_cheese(); 
  // test_contour_api(); 
  // test_bezier(); 
  // test_text();
  // test_stroke();
  // test_ttf(); 
  // test_ttf2();
  // test_merge(); 

  // E.optimize();
  E.visualize(true, false, true);
  // E.endDraw();
}

void draw() {
}



//---------------------------------------------
void other() {
  // LANDFILL
  
  
  //E.fill(0);
  //E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  //E.HATCH_ANGLE = QUARTER_PI;

  //E.circle(100,60,80);

  //E.HATCH_ANGLE = -QUARTER_PI;
  //E.circle(150,60,80);



  //E.beginCull();

  //E.HATCH_ANGLE = 0;
  //E.circle(150,160,80);

  //E.HATCH_ANGLE = HALF_PI;
  //E.circle(100,160,80);



  //E.endCull();



  //E.visualize();
  //E.endDraw();

  //E.fill(0);
  //E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  //E.rect(0,0,width,height);
  //E.visualize();
  //E.endDraw();

  //ArrayList<PVector> poly = new ArrayList<PVector>();
  //poly.add(new PVector(100,200));
  //poly.add(new PVector(150,100));
  //poly.add(new PVector(120,50));
  //poly.add(new PVector(150,30));
  //poly.add(new PVector(200,50));
  //poly.add(new PVector(180,120));
  //poly.add(new PVector(180,180));

  //ArrayList<PVector> poly2 = E.resampleN(poly,100);
  //E.pushPolyline(poly,color(0));
  ////E.pushPolyline(poly2,color(0));

  //println(poly2.size());

  //for (int i = 0; i < poly2.size(); i++){

  // circle(poly2.get(i).x,poly2.get(i).y,39); 
  // circle(poly2.get(i).x,poly2.get(i).y,3); 
  //}


  //E.HATCH_MODE = PEmbroiderGraphics.PERLIN;
  //E.noStroke();
  //E.fill(0);

  //E.circle(100,100,500);
  //E.quad(10,10,80,20,50,90,5,30);
  //E.rect(100,5,100,100);

  //E.BEZIER_DETAIL = 21;
  //E.translate(-100,-100);
  //E.scale(2);
  //E.beginShape();
  //E.vertex(100,100);
  //E.bezierVertex(200,200,300,200,400,100);
  //E.quadraticVertex(200,200,400,100);
  //E.endShape();
  //E.rect(20,20,100,100);
  //E.stroke(255,0,0);
  //E.rect(220,320,20,100);


  //E.line(0,0,100,100);

  //image(im,0,0);
  //E.stroke(255,0,0);
  //E.rect(10,10,100,100);
  //E.stroke(0,255,0);
  //E.rect(140,10,100,100);
  //PImage im = loadImage("/Users/studio/Downloads/matisse_inv.png");

  //ArrayList<ArrayList<PVector>> p = E.hatchDrunkWalkRaster(im,10,9999);
  //for (int i = 0; i < p.size(); i++){
  //  E.pushPolyline(p.get(i),0,0);
  //}
}


void test_cull2() {
  E.fill(0);
  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  E.beginCull();
  for (int i = 0; i < 200; i++) {
    E.circle(random(width), random(height), random(30, 120));
  }
  E.endCull();
}


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

void test_tsp() {
  ArrayList<ArrayList<PVector>> polylines = new ArrayList<ArrayList<PVector>>();
  for (int i = 0; i < 500; i++) {
    polylines.add(new ArrayList<PVector>());
    polylines.get(polylines.size()-1).add(new PVector(random(width), random(height)));
    polylines.get(polylines.size()-1).add(new PVector(random(width), random(height)));
  }


  for (int i = 0; i < polylines.size(); i++) {
    stroke(0);
    //strokeWeight(3);
    line(polylines.get(i).get(0).x, polylines.get(i).get(0).y, 
      polylines.get(i).get(1).x, polylines.get(i).get(1).y);
  }
  ArrayList<ArrayList<PVector>> polylines2 = PEmbroiderTSP.solve(polylines);
  println(polylines2.size());
  for (int i = 0; i < polylines2.size(); i++) {
    stroke(0, 255, 0);
    strokeWeight(1);
    // line(polylines2.get(i).get(0).x,polylines2.get(i).get(0).y,
    //     polylines2.get(i).get(1).x,polylines2.get(i).get(1).y);
    stroke(255, 0, 0);
    if (i > 0) {
      line(polylines2.get(i-1).get(1).x, polylines2.get(i-1).get(1).y, 
        polylines2.get(i).get(0).x, polylines2.get(i).get(0).y);
    }
  }
}


void test_tsp2() {

  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.noStroke();
  E.fill(0);
  E.circle(100, 100, 500);
  E.quad(10, 10, 80, 20, 50, 90, 5, 30);
  E.rect(100, 5, 100, 100);

  E.optimize();
  println("Original:"+PEmbroiderTSP.sumLengthPolylines(E.polylines));
}

void test_iso() {

  PGraphics pg = createGraphics(300, 300);

  pg.beginDraw();
  pg.background(0);
  pg.noStroke();
  pg.fill(255);
  pg.circle(150, 150, 250);
  pg.fill(0);
  pg.rect(120, 130, 30, 30);
  pg.rect(20, 180, 30, 30);
  pg.rect(20, 180, 30, 30);
  pg.rect(80, 100, 10, 10);
  pg.rect(200, 120, 50, 50);
  pg.endDraw();

  noFill();
  ArrayList<ArrayList<ArrayList<PVector>>> isolines = PEmbroiderTrace.findIsolines(pg, -1, 5f);
  for (int i = 0; i < isolines.size(); i++) {
    for (int j = 0; j < isolines.get(i).size(); j++) {
      beginShape();
      for (int k = 0; k < isolines.get(i).get(j).size(); k++) {
        vertex(isolines.get(i).get(j).get(k).x, isolines.get(i).get(j).get(k).y);
      }
      endShape();
    }
  }
}

void test_cheese() {
  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  //E.HATCH_COUNT = 20;

  PGraphics pg = createGraphics(width, height);
  pg.beginDraw();
  pg.background(0);
  pg.noStroke();
  pg.fill(255);

  pg.quad(50, 100, 500, 50, 500, 400, 50, 500);
  pg.triangle(50, 99, 500, 50, 300, 1);

  pg.fill(0);
  for (int i = 0; i < 30; i++) {
    pg.circle(random(width), random(height), random(20));
  }

  pg.endDraw();
  E.hatchRaster(pg);
}

void test_contour_api() {
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  //E.HATCH_FORCE_RASTER = true;
  E.HATCH_SPACING =5;
  E.fill(0);
  E.noStroke();

  E.beginShape();

  E.vertex(10, 10);
  E.vertex(200, 10);
  E.vertex(100, 200);
  E.vertex(10, 200);

  E.beginContour();
  E.vertex(80, 30);
  E.vertex(30, 30);
  E.vertex(80, 80);
  E.endContour();


  E.beginContour();
  E.vertex(50, 140);
  E.vertex(100, 140);
  E.vertex(50, 90);
  E.endContour();

  E.endShape(CLOSE);

  E.optimize();
  E.visualize(false, false, false);
  E.endDraw();
}


void test_bezier() {
  E.noFill();
  E.fill(0);
  E.beginShape();

  E.vertex(20, 20);
  E.rationalVertex(80, 20, 50, 50, 1);
  E.rationalVertex(20, 80, 80, 80, 1);
  E.vertex(80, 10);
  E.endShape(CLOSE);
}

void test_text() {
  E.textSize(3);
  E.textFont(PEmbroiderFont.GOTHIC_GERMAN_TRIPLEX);
  E.text("Hello", 50f, 100f);
  E.textFont(PEmbroiderFont.SCRIPT_COMPLEX);
  E.text("World!", 50f, 200f);
}

void test_stroke() {
  E.strokeWeight(20);
  E.STROKE_SPACING = 3;
  E.stroke(0);
  E.strokeJoin(MITER);
  E.STROKE_MODE = PEmbroiderGraphics.TANGENT;
  E.noFill();
  E.beginShape();
  E.vertex(60, 60);
  E.quadraticVertex(240, 60, 150, 150);
  E.quadraticVertex(60, 240, 240, 240);
  E.vertex(240, 44);
  //E.vertex(60, 30);
  E.endShape(CLOSE);
}

void test_ttf() {
  PFont f = createFont("PingFangTC-Semibold", 32);
  E.HATCH_MODE = PEmbroiderGraphics.CROSS;
  E.STROKE_MODE = PEmbroiderGraphics.TANGENT;
  E.fill(0);
  E.strokeWeight(1);
  E.textFont(f);
  E.textSize(200);
  E.text("我能吞下玻璃而不伤身体", 20, 100);
}

void test_ttf2() {
  PFont f = createFont("Helvetica", 200);
  E.HATCH_MODE = PEmbroiderGraphics.PARALLEL;
  E.STROKE_MODE = PEmbroiderGraphics.TANGENT;


  E.textFont(f);
  E.textSize(200);

  E.fill(0);
  E.noStroke();
  E.text("A", 20, 300);

  E.fill(0);
  E.stroke(0);
  E.strokeWeight(1);
  E.text("A", 185, 300);

  E.fill(0);
  E.stroke(0);
  E.strokeWeight(3);
  E.text("A", 350, 300);

  E.fill(0);
  E.stroke(0);
  E.STROKE_SPACING = 3;
  E.strokeWeight(10);
  E.text("A", 515, 300);
}


void test_merge() {
  PGraphics pg = createGraphics(width, height);
  E.HATCH_MODE = PEmbroiderGraphics.CONCENTRIC;
  pg.beginDraw();
  pg.background(0);
  pg.fill(255);
  pg.stroke(0);
  pg.strokeWeight(3);
  pg.rect(50, 100, 100, 100);
  pg.circle(150, 100, 100);


  pg.circle(350, 100, 100);
  pg.rect(250, 100, 100, 100);
  pg.endDraw();
  E.hatchRaster(pg, 0, 0);
}



void test_image() {
  E.beginCull();
  stroke(0);

  E.fill(255, 100, 0);
  E.HATCH_MODE = PEmbroiderGraphics.SPIRAL;
  //E.circle(0,0,width);

  PImage im = loadImage("/Users/studio/Downloads/matisse.png");
  E.fill(0, 0, 255);
  E.image(im, 0, 0, width, height);
  E.endCull();
}
