// "Hello World" program for the PEmbroider library for Processing.
// Repository: https://github.com/CreativeInquiry/PEmbroider
// Note that you will need to un-comment some code below
// in order to actually export the embroidery files. 

// Import the library, and declare a PEmbroider renderer. 
import processing.embroider.*;
PEmbroiderGraphics E;

//===============================================
void setup() {
  noLoop(); 
  size (1200, 750);
  float cx = width/2; 
  float cy = height/2 - 50;

  // Create the PEmbroider object
  E = new PEmbroiderGraphics(this, width, height);
  
  // Set the path for saving the embroidery file. 
  // Valid filetypes are: .dst, .exp, .jef, .pec, 
  // .pes, .vp3, .xxx, .pdf, .svg, .tsv, & .gcode.
  // Check your machine's manual to be certain! 
  E.setPath (sketchPath("Hello_PEmbroider.vp3")); 

  // Start rendering stitches to the PEmbroiderer.
  E.beginDraw(); 
  E.clear();
  E.ellipseMode(CENTER);
  E.CIRCLE_DETAIL = 60;

  // Draw the outline of a happy face
  E.noFill();
  E.stroke(0, 0, 0);
  E.strokeWeight(30); 
  E.strokeSpacing(3.0);
  E.strokeMode(PEmbroiderGraphics.PERPENDICULAR);
  E.setStitch(30, 40, 0); 
  E.ellipse (cx, cy, 500, 500); 

  // Draw the eyes
  E.fill(0, 0, 0); 
  E.stroke(0, 0, 0); 
  E.strokeWeight(1); 
  E.setStitch(10, 20, 0);
  E.hatchSpacing(3.0); 
  E.hatchMode(PEmbroiderGraphics.CONCENTRIC);
  E.circle(cx - 90, cy - 50, 100); 
  E.circle(cx + 90, cy - 50, 100); 

  // Draw the mouth
  E.noFill(); 
  E.stroke(0, 0, 0); 
  E.strokeWeight(30); 
  E.strokeSpacing(3.0);
  E.strokeMode(PEmbroiderGraphics.TANGENT);
  E.setStitch(10, 30, 0); 
  E.arc(cx, cy, 270, 270, PI*0.15, PI*0.85); 

  // Say "hello" with a Hershey font.
  E.stroke(0, 0, 0); 
  E.textSize(2.6); 
  E.textAlign(CENTER, BOTTOM);
  E.textFont(PEmbroiderFont.DUPLEX);
  E.text("Hello World!", cx, 675); 

  //-----------------------
  // IMPORTANT: in order to actually export the embroidery file. 
  // UN-COMMENT the E.optimize() and E.endDraw() lines below.
  // Note that E.optimize() can take some time to run. Be patient!
  
  // E.optimize();  // VERY SLOW, but ESSENTIAL for good file output!
  E.visualize();    // Display (preview) the embroidery onscreen.
  // E.endDraw();   // Write out the embroidery file.
  
}
