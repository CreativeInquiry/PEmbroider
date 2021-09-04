# PEmbroider Cheat Sheet

## Quick Reference Links

* Here is the detailed [**API for PEmbroider**](https://github.com/CreativeInquiry/PEmbroider/blob/master/API.md), in progress.
* This document assumes that you're familiar with the [**Processing drawing API**](https://processing.org/reference/). 
* All of our examples can be browsed [**here**](examples/README.md). 


## Overview of Startup, Drawing and Export
	
```java
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
  // NOTE: Leave optimize() and endDraw() commented out,
  // until you are ready to export the embroidery file! 
  // Don't forget to un-comment them when you want to export!
  //
  // E.optimize(); // VERY SLOW, but essential for file output! 
  E.visualize();   // Display the embroidery path on-screen.
  // E.endDraw();  // Actually writes out the embroidery file.
}
```
    
The `optimize()` function is ***slow***, as it takes time to calculate a more effective order for the machine to stitch the embroidery. It does not change the appearance of your onscreen embroidery preview, but it will slow you down, so generally it does not need to be called when you're still designing your sketch. 

Calling `visualize()` is the same as calling `visualize(false, true, false)`. The first variable, false by default, shows you a full color preview. The second variable, true by default, shows stitch ends. The third variable, false by default, shows you a preview of connecting stitches, which you will have to manually remove after your design is embroidered. If you wanna see all the diagnostic stuff, call `visualize(true, true, true);`


## Shapes

PEmbroider tries to copy the Processing drawing API. It includes commands for elements including lines, circles, ellipses, rects, arcs, triangles, quads, and arbitrary polygons with both straight and curved sides.

*See Examples: [shapes](examples/PEmbroider_shapes), [shape_hatching_1](examples/PEmbroider_shape_hatching_1), [shape_hatching_2](examples/PEmbroider_shape_hatching_2), [shape_hatching_3](examples/PEmbroider_shape_hatching_3), [shape_hatching_4](examples/PEmbroider_shape_hatching_4), [shape_culling](examples/PEmbroider_shape_culling)*

```java
// Examples of some simple shapes. 
E.circle(x, y, r);
E.rect(x, y, w, h);
E.triangle(x1, y1, x2, y2, x3, y3);
E.line(x1, y1, x2, y2);
```

## Composite Shapes

*See Examples: [stroke_outlines](examples/PEmbroider_stroke_outlines), [stroke_outlines_2](examples/PEmbroider_stroke_outlines_2)*

```java
// Merge two circles into a peanut shape
E.beginComposite();
  E.composite.circle(320, 250, 200);
  E.composite.circle(420, 250, 200);
  // add more, etc. ...
E.endComposite(); 
```
  
## Fills & Hatching

*See Examples: [shape_hatching_1](examples/PEmbroider_shape_hatching_1), [shape_hatching_2](examples/PEmbroider_shape_hatching_2), [shape_hatching_3](examples/PEmbroider_shape_hatching_3), [shape_hatching_4](examples/PEmbroider_shape_hatching_4), [satin_hatching_1](examples/PEmbroider_satin_hatching_1)*

```java
E.noFill();
```

Hatch Modes: 

```java
// Here are some different PEmbroider hatching modes. 
// CONCENTRIC, PARALLEL, and SATIN are really solid. 
// The others may be buggy. 

E.hatchMode(E.CONCENTRIC);
E.hatchMode(E.PARALLEL);
E.hatchMode(E.SATIN);
E.hatchMode(E.SPIRAL); 
E.hatchMode(E.PERLIN);
E.hatchMode(E.CROSS);
```

Hatch Settings:

```java
// These are your main settings for controlling the
// density, orientation, and color of a hatched fill. 

E.hatchSpacing(spacing); // sets the density of adjacent runs (in machine units)
E.hatchAngleDeg(angle);  // sets the orientation for SATIN & PARALLEL (in degrees)
E.fill(R, G, B);         // sets your thread color
```

  
## Strokes

*See Examples: [lines_1](examples/PEmbroider_lines_1), [lines_2](examples/PEmbroider_lines_2), [stroke_outlines](examples/PEmbroider_stroke_outlines), [stroke_outlines_2](examples/PEmbroider_stroke_outlines_2),[interactive_demo_2 (drawing)](examples/PEmbroider_interactive_demo_2)*

"Strokes" refers to the outlines that enclose basic graphics primitives. [Just like in Processing](https://processing.org/reference/stroke_.html), you can set the color and thickness of the stroke. With PEmbroider, you can also set the orientation of the stroke's stitches; the extent to which the stroke lies outside or inside the shape; and whether or not the stroke is embroidered before or after the shape's interior fill. 

### Main Stroke Settings:

```java
E.stroke(R, G, B);         // sets the stroke color, just like Processing.
E.strokeWeight(width);     // sets the thickness of the stroke (in machine units)
E.strokeSpacing(spacing);  // sets the density of the hatching within the stroke
```

### Stroke Modes:

```java
E.strokeMode(E.PERPENDICULAR );  // Stitches are perpendicular to the stroke
E.strokeMode(E.TANGENT);         // Stitches go in the same direction as stroke
E.noStroke();                    // This turns off the stroke entirely
```

### Stroke Locations:

```java
E.strokeLocation(E.CENTER);   // stroke is exactly centered on edge of shape
E.strokeLocation(E.INSIDE);   // stroke is completely inset within the shape
E.strokeLocation(E.OUTSIDE);  // stroke is a pure outline, outside the shape
E.strokeLocation(value);      // float between -1.0 (inside) and 1.0 (outside) 
```

### Stroke Order:

*See Examples: [text_1](examples/PEmbroider_text_1)*

``` java
E.SetRenderOrder(E.STROKE_OVER_FILL);  // stroke is embroidered after fill
E.SetRenderOrder(E.FILL_OVER_STROKE);  // stroke is embroidered before fill
```



## Text

* *See Examples: [Hello_PEmbroider](examples/), [text_1](examples/PEmbroider_text_1), [text_2](examples/PEmbroider_text_2), [text_3](examples/PEmbroider_text_3)*. 
* Also see [Processing's `text()`](https://processing.org/reference/text_.html) documentation.

```java
E.textSize(size);
E.textAlign(CENTER); // LEFT, RIGHT, etc., just like Processing
E.textFont(PEmbroiderFont.DUPLEX);
E.text(string, x, y);
```

## Images

PEmbroider can work with both bitmap and vector images. 

*See Examples: [bitmap_image_1](examples/PEmbroider_bitmap_image_1), [bitmap_image_2](examples/PEmbroider_bitmap_image_2), [bitmap_animation](examples/PEmbroider_bitmap_animation), [png_image_multicolor](examples/PEmbroider_png_image_multicolor), [svg_image](examples/PEmbroider_svg_image)*

### Bitmap Images

Raster (bitmap) images must be black-and-white only, where white pixels indicate the graphics to be embroidered. Processing will expect your image assets to be located in the "data" folder of your sketch; for more information on what that means, see [here](https://processing.org/reference/environment/#Sketchbook).

```java
PImage myImage = loadImage("filename.png");
E.fill(0,0,0);
E.image(myImage, x, y);
```

### Vector Images: 

```java
PShape mySvgImage = loadShape("filename.svg");
E.fill(0,0,0);
E.shape(mySvgImage,50,50,350,350);
```

Experimentally, we have also begun using the TSP code to render photographs. See [this](examples/work_in_progress/PEmbroider_TSP_grayscale) and [this](examples/work_in_progress/PEmbroider_TSP_CMYK)

## Other Really Important Things!

### Setting Hatch Spacing (Fill Density)

As mentioned above, the `hatchSpacing()` function has a major impact on your design. It sets the distance between adjacent runs in hatch modes like SATIN, PARALLEL, and CONCENTRIC. The units are machine units (for our machine, this is 0.1mm). 

```java
E.hatchSpacing(3);
```

### Setting Stitch Properties

The `setStitch()` function is *super-important*! It allows you to set the following: 
* the minimum stitch length (in machine units; for our machine, this is 0.1mm)
* the desired stitch length (in machine units)
* the amount of noise (0...1) affecting stitch length. (This can be helpful for dithering the stitches in fills, so that they don't all line up.)

*See Examples: [shape_hatching_3](examples/PEmbroider_shape_hatching_3)*

```java
E.setStitch(minLength, desiredLength, noise);
```
   
For `SPIRAL` hatching, `setStitch` behaves differently;

```java
E.setStitch(desiredLength, minLength, noise);
```

### Protect your Lines Endpoints with `repeatEnd()`

The `repeatEnd()` function is very helpful for detailed linework; it essentially ties a knot so your single-line designs don't fray. 

*See Examples: [ruler](examples/PEmbroider_ruler)*

```java
beginRepeatEnd(3);  
// draw some lines... 
endRepeatEnd();
``` 
