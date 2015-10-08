package aoop.asteroids;

import java.io.Serializable;
import javax.persistence.*;

@Entity
public class PlayerScore implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue
    private long id;
	
	private String name;
	private long score;
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
		return this.name+": "+this.score;
	}
	
}
