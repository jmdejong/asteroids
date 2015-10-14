package aoop.asteroids;

public class Utils {
	
	
	public static int floorMod(int a, int b){
		return (a%b+a)%b;
	}
	
	public static double floorMod(double a, double b){
		return ((a%b)+b)%b;
	}
	
	public static double imagMultR(double a, double b, double c, double d){
		return a*c-b*d;
	}
	
	
	public static double imagMultI(double a, double b, double c, double d){
		return a*d+b*c;
	}
}