package aoop.asteroids;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class HighScores {
	
	private static HighScores instance = null;
	
	private static String filename = "highscores.json";
	
	private EntityManager em = null;
	
	private HighScores(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("$objectdb/db/points.odb");
        em = emf.createEntityManager();
		
	}
	
	public static HighScores getInstance(){
		if(instance == null){
			instance = new HighScores();
		}
		return instance;
	}
	
	public void saveScore(String name, long score){
		//long oldScore = getScore(name);
		PlayerScore ps = getScoreObj(name);
		
		if(ps == null || score > ps.getScore()){
			
			em.getTransaction().begin();
			if(ps == null){
				em.persist(new PlayerScore(name,score,System.currentTimeMillis()));
			}else{
				ps.setScore(score);
			}
			em.getTransaction().commit();
		}
	}
	
	public long getScore(String name){
		PlayerScore ps = getScoreObj(name);
		if(ps == null){
			return -1;
		}
		return ps.getScore();
	}
	
	private PlayerScore getScoreObj(String name){
		TypedQuery<PlayerScore> query = em.createQuery("SELECT p FROM PlayerScore p WHERE p.name == '"+name+"'", PlayerScore.class);
		ArrayList<PlayerScore> list = (ArrayList<PlayerScore>) query.getResultList();
		if(list.isEmpty()){
			return null;
		}
		return list.get(0);
	}
	
	public ArrayList<PlayerScore> getHighScores(){
		return getHighScoresNewerThan(Long.MIN_VALUE);
	}
	
	public ArrayList<PlayerScore> getHighScoresNewerThan(long datetime){
		
		TypedQuery<PlayerScore> query = em.createQuery("SELECT p FROM PlayerScore p WHERE p.datetime > :maxtime ORDER BY p.score DESC", PlayerScore.class);
		ArrayList<PlayerScore> list = (ArrayList<PlayerScore>) query.setParameter("maxtime", datetime).setMaxResults(10).getResultList();
		
		return list;
	}
	
	public ArrayList<PlayerScore> getLastHourHighScores(){
		long msInHour = (1000*60*60);
		return getHighScoresNewerThan(System.currentTimeMillis()-msInHour);
	}
}
