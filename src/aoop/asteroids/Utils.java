package aoop.asteroids;

public class Utils {
	
	
	public static int floorMod(int a, int b){
		return (a%b+a)%b;
	}
	
	public static double floorMod(double a, double b){
		return ((a%b)+b)%b;
	}
}