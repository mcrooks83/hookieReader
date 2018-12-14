package timo.jyu;

import java.util.ArrayList;

//Testing read file with java as well
import java.io.FileInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.Arrays;
import java.text.ParsePosition;

//Writing the data to a file

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;


//Accelerometry analysis
import timo.jyu.utils.*;

public class HookieReader{
	
	public HookieHeader header = null;
	
	private int epochLength;	
	private final double bitsToGs = 32d/Math.pow(2d,13d);
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("fi", "FI"));
	
	public static void main(String[] a){
		new HookieReader(a[0],a[1],a[2]);
	}
	
	public HookieReader(String path, String fName, String targetPath){
		
		//File name output
		FileWriter fstream = null;
		BufferedWriter out = null; 

		
		//Open outputfile
		try{
			//File name output
			fstream = new FileWriter(targetPath+fName+".csv");
			out = new BufferedWriter(fstream);
			out.write("tStamp,X,Y,Z,FileName,pointer\n");
		}catch(Exception e){
			System.out.println("Could not open outputFiles "+e.toString());
		}
		
		//List files in folder, and loop through .dat files to get the whole data
		File folder = new File(path+fName);
		File[] listOfFiles = folder.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name){
				return name.endsWith(".dat") | name.endsWith(".DAT");
			}
		});
		
		//If files were found list them
		if (listOfFiles != null){
			Arrays.sort(listOfFiles); //Sort the files in alphabetical order
			//First timestamp identified start going through files
			for (int i = 0; i < listOfFiles.length; i++) {
				//Pop the files through analysis to accumulate data
				File toRead = listOfFiles[i];
				//System.out.println("QuickHOOKIEAnalyser file loop "+toRead.toString());
				
				if (toRead.exists() & toRead.canRead()){
					int bytesToRead = (int) toRead.length();
					//int bytesToRead = 100000;
					//System.out.println("File to read "+fName+" "+bytesToRead+" bytes");
					
					byte[] bytes = new byte[bytesToRead];
					//byte[] bytes = new byte[(int) 10e6];
					try{
						FileInputStream fis = new FileInputStream(toRead);
						int readBytes = fis.read(bytes);
						if (readBytes == bytesToRead){
							fis.close();
						}
						
						//Read header from the first file
						if (header == null){
							header = new HookieHeader(Arrays.copyOf(bytes,512));
						}	
						
						
						//System.out.println("Read "+readBytes+" of "+bytesToRead);
						ArrayList<AccData> data = interpretBytes(bytes,toRead.getName());
						for (int d = 0; d<data.size(); ++d){
							out.write(String.format(Locale.ROOT,"%d,%f,%f,%f,%s,%d\n",data.get(d).tStamp,data.get(d).X,data.get(d).Y,data.get(d).Z,data.get(d).fileName,data.get(d).pointer));
						}
						
						System.out.println(String.format("File %s samples %d",toRead.getName(),data.size()));
						//System.out.println(String.format("Did %s %d s epochs accumulated %d",listOfFiles[i].toString(),epochLength,data.size()));
					}catch (Exception e){
						System.out.println(e.toString());
					}
					
				}else{
					System.out.println("Could not find or read "+fName);
				}
				
			}
			
			try{
				out.close();
			}catch (Exception e){e.printStackTrace();}
			
		}

	}

	/*Analyse data from memory. Save only analysis results*/
	private ArrayList<AccData> interpretBytes(byte[] trxdata, String fileName){
		int pointer = 512;	//Skip the header
		ArrayList<AccData> ret = new ArrayList<AccData>();
		while (pointer < (trxdata.length-511)){
			Packet packet = new Packet(trxdata,pointer,fileName);
			try{
				packet.interpret();
				ret.addAll(packet.data);	//Add data to the memory arrays
				pointer+=512;	//Advance memory pointer by a package
			}catch (Exception e){
				//Corrupted packet, advance until we find the next package, i.e.  double aa aa.
				++pointer;
				while(pointer < trxdata.length-511 && (trxdata[pointer] != (byte) 0XAA | trxdata[pointer+1] != (byte) 0XAA)){
					++pointer;
				}
			}

		}
		return ret;

	}
	
	
	
	
	public class Packet{
		public ArrayList<AccData> data;
		byte[] trxdata;
		int pointer;
		int initPointer;
		String fileName;
		public long packageStamp;
		
		public Packet(byte[] trxdata, int pointer,String fileName){
			data = new ArrayList<AccData>();	
			this.trxdata =trxdata;
			this.pointer =pointer;
			this.fileName = fileName;
			initPointer = pointer;
		}
		 
		public void interpret() throws Exception{
			int packagePointer = pointer;
			if (trxdata[pointer] == (byte) 0XAA & trxdata[pointer+1] == (byte) 0XAA){
				 pointer+=2;
				 String day = String.format("%02X",trxdata[pointer++]);
				 String month = String.format("%02X",trxdata[pointer++]);
				 String year = String.format("%03X",trxdata[pointer++]);
				 String hour = String.format("%02X",trxdata[pointer++]);
				 String min = String.format("%02X",trxdata[pointer++]);
				 String sec = String.format("%02X",trxdata[pointer++]);
				 String dateString = "2"+year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec;
				 Date currentStamp = sdf.parse(dateString, new ParsePosition(0));
				 packageStamp = currentStamp.getTime();
				int[] tempVals = new int[3];
				
				 while (pointer < initPointer+512-5){
					//Decode values, anything with 0xf000 is to be interpreted as a negative value
					for (int i=0; i<3; ++i){
						tempVals[i] = ((0xff & ((int) trxdata[pointer+2*i+1])) << 8) | (0xff & ((int) trxdata[pointer+2*i+0]));
						//System.out.print(String.format(" %04X %b",tempVals[i],(tempVals[i] & 0xf000) > 0));
						if ((tempVals[i] & 0xf000) > 0){
							//Negative value
							tempVals[i] = (0x0fff & tempVals[i]) - 0x1000;
						}else{
							tempVals[i] = 0x0fff & tempVals[i];
						}
					}
					//Convert the values to gs *32d/Math.pow(2d,13d);
					data.add(new AccData(packageStamp
					,((double) tempVals[0]) *bitsToGs
					,((double) tempVals[1]) *bitsToGs
					,((double) tempVals[2]) *bitsToGs
					,(long) packagePointer
					,fileName
					
					));
					pointer+=6;	//go to the next value
				 }
			}else{
				 //abort
				 throw new Exception(String.format("%s  %02X %02X","Not a package two first bytes not 0xAAAA",trxdata[pointer],trxdata[pointer+1]));
			}
		}
	}
}