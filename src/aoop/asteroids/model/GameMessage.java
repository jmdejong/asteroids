package aoop.asteroids.model;

public class GameMessage {
	private long creationTime;
	
	public static long visibleTime = 5000;
	
	private String message;
	
	public GameMessage(String message){
		this.message = message;
		this.creationTime = System.currentTimeMillis();
	}
	
	public boolean isDestroyed(){
		return System.currentTimeMillis() > creationTime + GameMessage.visibleTime ;
	}
	
	@Override
	public String toString(){
		return this.message;
	}
}
