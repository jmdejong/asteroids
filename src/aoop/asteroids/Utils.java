package aoop.asteroids;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aoop.asteroids.model.gameobjects.GameObject;

/**
 * This Utils class contains a couple of useful functions that are used in multiple places of the program.
 * @author Wiebe-Marten Wijnja, Michiel de Jong
 *
 */
public final class Utils {
	
	/**
	 * Returns the remainder of dividing a by b. (rounded towards zero. This will never return a negative number)<br/>
	 * In Java 8, this functionality is built-in in the Math library. But, as we use Java 7, we have to make it ourselves.
	 * @return the result of the floorMod operation: (a % b)
	 * @see Utils#floorMod(double, double)
	 */
	public static int floorMod(int a, int b){
		return (a%b+b)%b;
	}
	
	/**
	 * Returns the remainder of dividing a by b. (rounded towards zero. This will never return a negative number)<br/>
	 * If `b` is infinite, simply returns `a`.
	 * @see Utils#floorMod(int, int)
	 */
	public static double floorMod(double a, double b){
		if (Double.isInfinite(b)){
			return a;
		} else {
			return (a%b+b)%b;
		}
	}
	
	/**
	 * Multiplies two imaginary numbers, and returns the 'real' part of the result.
	 * @param a Real part of the first number
	 * @param b Imaginary part of the first number
	 * @param c Real part of the second number
	 * @param d Imaginary part of the second number
	 * @return the real part of (a+b*i)*(c+d*i)
	 */
	public static double imagMultR(double a, double b, double c, double d){
		return a*c-b*d;
	}
	
	/**
	 * Multiplies two imaginary numbers, and returns the 'imaginary' part of the result.
	 * @param a Real part of the first number
	 * @param b Imaginary part of the first number
	 * @param c Real part of the second number
	 * @param d Imaginary part of the second number
	 * @return the imaginary part of (a+b*i)*(c+d*i)
	 */
	public static double imagMultI(double a, double b, double c, double d){
		return a*d+b*c;
	}
	
	/**
	 * Function that generates a nice-looking (First or Last) name.<br>
	 * Readability is improved by alternating between a vowel and a consonant.<br>
	 * Vowels that are common in the English language have a higher chance to end up in the names.<br>
	 * Names have between 3 and 12 characters.<br>
	 * This function always terminates.<br>
	 * @return the generated name
	 */
	public static String generateName(){
		String vowels = "aaaeeeiiiooouuuy";
		String consonants = "bbcddffgghjjkkllmmmnnnppqrrrsssttttvvwxz";
		String name = "";
		Random rand = new Random();
		Boolean wroteVowel = rand.nextBoolean();
		int length = (int)(rand.nextFloat()*rand.nextFloat()*10+3);
		for (int i=0; i<length; i++){
			if (!wroteVowel){
				name += vowels.charAt(rand.nextInt(vowels.length()));
			} else {
				name += consonants.charAt(rand.nextInt(consonants.length()));
			}
			if (i==0){
				name = name.toUpperCase();
			}
			wroteVowel = !wroteVowel;
		}
		return name;
	}
	
	/** the complement colour of a given colour */
	public static Color getComplementColour(Color c){
		return new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue()).brighter().brighter();
	}
	
	/** get the value x where b mod range == x mod range that is closest to a */
	public static double getClosestPoint(double a, double b, double range){
		double m = a - range/2;
		return Utils.floorMod(b-m, range)+m;
	}
	
	/** A copy of a list of GameObjects filled with clones of the GameObjects in the original list */
	@SuppressWarnings("unchecked")
	public static <GO extends GameObject> List<GO> deepCloneList(List<GO> objects){
		List<GO> clones = new ArrayList<>();
		for(GO o: objects){
			clones.add((GO) o.clone ());
		}
		
		return clones;
	}
}