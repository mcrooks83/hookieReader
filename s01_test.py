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

# Define the paths as strings (ensure these variables are defined appropriately in your script)
dataPath = 'data/'
participantFolder = '59'
exportFolder = 'output/'

# create an output directory
if not os.path.isdir(exportFolder):
    os.makedirs(exportFolder)

# create and instance of the class that converts the data and writes to the output dir
hReader = HookieReader(dataPath, participantFolder, exportFolder)

# Use timeZone to get calendar in Tallinn time zone
tZone = TimeZone.getTimeZone(JString('Europe/Tallinn')) 
jCalendar = Calendar.getInstance(tZone)
sdf = SimpleDateFormat(JString('yyyy-MM-dd HH:mm:ss'))
sdf.setTimeZone(tZone)
jDate = Date()

# read the converted ouput back in output/participant folder
data = pd.read_csv(f'output/{participantFolder}.csv')
# Calculate the resultant vector
resultant = np.sqrt((data[['X', 'Y', 'Z']] ** 2).sum(axis=1))
timeStamps = data['tStamp']

print(resultant[0:10])

print("shutting down JVM")
jpype.shutdownJVM()
print("complete")