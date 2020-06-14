// Test program for the PEmbroider library for Processing:
// Filling a pre-loaded "image" using various hatches.

import processing.embroider.*;
import static processing.embroider.PEmbroiderGraphics.*; // PEmbroiderGraphics.PARALLEL -> PARALLEL

PEmbroiderGraphics E;


void randomStyle(){
    E.hatchMode(random(1f)<0.5?PARALLEL:CONCENTRIC);
    if (random(1f)<0.5){
      E.noFill(); 
      E.strokeWeight(1+random(10));
    }else{
      E.fill(0);
      E.strokeWeight(1);
      
    }
}

void setup() {
  size(1000, 500); 
  noLoop(); 
  E = new PEmbroiderGraphics(this, width, height);

  
  PShape duck = loadShape("duck.svg");
  
  E.fill(0);
  
  E.hatchMode(PERLIN);
  E.shape(duck,50,0,300,300);
  
  E.hatchMode(CROSS);
  E.shape(duck,400,200);
  
  E.hatchMode(CONCENTRIC);
  E.shape(duck,550,200);
  
  E.hatchMode(PARALLEL);
  E.shape(duck,700,200);
  
  E.noFill();
  E.strokeWeight(10);
  E.strokeMode(PERPENDICULAR);
  E.shape(duck,850,200);
  
  E.strokeWeight(1);
  for (int i = 0; i < 50; i++){
    float x = random(0,width);
    float y = random(300,height);
    float w = random(30,100);
    float r = random(min(w,50)/2);

    randomStyle();
    E.rect(x,y,w,50,r);
  }


  for (int i = 0; i < 20; i++){
    float x = random(300,width);
    float y = random(0,100);
    int mode = random(1f) < 0.5 ? PIE : CHORD;
    randomStyle();
    E.arc(x,y,100,100,random(TWO_PI),random(TWO_PI),mode);
  }

  //-----------------------
  //E.optimize(); // slow, but good and important
  E.visualize();
  E.endDraw(); // write out the file
}


//--------------------------------------------
void draw() {
  ;
}
