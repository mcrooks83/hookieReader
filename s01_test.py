# file to extact data from a .dat file
# outputs to exported_folder 
# test function reads in data, computes resultant and prints first 10 points

import jpype
import jpype.imports
from jpype.types import *
import os
import pandas as pd
import numpy as np

# Start the JVM with the specified classpath
jpype.startJVM(classpath=['build/libs/HookieReader-1.0.jar'])

# Import the Java classes
from timo.jyu import HookieReader
from java.util import TimeZone, Calendar, Date
from java.text import SimpleDateFormat

run=True
# test functions
def test_resultant(export_folder, p_num, p):
    # read the converted ouput back in output/participant folder
    data = pd.read_csv(f'{export_folder + p_num + "_" + p}.csv')

    # test computation that can actually be removed from here
    # Calculate the resultant vector
    resultant = np.sqrt((data[['X', 'Y', 'Z']] ** 2).sum(axis=1))
    timeStamps = data['tStamp']
    print(resultant[0:10])


def get_sample_rate(hReader):
    # sample rate is position 0 
    print(hReader.header.getSampleRate().split('Hz'))


# Define the paths as strings
dataPath = '../4_cosmo/'                            # is the path to where the raw data is (one directory up)
export_folder = '../4_cosmo/extracted_data/'        # the folder to which the extracted data is put
p_nums = ["1", "2", "3", "4"]                       # the participants (in this case as numbers and not names)
periods = ["before_flight", "flight", "recovery"]   # phases involved (there maybe more directories within these)
export_folder = 'test_data/'

# create an output directory if not already created
if not os.path.isdir(export_folder):
    os.makedirs(export_folder)

# test area - if run is False
if(not run):
    # read first p_num and period - this will extract all the data in the dir which could be a called function instead
    hReader = HookieReader(dataPath, p_nums[0], periods[0], export_folder)
    get_sample_rate(hReader)



# loop through the list of participants and each period extracting the data
if(run):
    for p_num in p_nums:
        for p in periods:
            # create and instance of the class that converts the data and writes to the output dir
            hReader = HookieReader(dataPath, p_num, p, export_folder)

            # header is just an object so can be read
            # getSampleRate retuns a string 
            # strsplit must be converted to python equivalent
            # str.split('Hz')
            # sampleRateString = strsplit(hReader.header.getSampleRate(),'Hz');
        


            # no use for this in here as we will compute and visualise what we need elsewhere

            # Use timeZone to get calendar in Tallinn time zone
            tZone = TimeZone.getTimeZone(JString('Europe/Tallinn')) 
            jCalendar = Calendar.getInstance(tZone)
            sdf = SimpleDateFormat(JString('yyyy-MM-dd HH:mm:ss'))
            sdf.setTimeZone(tZone)
            jDate = Date()

            test_resultant(export_folder, p_num, p)

# shut dowm JVM
print("shutting down JVM")
jpype.shutdownJVM()
print("complete")