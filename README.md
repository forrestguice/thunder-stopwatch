<h1>Thunder-Stopwatch README</h1>

<h2>Contents:</h2>

      I. Description
     II. Project Structure
     II. Project Dependencies
    III. Legal Stuff

<h2>I. Description</h2>

Thunder StopWatch is a toy app for Android that can be used to 
estimate the distance to a flash of lightning.

The app:
  * provides a simple timer with large controls
  * automatically estimates the distance sound may have traveled
  * keeps a history of recent times and distances
  * displays a plot of recent distances

Cool Ideas for future development:
  * use the camera to start the timer; analyze frames using a histogram to identify
    the lightning flash.
  * use the microphone to stop the timer; use waveform analysis to identify the thunder.
  * record the direction the device was facing when the timer was started or stopped;
    estimate probable strike zone and display (pass coordinate range to another app for
    display on a map).
  

<h2>II. Project Structure</h2>

The project is split into two components: 
  * thunder-stopwatch-library - an Android Library project
  * thunder-stopwatch-app - an Android Application project

The library project provides all functionality, while the application project provides the distributable APK. It was originally organized like this in order to include advertising frameworks within some versions of the APK. Since then all advertising has been removed and the source code released under GPLv3. The original structure is retained as an example of how to use Android Library projects, but at this point the application project is only a thin wrapper over the library project and does not provide any additional functionality.


<h2>III. Project Dependencies</h2>

Thunder Stopwatch depends on:

  * AndroidPlot (http://androidplot.com/about/)
    com.androidplot:androidplot-core:0.6.1

See "Legal Stuff" for license information.


<h2>IV. Legal Stuff</h2>

  * Thunder Stopwatch (https://github.com/forrestguice/thunder-stopwatch) <br/>
    Copyright (C) 2010 Forrest Guice <br/>

        GNU GENERAL PUBLIC LICENSE
              
            Version 3, 29 June 2007
        
        Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>            
    
    See thunder-stopwatch\COPYING for more information.


  * AndroidPlot (http://androidplot.com/about/) <br />
    Apache License, Version 2.0 <br />
    See thunder-stopwatch-library\libs\LICENSE_AndroidPlot.txt</b> for more information.
