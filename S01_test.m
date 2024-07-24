close all;
fclose all;
clear all;
clc;

javaaddpath('build/libs/HookieReader-1.0.jar');

dataPath = 'data/';
participantFolder = '59';
exportFolder = 'output/';

if ~exist(exportFolder,'dir')
	mkdir(exportFolder);
end

hReader = javaObject('timo.jyu.HookieReader',dataPath,participantFolder,exportFolder);


tZone = javaMethod('getTimeZone','java.util.TimeZone','Europe/Helsinki');%'UTC');  %Use timeZone to get calendar in Melbourne time zone
jCalendar = javaMethod('getInstance','java.util.Calendar',tZone); %Get Calendar instance
sdf = javaObject('java.text.SimpleDateFormat','yyyy-MM-dd HH:mm:ss'); %Create simple date format
javaMethod('setTimeZone',sdf,tZone);
jDate = javaObject('java.util.Date'); 

if exist('OCTAVE_VERSION', 'builtin')
	data = dlmread('output/59.csv');  %Pop your output file into here
	resultant = sqrt(sum(data(:,3:5).^2,2));
	timeStamps = data(:,2);
	jDate.setTime(data(1,2));
else
	data = readtable('output/59.csv');  %Pop your output file into here
	resultant = sqrt(sum([data.X data.Y data.Z].^2,2));
	timeStamps = data.tStamp;
	jDate.setTime(data.tStamp(1));
end

if exist('OCTAVE_VERSION', 'builtin')
    dateString = sdf.format(jDate);
    sampleRateString = strsplit(hReader.header.getSampleRate(),'Hz');
else
    dateString = sdf.format(jDate).toCharArray';
    sampleRateString = strsplit(hReader.header.getSampleRate().toCharArray','Hz');
end
disp(dateString);
sampleRate = str2num(sampleRateString{1});


[buffered, ignore] = buffer(resultant,sampleRate,0,'nodelay');
[tStamps, ignore] = buffer(timeStamps,sampleRate,0,'nodelay');
meanVals = mean(buffered,1);
meanShifted = bsxfun(@minus,buffered,meanVals);
mads = mean(abs(meanShifted));
tStamps = tStamps(1,:);

fh = figure,plot(tStamps-tStamps(1),mads)
waitfor(fh);
