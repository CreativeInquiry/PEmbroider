import processing.embroider.*;

PEmbroiderGraphics E;

void setup(){
  size(600,600);
  smooth();
  E = new PEmbroiderGraphics(this);
  //E.noStroke();
  E.fill(0);
  E.hatchMode(E.SATIN);
  //noLoop();
  
  PEmbroiderBSA BSA = new PEmbroiderBSA(width,height);
  
  BSA.rect(300,200,100,100);
  BSA.operator(PEmbroiderBSA.AND);
  BSA.circle(400,200,100);
  
  BSA.operator(PEmbroiderBSA.OR);
  
  BSA.circle(100,100,100);
  BSA.operator(PEmbroiderBSA.XOR);
  BSA.circle(150,100,100);

  BSA.operator(PEmbroiderBSA.OR);
  
  BSA.circle(100,250,100);
  BSA.circle(150,250,100);
  
  BSA.rect(300,50,100,100);
  BSA.operator(PEmbroiderBSA.DIFFERENCE);
  BSA.circle(400,50,100);
  
  E.image(BSA,0,0);
}

void draw(){
  background(255);

  E.visualize(false,true,true,frameCount);

}
