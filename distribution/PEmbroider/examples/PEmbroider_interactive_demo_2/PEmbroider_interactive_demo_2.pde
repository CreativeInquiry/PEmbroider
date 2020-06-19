// Test program for the PEmbroider library for Processing:
// Doodle recorder

import processing.embroider.*;
PEmbroiderGraphics E;
ArrayList<PVector> currentMark;
ArrayList<ArrayList<PVector>> marks;

void setup() { 
  size (800, 600);
  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("PEmbroider_interactive_demo_2.vp3");
  E.setPath(outputFilePath);

  currentMark = new ArrayList<PVector>();
  marks = new ArrayList<ArrayList<PVector>>();
}

//===================================================
void draw() {
  background(200); 
  E.beginDraw(); 
  E.clear();
  E.noFill(); 

  E.stroke(0, 0, 0); 
  E.strokeWeight(10); 
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.setStitch(10, 40, 0);

  // Add latest mouse point to curent mark
  if (mousePressed) {
    currentMark.add(new PVector(mouseX, mouseY));
  }

  // Draw previous marks
  for (int m=0; m<marks.size(); m++) {
    ArrayList<PVector> ithMark = marks.get(m); 
    E.beginShape(); 
    for (int i=0; i<ithMark.size(); i++) {
      PVector ithPoint = ithMark.get(i); 
      E.vertex (ithPoint.x, ithPoint.y);
    }
    E.endShape();
  }

  // Draw current mark
  E.beginShape(); 
  for (int i=0; i<currentMark.size(); i++) {
    PVector ithPoint = currentMark.get(i); 
    E.vertex (ithPoint.x, ithPoint.y);
  }
  E.endShape();


  E.visualize();
}

//===================================================
void mousePressed() {
  currentMark = new ArrayList<PVector>();
}

//===================================================
void mouseReleased() {
  marks.add(currentMark); 
  E.printStats();
}

//===================================================
void keyPressed() {
  if (key == ' ') {
    currentMark.clear(); 
    marks.clear();
    
  } else if (key == 's' || key == 'S') { // S to save
    E.optimize(); // slow, but very good and important
    E.printStats(); 
    E.endDraw(); // write out the file
  }
}
