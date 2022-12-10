Alis Recording Tool is a frontend for simultaneously capturing and recording 
audio using one or more soundcards as audio sources, as well as storing metadata 
for each recording. It is designed to be used during conferences, seminars, 
forums, etc.

Alis Recording Tool is open source software and licensed under GPL v2. A copy of 
the license is included in the file called COPYING and in the About Dialog of 
the tool.

REQUIREMENTS:
Alis Recording Tool requires Sun Java 1.5.x or newer to run.
JRE 1.5.0_09 (5.0 series) is highly recommended.
http://java.sun.com

RUNNING:

For Windows Users:
------------------

1. Just install using the exe installer 
   (double click on the alisrectool-x.y.z-setup.exe)

2. Launch the application using the new Desktop icon or using the Start Menu 
   shortcuts


For Linux and Mac OS X Users:
-----------------------------

1. Extract the contents of this compressed package.

2. Change to the 'alisrectool' directory where the files were extracted.

3. Start Alis Recording Tool by running the script named 'alisrectool.sh' from a 
   command terminal; ex. "./alisrectool.sh"


NOTE:
If you have the Java JRE installed somewhere unusual (or not in your PATH), 
use the JAVA_PROGRAM_DIR option in the script.

NOTE for Mac OS X users:
Recording in mp3 format doesn't currently work for Mac OS X. You should instead 
tick the checkbox 'Record in WAVE format' located in the 'Config Dialog' -> 
'Java Sound Properties' tab in order to record .wav files.


REPORTING BUGS:
When reporting bugs for crashes or unusual behaviour, don't forget to include in 
your e-mail or bug report the Java StackTrace information that can probably be 
found in the terminal where you launched the application.
Windows users can use alisrectool.bat to launch the tool from a command prompt 
window and be able to copy paste the StackTrace


