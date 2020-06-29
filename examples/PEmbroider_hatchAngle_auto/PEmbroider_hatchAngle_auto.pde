import processing.embroider.*;

PEmbroiderGraphics E;

void setup(){
  size(400,400);
  smooth();
  E = new PEmbroiderGraphics(this);
  E.noStroke();
  E.fill(0);
  E.hatchSpacing(8);
  E.hatchAngle(E.AUTO);

  
}
void draw(){
  background(255);
  E.clear();
  E.ellipseMode(CORNERS);
  E.hatchMode((frameCount/100)%2==0?E.PARALLEL:E.SATIN);
  E.ellipse(50,50,mouseX,mouseY);
  E.beginShape(); E.vertex(100, 100); E.vertex(160, 110); E.vertex(250, 170); E.vertex(300, 250); E.vertex(330, 290); E.vertex(280, 290); E.vertex(150, 180); E.endShape(CLOSE);
  E.visualize();
}
