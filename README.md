A java class to read TraxMeet Hookie .dat files from a folder.

Released with the Creative Commons BY license (https://creativecommons.org/licenses/by/4.0).

COMPILE
ensure gradle is installed and the path variables set (see online documentation)
Use gradle to compile (from command line in this folder)
	gradle jar

note: updated sourceCompatability to compile with latest version of gradle. 

USE
You will need to have hookie traxmeet .dat files in a folder (currently points to data/59)
Run Script S01... with Matlab
	This will read the data and spit it out into a textfile in a folder called output and visualise the data on screen.

Python
create a data folder in the root directory
add data into directorys of the form <number><..DAT files>
update variable participantFolder = '59' in the python script
Run python s01_test.py 

this will extract the data to the output directory as <number>.csv
this can be read in using pandas (example in the script)
note: all .DAT files are concatenated into one sinlge csv (to be understood why)


Written by Timo Rantalainen 2018 tjrantal at gmail dot com.