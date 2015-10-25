package aoop.asteroids;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The PlayerScore object is constructed to be save-able in the Objectdb database.
 * It is mostly a datastore with three fields:<br>
 * <br>
 * <ul>
 * <li>A player name</li>
 * <li>The player's high score</li>
 * <li>The time (in milliseconds since the Epoch) at which this score was obtained</li>
 * </ul>
 * @author qqwy
 *
 */
@Entity
public class PlayerScore implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue
    private long id;
	
	/**
	 * The player's name
	 */
	private String name;
	
	/**
	 * The player's highest score.
	 */
	private long score;
	
	/**
	 * The timestamp (in milliseconds since the Epoch) at which the player obtained this score.
	 */
	private long datetime;
	
	public PlayerScore(String name, long score, long datetime){
		this.setName(name);
		this.setScore(score);
		this.setDatetime(datetime);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public long getDatetime() {
		return datetime;
	}

	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}
	
	public String toString(){
		return String.format("%s: %5d",this.name, this.score);
	}
	
}
