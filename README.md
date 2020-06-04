# PEmbroider

Emboridery library for [Processing](http://processing.org)


## Building the Library

First, open the project in eclipse, then

Follow this excerpt from [processing/processing-library-template](https://github.com/processing/processing-library-template):
 
> From the menu bar, choose Window → Show View → Ant. A tab with the title "Ant" will pop up on the right side of your Eclipse editor.
> 
> Drag the resources/build.xml file in there, and a new item "ProcessingLibs" will appear.
> 
> Press the "Play" button inside the "Ant" tab.
> 
> BUILD SUCCESSFUL. The Library template will start to compile, control messages will appear in the console window, warnings can be ignored. When finished it should say BUILD SUCCESSFUL. Congratulations, you are set and you can start writing your own Library by making changes to the source code in folder src.
> 
> BUILD FAILED. In case the compile process fails, check the output in the console which will give you a closer idea of what went wrong. Errors may have been caused by Incorrect path settings in the build.properties file.

Same steps, in pictures:

<img width="2120" alt="Screen Shot 2020-04-06 at 1 33 20 AM" src="https://user-images.githubusercontent.com/7929704/78526116-0415c200-77a7-11ea-8918-fa34c4baf946.png">


If there're any issues, download a fresh copy, and follow processing's official doc entirely:

https://github.com/processing/processing-library-template

## Using the Library

After building the library, it can now be accessed from processing.

- In Processing app, Sketch -> Import Library -> Pembroider

Check out example usage / tests in `examples/Hello/Hello.pde`.
