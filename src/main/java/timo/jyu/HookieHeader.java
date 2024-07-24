package timo.jyu;
public class HookieHeader{
	private String headerString = null;
	private String[] headerFields = {"Version ","S/N ","Date: ","Time: ","Data rate: ","Data compression: ", "Activity threshold: ", "Inactivity threshold: ","Inactivity time: ","Acceleration coupling: ","Logfile max.size: "};
	private String[] headerValues = null;
	private int[] fieldLengths = {10,20,10,8,6,1,3,3,3,2,8};
	
	public HookieHeader(byte[] dataIn){
		headerString = new String(dataIn);
		//Go through the headerFields, and extract corresponding values
		headerValues = new String[headerFields.length];
		for (int i = 0;i<headerFields.length; ++i){
			int firstIndex = headerString.indexOf(headerFields[i])+headerFields[i].length();
			headerValues[i] = headerString.substring(firstIndex,firstIndex+fieldLengths[i]);
		}
	}
	
	public String getActivityThreshold(){
		return headerValues[getIndex("Activity threshold")];
	}
	public String getInactivityThreshold(){
		return headerValues[getIndex("Inactivity threshold")];
	}
	public String getInactivityTime(){
		return headerValues[getIndex("Inactivity time")];
	}
	
	public String getAccelerationCoupling(){
		return headerValues[getIndex("Acceleration coupling")];
	}
	public String getLogfileSize(){
		return headerValues[getIndex("Logfile")];
	}
	
	public String getSampleRate(){
		return headerValues[getIndex("Data rate")];
	}
	
	public String getDate(){
		return headerValues[getIndex("Date")];
	}
	
	public String getVersion(){
		return headerValues[getIndex("Version")];
	}
	
	public String getSN(){
		return headerValues[getIndex("S/N")];
	}
	
	public String getDataCompression(){
		return headerValues[getIndex("compression")];
	}
	
	private int getIndex(String a){
		for (int i = 0; i<headerFields.length; ++i){
			if (headerFields[i].indexOf(a) > -1){
				return i;
			}
		}
		return -1;
	}
}