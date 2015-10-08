package aoop.asteroids;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class HighScores {
	
	private static HighScores instance = null;
	
	private static String filename = "highscores.json";
	
	private HashMap<String,long[]> scores = null;
	
	private HighScores(){
		this.loadScores();
	}
	
	public static HighScores getInstance(){
		if(instance == null){
			instance = new HighScores();
		}
		return instance;
	}
	
	private void loadScores(){
		this.scores = new HashMap<>();
		
		//String.join("\n", Files.readAllLines(Paths.get(HighScores.filename), Charset.forName("UTF-8")));
	    try {
			String encodedScores = new String(Files.readAllBytes(Paths.get(HighScores.filename)), Charset.forName("UTF-8"));
			JSONArray jsonScores=(JSONArray) JSONValue.parse(encodedScores);
			for(int i=0;i<jsonScores.size();i++){
				JSONArray elem = (JSONArray) jsonScores.get(i);
				String name = (String) elem.get(0);
				long score = (long) elem.get(1);
				long datetime = (long) elem.get(2);
				this.scores.put(name, new long[]{score,datetime});
			}
	    } catch (IOException e) {
			//If no file available, do nothing. New one will be made after first player scores.
		}
	}
	
	
}
