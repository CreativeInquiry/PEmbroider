// Interactive demo; move the mouse.

import processing.embroider.*;
PEmbroiderGraphics E;

void setup(){
  size(600,350);
  E = new PEmbroiderGraphics(this);
}

void draw(){
  background(200);
  E.clear();
  E.strokeMode(E.TANGENT);
  E.strokeWeight(30);
  
  // these also work:
  //E.strokeLocation(E.CENTER);
  //E.strokeLocation(E.INSIDE);
  //E.strokeLocation(E.OUTSIDE);
  
  float strokeLocationVal = map(mouseX,0,width,-1,1);
  E.strokeLocation(strokeLocationVal);
  E.fill(0);

  E.beginComposite();
  E.composite.rect(100,100,100,100);
  E.composite.circle(200,100,100);
  E.composite.triangle(170,190,280,200,200,260);
  E.composite.beginShape();
  E.composite.vertex(400,150);
  E.composite.vertex(500,100);
  E.composite.vertex(500,300);
  E.composite.vertex(400,300);
  
  // holes kinda work
  E.composite.beginContour();
  E.composite.vertex(410,290);
  E.composite.vertex(490,290);
  E.composite.vertex(420,180);
  E.composite.endContour();
  
  E.composite.endShape(CLOSE);
  E.endComposite();
 
  fill(0);
  text("strokeLocation("+nf(E.STROKE_LOCATION,1,2)+")",15,20);
  E.visualize(true, false, false);
}
