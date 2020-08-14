// "Hello World" program for the PEmbroider library for Processing. 
// Generates a smily face and the words "hello world". 

// Import the library, and declare a PEmbroider renderer. 
import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {
  size (600, 750);
  float cx = width/2; 
  float cy = height/2 - 40;

  // Create the PEmbroider object
  E = new PEmbroiderGraphics(this, width, height);

  // Start rendering to the PEmbroiderer.
  E.beginDraw(); 
  E.clear();
  E.ellipseMode(CENTER);  // Ellipses are specfied by their center (not top-left)
  E.CIRCLE_DETAIL = 60;   // Circles are secretly 60-sided polygons; good enough
  E.toggleResample(true); // Turn resampling on (good for embroidery machines)

  // Draw the face outline
  E.noFill();
  E.stroke(0, 0, 0);
  E.strokeWeight(30); 
  E.strokeSpacing(2.5);
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.setStitch(10, 50, 0); 
  E.ellipse (cx, cy, 500, 500); 

  // Draw the eyes
  E.fill(0, 0, 0); 
  E.stroke(0, 0, 0); 
  E.strokeWeight(1); 
  E.setStitch(10, 25, 0);
  E.hatchSpacing(2); 
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.circle(cx - 90, cy - 50, 110); 
  E.circle(cx + 90, cy - 50, 110); 

  // Draw the mouth
  E.noFill(); 
  E.stroke(0, 0, 0); 
  E.strokeWeight(50); 
  //E.strokeSpacing(1.5);
  E.strokeMode(PEmbroiderGraphics.TANGENT);
  E.setStitch(10, 25, 0); 
  E.arc(cx, cy, 270, 270, PI*0.15, PI*0.85); 

  // Say hello
  E.stroke(0, 0, 0); 
  E.textSize(2.5); 
  E.textAlign(CENTER, BOTTOM);
  E.textFont(PEmbroiderFont.SCRIPT_SIMPLEX);
  E.text("Hello World!", cx, 675); 


  // May be helpful for .pes output
  /*
  PEmbroiderWriter.PES.TRUNCATED = false;
   PEmbroiderWriter.PES.VERSION = 1;
   PEmbroiderWriter.TITLE = "HELLWRLD"; // Note: 8 character limit!
   */

  //-----------------------
  // IMPORTANT: Un-comment both E.optimize() and E.endDraw() 
  // in order to actually export the embroidery file. 
  // Note that E.optimize() can take about a minute to run. Patience!

  E.optimize(); // VERY SLOW, but ESSENTIAL for good file output!
  E.visualize(true, false, true); // Display (preview) the embroidery onscreen.
  String outputFilePath = sketchPath("Hello_PEmbroider.pes");
  E.setPath(outputFilePath);
  // E.endDraw(); // Uncomment this to write out the embroidery file.

  boolean bAlsoSaveScreenshot = false; 
  if (bAlsoSaveScreenshot) { // Save a screenshot 
    save("Hello_Pembroider.png");
  }
}


void draw() {
  boolean bShowAnimatedProgress = false;
  if (bShowAnimatedProgress) {
    background(255);
    E.visualize(true, false, true, frameCount);
  }
}
