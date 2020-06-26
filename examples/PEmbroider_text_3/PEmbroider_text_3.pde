// Test program for the PEmbroider library for Processing: 
// Test of Hershey Font text output

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (1500, 1150);

  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_text_3.vp3");
  E.setPath(outputFilePath); 

  E.beginDraw(); 
  E.clear();

  //-----------------------
  E.stroke(0); 
  E.strokeWeight(1);
  E.strokeMode(PEmbroiderGraphics.PARALLEL);
  E.noFill();

  String pangram = "Sphinx of black quartz, judge my vow.";
  float ty = 0; 

  E.textSize(2); 
  E.textAlign(CENTER, BASELINE);
  
  E.textSize(4.2); 
  E.textFont(PEmbroiderFont.PLAIN);
  E.text(pangram, width/2.0, ty+=60);
  
  E.textSize(2.37); 
  E.textFont(PEmbroiderFont.SIMPLEX);
  E.text(pangram, width/2.0, ty+=75);
  
  E.textSize(2.3); 
  E.textFont(PEmbroiderFont.DUPLEX);
  E.text(pangram, width/2.0, ty+=85);

  E.textSize(2.15); 
  E.textFont(PEmbroiderFont.COMPLEX);
  E.text(pangram, width/2.0, ty+=85);
 
  E.textSize(3); 
  E.textFont(PEmbroiderFont.COMPLEX_SMALL);
  E.text(pangram, width/2.0, ty+=90);

  E.textSize(2.15); 
  E.textFont(PEmbroiderFont.TRIPLEX);
  E.text(pangram, width/2.0, ty+=80);

  E.textSize(2.15); 
  E.textFont(PEmbroiderFont.ITALIC_COMPLEX);
  E.text(pangram, width/2.0, ty+=80);

  E.textSize(2.05); 
  E.textFont(PEmbroiderFont.ITALIC_TRIPLEX);
  E.text(pangram, width/2.0, ty+=80);

  E.textSize(2.4); 
  E.textFont(PEmbroiderFont.GOTHIC_ENGLISH_TRIPLEX);
  E.text(pangram, width/2.0, ty+=80);

  E.textSize(2.5); 
  E.textFont(PEmbroiderFont.GOTHIC_ITALIAN_TRIPLEX);
  E.text(pangram, width/2.0, ty+=80);

  E.textSize(2.5); 
  E.textFont(PEmbroiderFont.GOTHIC_GERMAN_TRIPLEX);
  E.text(pangram, width/2.0, ty+=90);

  E.textSize(2.75); 
  E.textFont(PEmbroiderFont.SCRIPT_SIMPLEX);
  E.text(pangram, width/2.0, ty+=90);

  E.textFont(PEmbroiderFont.SCRIPT_COMPLEX);
  E.text(pangram, width/2.0, ty+=95);

  //-----------------------
  //E.optimize(); // VERY SLOW -- can take MINUTES -- but ESSENTIAL!!!
  E.visualize();
  //E.endDraw(); // write out the file
  //save("PEmbroider_text_3.png");
}
