// Test program for the PEmbroider library for Processing:
// Lines and curves

import processing.embroider.*;
PEmbroiderGraphics E;


void setup() {
  noLoop(); 
  size (800, 600);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_lines_1.vp3");
  E.setPath(outputFilePath);
}

void draw() {
  background(220); 
  E.beginDraw(); 
  E.clear();
  E.noFill(); 
  
  
  //-----------------------
  E.strokeWeight(1); 
  E.ellipseMode(CENTER);
  for (int i=0; i<6; i++){
    float cx = map(i,0,5, 100,width-100);
    float cy = 100; 
    float minStitchLen = map(i,0,5, 5, 40);
    E.setStitch(minStitchLen, 20, 0.0);
    E.circle(cx,cy, 100); 
  }
  
  

  //-----------------------
  E.strokeWeight(1); 
  E.setStitch(10, 40, 0.0);

  E.beginShape(); 
  float cx = 125; 
  float cy = 400;
  float r = 40;
  for (int i=0; i<=360; i++) {
    float t = map(i, 0, 360, 0, TWO_PI);
    float x = cx + r*sin(t) + (r*1.33)*cos(2*t); 
    float y = cy + r*cos(t) + (r*1.33)*sin(2*t); 
    E.vertex (x, y);
  }
  E.endShape();
  
  
  //-----------------------
  E.setStitch(4, 20, 0.0);
  E.beginShape();
  float px = 550;
  float py = 400; 
  float t = 0; 
  float rr = 0.25;
  for (int i=0; i<150; i++) {
    t += 10.0; 
    E.vertex(px,py);
    px += i*rr*sin(radians(t)); 
    py += i*rr*cos(radians(t)); 
  }
  E.endShape();
  
  
  //E.strokeSpacing(2.0);
  //E.strokeMode(PEmbroiderGraphics.PERPENDICULAR); 

  //-----------------------
  // E.optimize(); // slow, but very good and important
  E.visualize();
  // E.endDraw(); // write out the file
  // save("Pembroider_lines_2.png");  //saves a png of design from canvas
}
