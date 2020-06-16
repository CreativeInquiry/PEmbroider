// Test program for the PEmbroider library for Processing: 
// Test of Hershey Font text output

import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (1200, 800);

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
  float ty = 50; 

  E.textSize(1.45); 
  E.textAlign(CENTER, BASELINE);

  E.textFont(PEmbroiderFont.PLAIN);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.SIMPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.DUPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.COMPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.TRIPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.ITALIC_COMPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.ITALIC_TRIPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.GOTHIC_ENGLISH_TRIPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.GOTHIC_ITALIAN_TRIPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.GOTHIC_GERMAN_TRIPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.SCRIPT_SIMPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.SCRIPT_COMPLEX);
  E.text(pangram, width/2.0, ty+=50);

  E.textFont(PEmbroiderFont.COMPLEX_SMALL);
  E.text(pangram, width/2.0, ty+=50);


  //-----------------------
  // E.optimize(); // VERY SLOW -- can take MINUTES -- but ESSENTIAL!!!
  E.visualize();
  // E.endDraw(); // write out the file
}
