### A java class to read TraxMeet Hookie .dat files and convert to csv

Released with the Creative Commons BY license (https://creativecommons.org/licenses/by/4.0).

## DATA

data for 4 participants that have similar data labels were used to test 
each participant was given a number 1-4
flight data had multiple directories so one was chosen that was in close proximity to all participants
each directory before_flight, flight and recovery contains all .dat files
data is locaated on level up to this directory (4_cosmo)


## COMPILE
ensure gradle is installed and the path variables set (see online documentation)
Use gradle to compile (from command line in this folder)
	gradle jar

note: updated sourceCompatability to compile with latest version of gradle (sourceCompatibility = 1.7) 

## USE
You will need to have hookie traxmeet .dat files in a folder (currently points to data/59)
Run Script S01... with Matlab
	This will read the data and spit it out into a textfile in a folder called output and visualise the data on screen.

## Python
create a data directory and place each participant (as a number i.e 1, 2, 3 etc) in it
each participant has directories for before flight, flight, and recovery (set this up as required)

dataPath = '../4_cosmo/'                            # is the path to where the raw data is (one directory up)
export_folder = '../4_cosmo/extracted_data/'        # the folder to which the extracted data is put
p_nums = ["1", "2", "3", "4"]                       # the participants (in this case as numbers and not names)
periods = ["before_flight", "flight", "recovery"]   # phases involved (there maybe more directories within these)

Run python s01_test.py 

this will extract the data to the export_folder directory as <number>_<period>.csv
this can be read in using pandas (example in the script)
note: all .DAT files are concatenated into one sinlge csv (to be understood why)


Written by Timo Rantalainen 2018 tjrantal at gmail dot com.
updated by Mike Crooks - Right Step Health mike@rightstep-health.com