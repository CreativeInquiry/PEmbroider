# PEmbroider

An embroidery library for [Processing](http://processing.org). Developed at the [Frank-Ratchye STUDIO for Creative Inquiry](http://studioforcreativeinquiry.org) by Golan Levin and Lingdong Huang, spring 2020, with generous support from the [Clinic for Open Source Arts](https://www.du.edu/ahss/opensourcearts/) (COSA) at the University of Denver. 


## Features

PEmbroider is an embroidery Library for [Processing](http://processing.org), suitable for generating computational embroidery designs. PEmbroider has the following features:

* Structured similarly to other alternative renderers for Processing.
* Able to generate embroidery files in .DST, .PEC, and .VP3 formats, suitable for consumer embroidery machines such as the Viking Jade. 
* Also able to generate .PDF, .SVG, .TSV, and .GCODE files for other output devices such as pen plotters, CNC routers, etc.
* Offers a wide range of fill methods for shapes. 
* Allows a variety of ways of composing shapes, including overlapping, cropping, and merging, using computational geometry algoritms.
* Generates optimized (shortest) paths using a modified Traveling Salesperson Problem solver. 

---

## Building the Library (From Source)

First, open the project in Eclipse; then:

Follow this excerpt from [processing/processing-library-template](https://github.com/processing/processing-library-template):
 
> * From the Eclipse menu bar, choose *Window* → *Show View* → *Ant*. A tab with the title "Ant" will pop up on the right side of your Eclipse editor.
> * Drag the `resources/build.xml` file in there, and a new item "ProcessingLibs" will appear.
> * Press the "Play" button inside the "Ant" tab.
> 
> If BUILD SUCCESSFUL: The Library template will start to compile, control messages will appear in the console window, warnings can be ignored. When finished it should say BUILD SUCCESSFUL. Congratulations, you're set and you can start modifying the library by making changes to the source code in folder `src`.
> 
> If BUILD FAILED:  In case the compile process fails, check the output in the console which will give you a closer idea of what went wrong. Errors may have been caused by Incorrect path settings in the `build.properties` file.

Here are the same steps, in screenshots:

![Compilation steps in Eclipse](images/eclipse_steps.png)

If there are any issues, download a fresh copy of this repositoy, and try following Processing's official documentation entirely: [https://github.com/processing/processing-library-template](https://github.com/processing/processing-library-template).


---

## Using the Library

After building the library, it can now be accessed from processing.

- In the Processing app, *Sketch* → *Import Library* → *Pembroider*

Check out example usage and tests in `examples/HelloPEmbroider/HelloPEmbroider.pde`.
