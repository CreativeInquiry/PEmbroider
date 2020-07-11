import processing.embroider.*;
PEmbroiderGraphics E;


void palette(int n){
  switch(n){
    case 0: E.fill(100,70,20);break;
    case 1: E.fill(250,120,50);break;
    case 2: E.fill(255,200,200);break;
    case 3: E.fill(250,240,230);break;
    case 4: E.fill(220,200,80);break;
    case 5: E.fill(10,50,5);break;
    
  }
}
void dostroke(){
  E.stroke((E.currentFill>>16) & 255,(E.currentFill>>8) & 255,(E.currentFill) & 255);
}

void halfPetal(float w, float h, float cd, float mh){
  int n = 100;
  E.beginShape();
  float x0[] = new float[n];
  x0[0] = 0;
  
  for (int i = 1; i < n; i++){
    float t = (float)i/(float)n*h/mh;
    x0[i] = pow(t,6)*cd;
  }
    
  for (int i = 0; i < n; i++){
    float t = (float)i/(float)n;
    float s = t*1.35;
    float x;
    if (s > 0.5){
      x = sin(PI*s)*0.5+0.5;
    }else{
      x = sin(PI*s);
    }
    E.vertex(x*w+x0[i],-t*h);
  }
  for (int i = n-1; i > 0; i--){
    float t = (float)i/(float)n;
    E.vertex(x0[i],-t*h);
  }
  
  E.endShape();
}


void drawPetal(float cd){
  
  E.hatchSpacing(2);
  E.hatchMode(E.SATIN);
  
  
  
  
  palette(0);
  dostroke();
  
  E.hatchAngleDeg(70);
  halfPetal(60,200,cd,200);
  E.hatchAngleDeg(110);
  halfPetal(-60,200,cd,200);
  
  E.noStroke();
  
  palette(1);
  
  E.hatchSpacing(5);
  E.hatchAngleDeg(70);
  halfPetal(55,200,cd,200);
  E.hatchAngleDeg(110);
  halfPetal(-55,200,cd,200);
  
  E.hatchSpacing(3);
  E.hatchAngleDeg(70);
  halfPetal(50,190,cd,200);
  E.hatchAngleDeg(110);
  halfPetal(-50,190,cd,200);
  
  palette(2);
  
  E.hatchSpacing(5);
  E.hatchAngleDeg(70);
  halfPetal(40,190,cd,200);
  E.hatchAngleDeg(110);
  halfPetal(-40,190,cd,200);
  
  E.hatchSpacing(3);
  E.hatchAngleDeg(70);
  halfPetal(30,180,cd,200);
  E.hatchAngleDeg(110);
  halfPetal(-30,180,cd,200);

  palette(3);
  
  E.hatchSpacing(5);
  E.hatchAngleDeg(70);
  halfPetal(20,180,cd,200);
  E.hatchAngleDeg(110);
  halfPetal(-20,180,cd,200);
  
  E.hatchSpacing(3);
  E.hatchAngleDeg(70);
  halfPetal(10,170,cd,200);
  E.hatchAngleDeg(110);
  halfPetal(-10,170,cd,200);
}


void halfPetal2(float x0, float y0, float x1, float y1, float d0, float d1){
  int n = 100;
  E.beginShape();

  for (int i = 0; i < n; i++){
    float t = (float)i/(float)n;

    float xx = lerp(x0,x1,t);
    float yy = lerp(y0,y1,t);
    float dd = pow(sin(PI*t),0.5)*d0;
    E.vertex(xx-dd*0.2,yy-dd);
  }
  
  for (int i = 0; i < n; i++){
    float t = 1-(float)i/(float)n;

    float xx = lerp(x0,x1,t);
    float yy = lerp(y0,y1,t);
    float dd = pow(sin(PI*constrain(t,0.0000001,0.9999999)),0.5)*d1;
    //println(dd);
    E.vertex(xx,yy-dd);
  }
  
  E.endShape();
}

void drawPetal2(float x0, float y0, float x1, float y1, float d0, float d1){
  
  E.hatchMode(E.SATIN);
  E.hatchAngle(-atan2(y1-y0,x1-x0)+PI/4);
  
  //palette(3);
  //E.hatchSpacing(5);
  //E.noStroke();
  //halfPetal2(x0,y0,x1,y1,d0*0.8,d1*1.2);
  
  palette(0);
  E.hatchSpacing(2);
  dostroke();
  halfPetal2(x0,y0,x1,y1,d0,d1);
  
  E.noStroke();
  
  palette(1);
  E.hatchSpacing(5);
  halfPetal2(x0,y0,x1,y1,d0,d1*0.86);

  E.hatchSpacing(3);
  halfPetal2(x0,y0,x1,y1,d0,d1*0.7);
}

void drawPetal2A(){
  palette(0);
  dostroke();
  E.hatchSpacing(2);
  E.hatchAngleDeg(70);
  halfPetal(15,60,0,60);
  E.hatchAngleDeg(110);
  halfPetal(-15,60,0,60);
  
  palette(1);
  E.noStroke();
  E.hatchSpacing(4);
  E.hatchAngleDeg(70);
  halfPetal(15,55,0,60);
  E.hatchAngleDeg(110);
  halfPetal(-15,55,0,60);
  
  
  palette(2);
  E.noStroke();
  E.hatchSpacing(4);
  E.hatchAngleDeg(70);
  halfPetal(12,40,0,60);
  E.hatchAngleDeg(110);
  halfPetal(-12,40,0,60);
  
  E.noStroke();
  E.hatchSpacing(2);
  E.hatchAngleDeg(70);
  halfPetal(5,30,0,60);
  E.hatchAngleDeg(110);
  halfPetal(-5,30,0,60);
  
}


