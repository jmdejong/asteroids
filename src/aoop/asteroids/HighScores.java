package aoop.asteroids;


import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

/**
 * This class uses Objectdb (http://www.objectdb.org) to store and fetch player high scores persistently.<br.>
 * <br>
 * It is implemented as a Singleton class, as we never need more than one reference to the high scores object, because the Objectdb database will lock. Another reason is that results should be consistent everywhere, all the time.
 * <br>
 * @author Wiebe-Marten Wijnja & Michiel de Jong
 *
 */
public class HighScores {
	
	/**
	 * The actual singleton-instance.
	 */
	private static HighScores instance = null;
	
	/**
	 * Objectdb requires this field. It contains the actual location of the database file.
	 */
	private static String dbname = "$objectdb/db/highscores.odb";
	
	/**
	 * Private constructor, used when initializing the singleton.
	 */
	private HighScores(){
	}
	

	/**
	 * Public function that returns the sungleton instance (after creating it if it did not yet exist)
	 */
	public static HighScores getInstance(){
		if(instance == null){
			instance = new HighScores();
		}
		return instance;
	}
	
	/**
	 * Attempts to store a players new score.<br>
	 * <br>
	 * The players new score is compared with their current high score in the database.<br>
	 * It is only added when the new score is actually higher.<br>
	 * @param name the player's name
	 * @param score the score the player has obtained in the game.
	 */
	public void saveScore(String name, long score){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(dbname);
		EntityManager em = emf.createEntityManager();
		PlayerScore ps = getScoreObj(em, name);
		
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
	
	/**
	 * Returns the current score for a certain player.
	 * This is used to compare the current player's scores against their personal high score during the game.
	 * @param name
	 * @return
	 */
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
	
	/**
	 * Returns the PlayerScore object that belongs to a certain playername.
	 * @param em
	 * @param name
	 * @return
	 */
	private PlayerScore getScoreObj(EntityManager em, String name){
		
		TypedQuery<PlayerScore> query = em.createQuery("SELECT p FROM PlayerScore p WHERE p.name == '"+name+"'", PlayerScore.class);
		ArrayList<PlayerScore> list = (ArrayList<PlayerScore>) query.getResultList();
		if(list.isEmpty()){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * @return the ten best high scores of all time, contained in an ArrayList of PlayerScore objects.
	 * 
	 */
	public ArrayList<PlayerScore> getHighScores(){
		return getHighScoresNewerThan(Long.MIN_VALUE);
	}
	
	/**
	 * @return the ten best high scores of the last hour, contained in an ArrayList of PlayerScore objects.
	 * 
	 */
	public ArrayList<PlayerScore> getLastHourHighScores(){
		long msInHour = (1000*60*60);
		return getHighScoresNewerThan(System.currentTimeMillis()-msInHour);
	}
	
	/**
	 * @return the ten best high scores between now and `datetime` milliseconds ago, contained in an ArrayList of PlayerScore objects.
	 * 
	 */
	public ArrayList<PlayerScore> getHighScoresNewerThan(long datetime){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(dbname);
		ArrayList<PlayerScore> list = new ArrayList<PlayerScore>();
		try{
			
			EntityManager em = emf.createEntityManager();
			TypedQuery<PlayerScore> query = em.createQuery("SELECT p FROM PlayerScore p WHERE p.datetime > :maxtime ORDER BY p.score DESC", PlayerScore.class);
			list = (ArrayList<PlayerScore>) query.setParameter("maxtime", datetime).setMaxResults(10).getResultList();
			
		}catch(PersistenceException e){
			Logging.LOGGER.warning("Database file could not be opened:"+e.getMessage());
			
		}finally{
			emf.close();
			
		}
		return list;

	}
	
	
}
