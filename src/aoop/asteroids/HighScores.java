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

// import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class HighScores {
	
	private static HighScores instance = null;
	
	private static String dbname = "$objectdb/db/highscores.odb";
	
	
	private HighScores(){
        
		
	}
	

	
	public static HighScores getInstance(){
		if(instance == null){
			instance = new HighScores();
		}
		return instance;
	}
	
	public void saveScore(String name, long score){
		//long oldScore = getScore(name);
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(dbname);
		EntityManager em = emf.createEntityManager();
		PlayerScore ps = getScoreObj(em, name);
		
		// please don't use severe all the time
		// or if you do, change it back afterwards
		Logging.LOGGER.fine("name: "+name+ " score:"+score+" oldScore:"+ps);
		
		if(ps == null || score > ps.getScore()){
			
			em.getTransaction().begin();
			if(ps == null){
				Logging.LOGGER.fine("New PlayerScore created.");
				em.persist(new PlayerScore(name,score,System.currentTimeMillis()));
			}else{
				Logging.LOGGER.fine("PS updated.");
				ps.setScore(score);
			}
			em.getTransaction().commit();
			
		}
		emf.close();
	}
	
	public long getScore(String name){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(dbname);
		EntityManager em = emf.createEntityManager();
		PlayerScore ps = getScoreObj(em, name);
		emf.close();
		if(ps == null){
			return -1;
		}
		return ps.getScore();
	}
	
	private PlayerScore getScoreObj(EntityManager em, String name){
		
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
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(dbname);
		EntityManager em = emf.createEntityManager();
		TypedQuery<PlayerScore> query = em.createQuery("SELECT p FROM PlayerScore p WHERE p.datetime > :maxtime ORDER BY p.score DESC", PlayerScore.class);
		ArrayList<PlayerScore> list = (ArrayList<PlayerScore>) query.setParameter("maxtime", datetime).setMaxResults(10).getResultList();
		emf.close();
		return list;
	}
	
	public ArrayList<PlayerScore> getLastHourHighScores(){
		long msInHour = (1000*60*60);
		return getHighScoresNewerThan(System.currentTimeMillis()-msInHour);
	}
}
