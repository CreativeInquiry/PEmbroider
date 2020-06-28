import processing.embroider.*;

// THIS EXAMPLE IS NOT READY FOR DOCUMENTATION
// -GL

PEmbroiderGraphics E;

void setup(){
  size(600,600);
  smooth();
  E = new PEmbroiderGraphics(this);
  //E.noStroke();
  E.fill(0);
  E.hatchMode(E.SATIN);
  //noLoop();
  
  PEmbroiderBooleanShapeGraphics BSG = new PEmbroiderBooleanShapeGraphics(width,height);
  
  BSG.rect(300,200,100,100);
  BSG.operator(PEmbroiderBooleanShapeGraphics.AND);
  BSG.circle(400,200,100);
  
  BSG.operator(PEmbroiderBooleanShapeGraphics.OR);
  
  BSG.circle(100,100,100);
  BSG.operator(PEmbroiderBooleanShapeGraphics.XOR);
  BSG.circle(150,100,100);

  BSG.operator(PEmbroiderBooleanShapeGraphics.OR);
  
  BSG.circle(100,250,100);
  BSG.circle(150,250,100);
  
  BSG.rect(300,50,100,100);
  BSG.operator(PEmbroiderBooleanShapeGraphics.DIFFERENCE);
  BSG.circle(400,50,100);
  
  E.image(BSG,0,0);
}

void draw(){
  background(255);

  E.visualize(false,true,true,frameCount);

}
