package aoop.asteroids;

import java.util.Random;

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
}