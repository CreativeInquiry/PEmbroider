// "Hello World" program for the PEmbroider library for Processing

// Import the library, and declare a PEmbroider renderer. 
import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  noLoop(); 
  size (600, 750);
  float cx = width/2; 
  float cy = height/2 - 40;

  // Create the PEmbroider object
  E = new PEmbroiderGraphics(this, width, height);
  E.setPath (sketchPath("Hello_etebigger_3widfacespace3_text3.vp3")); 

  // Start rendering to the PEmbroiderer
  E.beginDraw(); 
  E.clear();
  E.ellipseMode(CENTER);
  E.CIRCLE_DETAIL = 60;


  E.toggleResample(true);


  // Draw the face outline
  E.noFill();
  E.stroke(0, 0, 0);
  E.strokeWeight(25); 
  E.strokeSpacing(3);
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.setStitch(10, 50, 0); 
  E.ellipse (cx, cy, 500, 500); 
  

  // Draw the eyes
  E.fill(0, 0, 0); 
  E.stroke(0, 0, 0); 
  E.strokeWeight(1); 
  E.setStitch(10, 20, 0);
  E.hatchSpacing(3); 
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.circle(cx - 90, cy - 50, 100); 
  E.circle(cx + 90, cy - 50, 100); 

  // Draw the mouth
  E.noFill(); 
  E.stroke(0, 0, 0); 
  E.strokeWeight(30); 
  //E.strokeSpacing(1.5);
  E.strokeMode(PEmbroiderGraphics.TANGENT);
  E.setStitch(10, 30, 0); 
  E.arc(cx, cy, 270, 270, PI*0.15, PI*0.85); 

  // Say hello
  E.stroke(0, 0, 0); 
  E.textSize(2.5); 
  E.textAlign(CENTER, BOTTOM);
  E.textFont(PEmbroiderFont.DUPLEX);
  E.text("Hello World!", cx, 675); 

  //-----------------------
  // IMPORTANT: Un-comment E.optimize() and E.endDraw() 
  // in order to actually export the embroidery file. 
  // Note that E.optimize() can take a few seconds to run. Patience!
  
   //E.optimize();  // VERY SLOW, but ESSENTIAL for good file output!
   E.visualize(); // Display (preview) the embroidery onscreen.
   E.endDraw();   // Write out the embroidery file.
   //save("Hello_Pembroider.png");
}
