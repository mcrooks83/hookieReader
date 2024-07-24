package timo.jyu.utils;

public class AccData{
	public long tStamp;
	public double X;
	public double Y;
	public double Z;
	public long pointer;	//Save the package start pointer
	public String fileName;
	
	//Overload for when pointer is not relevant
	public AccData(long a, double b, double c, double d){
		this(a,b,c,d,-1l, "");
	}
	
	public AccData(long a, double b, double c, double d, long e, String f){
		this.tStamp = a;
		this.X = b;
		this.Y = c;
		this.Z = d;
		this.pointer = e;
		this.fileName = f;
	}
}