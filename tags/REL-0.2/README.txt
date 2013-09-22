YAD2XX
------

YAD2XX is a Java native interface to the FTDI D2XX USB driver.

README.txt
Stephen Davies
2 July, 2012

Getting Started
---------------

You MUST install the FTDI D2XX driver from the FTDI website. See
http://www.ftdichip.com/Drivers/D2XX.htm

YAD2XX Installation
-------------------

Users of this project need to understand that a Java Native Interface consists of 
two download files. The first is the projects Java library, yad2xxJava-*.jar. The JAR
file contains compiled Java bytecode and is platform independent. You need to
download this and make it available on your projects CLASSPATH.

The second download file is platform dependent. You will need to choose between
the Windows x64 or OS X variants. This file contains the platform dependent 
implementations of the native code.

On Windows x64 (I've tested on Windows 7) you should copy FTDIInterface.dll to
c:\windows\system32.

On OS X copy libFTDIInterface.jnilib to /usr/lib/java.