void halfPetal3(float w, float h, float ww){
  int n = 100;
  E.beginShape();
  for (int i = 0; i < n; i++){
    float t = (float)i/(float)n;
    float x;
    if (t < 0.25){
      x = pow(sin(PI*t*2),0.5);
    }else{
      x = pow(sin((t+0.5)*PI*2f/3f),0.7);
      
    }

    E.vertex(x*w,-t*h);
  }
  for (int i = 0; i < n; i++){
    float t = 1-(float)i/(float)n;
    float x = sin(PI*t);
    E.vertex(x*ww,-t*h);
  }
  E.endShape();
}

void drawPetal3(float w, float h){
  float d = w > 0 ? 1 : 0;
  E.hatchSpacing(2);
  E.hatchAngle(90-d*20);
  
  palette(4);
  dostroke();
  halfPetal3(w,h,0);
  
  E.noStroke();
  
  palette(3);
  E.hatchSpacing(5);
  halfPetal3(w-4*d,h-5,0);
  
  palette(2);
  E.hatchSpacing(3);
  halfPetal3(w-8*d,h-20,0);
  
  palette(1);
  E.hatchSpacing(5);
  halfPetal3(w-12*d,h-35,0);
  
  E.hatchSpacing(3);
  halfPetal3(w-16*d,h-55,0);
  
  palette(0);
  E.hatchSpacing(5);
  halfPetal3(w-20*d,h-65,0);
  
  E.hatchSpacing(3);
  halfPetal3(w-24*d,h-80,0);
}

void drawPetal3A(float w, float h){
  float d = w > 0 ? 1 : 0;
  E.hatchSpacing(2);
  E.hatchAngle(90-d*20);
  
  palette(4);
  dostroke();
  halfPetal3(w,h,10);
  
  E.noStroke();
  
  palette(3);
  E.hatchSpacing(5);
  halfPetal3(w-4*d,h-5,10);
  
  palette(2);
  E.hatchSpacing(3);
  halfPetal3(w-8*d,h-20,10);
  
  palette(1);
  E.hatchSpacing(5);
  halfPetal3(w-12*d,h-35,10);
  
  E.hatchSpacing(3);
  halfPetal3(w-16*d,h-55,8);
  
  palette(0);
  E.hatchSpacing(5);
  halfPetal3(w-20*d,h-65,8);
  
  E.hatchSpacing(3);
  halfPetal3(w-24*d,h-80,8);
}


void stem(){
  E.hatchAngle(0);
  palette(5);
  dostroke();
  E.hatchSpacing(2);
  
  float x0[] = new float[100];
  for (int i = 0; i < 100; i++){
    x0[i] = i*noise((float)i/100.0);
  }
  
  E.beginShape();
  for (int i = 0; i < x0.length; i++){
    E.vertex(x0[i]-10,i*3);
  }
  for (int i = x0.length-1; i >= 0; i--){
    E.vertex(x0[i]+10, i*3);
  }
  E.endShape();
  

}

void halfFlower(){
  
  E.pushMatrix();
  E.translate(20,-80);
  E.rotate(PI/3);
  drawPetal(-30);
  E.popMatrix();
  
  E.pushMatrix();
  E.translate(0,-40);
  E.rotate(PI/2);
  drawPetal(-30);
  E.popMatrix();
  
  E.pushMatrix();
  E.rotate(PI-PI/4);
  drawPetal(-30);
  E.popMatrix();
  
  drawPetal2(0,-100,80,-200,30,60);
  
  drawPetal2(0,-100,90,-150,30,60);
  drawPetal2(0,-50,100,-100,30,62);
  
  drawPetal2(0,0,110,-50,30,60);
  drawPetal2(0,0,90,0,30,50);
  
  E.pushMatrix();
  E.rotate(PI*0.2);
  drawPetal2(0,0,70,0,20,40);
  E.popMatrix();
  
  E.pushMatrix();
  E.rotate(PI*0.4);
  drawPetal2(0,0,50,0,10,30);
  E.popMatrix();
  
  E.pushMatrix();
  E.rotate(PI);
  drawPetal2A();

  
  E.popMatrix();
  
  E.pushMatrix();
    E.translate(-1,-120);
    drawPetal3(40,160);
    
    E.pushMatrix();
      E.translate(30,0);
      E.rotate(PI/32);
      drawPetal3A(-20,130);
    E.popMatrix();
  
  E.popMatrix();
  
  E.pushMatrix();
  E.translate(-1,-40);
  
  
   E.pushMatrix();
      E.translate(40,-30);
      E.rotate(PI/32);
      drawPetal3A(-40,130);
    E.popMatrix();
    
  drawPetal3(35,150);
    
  E.popMatrix();
  
  E.pushMatrix();
  E.translate(-1,0);
  
  
     E.pushMatrix();
       
      E.translate(20,-20);
      E.rotate(PI/8);
      drawPetal3A(-30,120);
    E.popMatrix();
    drawPetal3(40,100);
  E.popMatrix();
}

void setup(){
  size(600,600); 
  
  
  background(200,192,180);
  
  E = new PEmbroiderGraphics(this);
  E.setPath(sketchPath("PEmbroider_flower.vp3"));
  
  E.scale(1);
  E.translate(300,300);
  stem();
  E.rotate(-0.15);
  halfFlower();
  E.scale(-1,1);
  halfFlower();
  

  E.visualize(true,false,false);
 // E.endDraw();
}


void draw(){
  
  
}
