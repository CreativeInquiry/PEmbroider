# PEmbroider Cheat Sheet

## Quick Reference Links

* Here is the detailed [**API for PEmbroider**](https://github.com/CreativeInquiry/PEmbroider/blob/master/API.md), in progress.
* This document assumes that you're familiar with the [**Processing drawing API**](https://processing.org/reference/). 


## Overview of Startup, Drawing and Export
	
```
// Example PEmbroider program
import processing.embroider.*;
PEmbroiderGraphics E;

void setup() {

  // Starting up:
  noLoop();
  size(800, 600);
  E = new PEmbroiderGraphics(this, width, height);
  String outputFilePath = sketchPath("filename.dst");
  E.setPath(outputFilePath); 
  E.beginDraw();
  E.clear();

  //-------
  // Content goes here:
  E.fill(0, 0, 0); 
  E.circle(200, 200, 200); 

  //----------  
  // Visualization and export:
  // NOTE: Leave optimize() and endDraw() commented out
  // until you are ready to export!
  //
  // E.optimize(); // VERY SLOW, but essential for file output! 
  E.visualize(); 
  // E.endDraw();  // Write out the embroidery file
}
```
    
The `optimize()` function is ***slow***, as it takes time to calculate a more effective order for the machine to stitch the embroidery. It does not change the appearance of your onscreen embroidery preview, but it will slow you down, so generally it does not need to be called when you're still designing your sketch. 

Calling `visualize()` is the same as calling `visualize(false, true, false)`. The first variable, false by default, shows you a full color preview. The second variable, true by default, shows stitch ends. The third variable, false by default, shows you a preview of connecting stitches, which you will have to manually remove after your design is embroidered.


## Shapes

PEmbroider tries to mirror the Processing drawing API. It includes commands for elements including lines, circles, ellipses, rects, arcs, triangles, quads, and arbitrary polygons with both straight and curved sides.

*See Examples: [shapes](examples/PEmbroider_shapes), [shape_hatching_1](examples/PEmbroider_shape_hatching_1), [shape_hatching_2](examples/PEmbroider_shape_hatching_2), [shape_hatching_3](examples/PEmbroider_shape_hatching_3), [shape_hatching_4](examples/PEmbroider_shape_hatching_4), [shape_culling](examples/PEmbroider_shape_culling)*

    E.circle(x, y, radius)
    E.rect(x, y, width, height)
    E.triangle(x1, y1, x2, y2, x3, y3);
    E.line(x1, y1, x2, y2);
    
## Composite Shapes

*See Examples: stroke_outlines, stroke_outlines_2*

      E.beginComposite();
    	E.composite.circle(320, 250, 200);
    	E.composite.circle(420, 250, 200);
    	// add more, etc. ...
      E.endComposite(); 

  
## Fills & Hatching

*See Examples: shape_hatching_1-4*

    E.noFill();
    
Hatch Modes: 

    E.hatchMode(E.CONCENTRIC);
    E.hatchMode(E.PARALLEL);
    E.hatchMode(E.SATIN);
    E.hatchMode(E.SPIRAL);
    E.hatchMode(E.PERLIN);
    E.hatchMode(E.CROSS);
      
 Hatch Settings:

    E.hatchSpacing(spacing); // sets the density of adjacent runs
    E.hatchAngleDeg(angle);  // sets the orientation for SATIN & PARALLEL
    E.fill(R, G, B);         // sets your thread color

  
## Strokes

*See Examples: lines_1-2, stroke_outlines*

Stroke Modes:

    E.noStroke();
    E.strokeMode( E.PERPENDICULAR );
    E.strokeMode( E.TANGENT);
     
Stroke Locations

    E.strokeLocation(E.CENTER);
    E.strokeLocation(E.INSIDE);
    E.strokeLocation(E.OUTSIDE);
    
Stroke Settings

    E.strokeWeight(width);
    E.strokeSpacing(spacing);
    E.stroke(R, G, B);
    

## Text

*See Examples: Hello_PEmbroider, text_1, text_2, text_3* 

    E.textSize(size);
    E.textAlign
    E.textFont( PEmbroiderFont.DUPLEX )
    E.text( string, x, y);

## Images

*see bitmap_animation, bitmap_image_1,  bitmap_image_2, png_image_mulitcolor, *

    PImage myImage = loadImage(filename);
    E.image(myImage, x, y);

## Other Really Useful Things!

### Set hatch spacing with `hatchSpacing()`

As mentioned above, the `hatchSpacing()` function has a major impact on your design. It sets the distance between adjacent runs in hatch modes like SATIN, PARALLEL, and CONCENTRIC. The units are machine units (for our machine, this is 0.1mm). 

    E.hatchSpacing(3);


### Set stitch properties with `setStitch()`

The `setStitch()` function allows you to set the following: 
* the minimum stitch length (in machine units; for our machine, this is 0.1mm)
* the desired stitch length (in machine units)
* the amount of noise (0...1) affecting stitch length. (This can be helpful for dithering the stitches in fills, so that they don't all line up.)

*See Examples: hatching_3*

    E.setStitch(minLength, desiredLength, noise);
   
For SPIRAL fill, setStitch behaves differently;

    E.setStitch(desiredLength, minLength, noise);
    
### Protect your lines with `repeatEnd()`

The `repeatEnd()` function is very helpful for detailed linework; it essentially ties a knot so your single-line designs don't fray. 

*See Examples: ruler*

    beginRepeatEnd(3);  
    // draw some lines... 
    endRepeatEnd();
    
### Render Order

*See Examples: text_1*

    E.SetRenderOrder(STROKE_OVER_FILL);
    E.SetRenderOrder(FILL_OVER_STROKE);

      


