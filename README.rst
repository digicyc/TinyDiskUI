==========
TinyDiskUI
==========

TinyDiskUI extends all the features of TinyDisk and adds a GUI to it.

Reason
======
The purpose of this was to have it be an addon to certain applications
for quick file sharing of documents. Was also more for fun and to
take an existing open source application and extend on it for better
learning.

=============
TinyDisk Info
=============

TinyDisk is a program from saving and retrieving files from TinyURL
(http://tinyurl.com/) and TinyURL-like services such as Nanourl
(http://www.msblabs.org/nanourl)

Once a file has been written into a database it cannot be changed.
TinyDisk is a write-once-read-many file system, much like a CD-R.

USAGE:
To write a file into a database:
	java -jar TinyDiskUI.jar
This should load up the GUI with an easy to understand interface.


CONFIG:
TinyDisk is controlled by the config file TinyDisk.config. This file 
defines what servers TinyDisk uses.

****  BY DEFAULT TINYDISK IS CONFIGURED TO USE YOUR LOCALHOST ****
****  YOU MUST CHANGE THE CONFIG FILE TO LOAD AND STORE INTO  ****
****  TINYURL (http://tinyurl.com)                            ****

In addition, TinyDisk will only update files up to 2 megs by default.
You can change this to be larger. Please don't upload your 20 gigs of
porn into someone else's service!

LICENSE:
TinyDisk is BSD Licensed
