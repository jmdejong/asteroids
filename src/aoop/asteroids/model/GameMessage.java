package aoop.asteroids.model;


import org.json.simple.JSONArray;

public class GameMessage {
	private long creationTime;
	
	private long visibleTime;
	
	private String message;
	
	public GameMessage(String message, long visibleTime){
		this.message = message;
		this.creationTime = System.currentTimeMillis();
		this.visibleTime = visibleTime;
	}
	
	public GameMessage(String message){
		this(message, 5000);
	}
	
	private GameMessage(String message, long visibleTime, long creationTime){
		this(message, visibleTime);
		this.creationTime = creationTime;
	}
	
	public boolean isDestroyed(){
		return System.currentTimeMillis() > creationTime + this.visibleTime ;
	}
	
	@Override
	public String toString(){
		return this.message;
	}
	
	public float getOpacity(){
		if(this.isDestroyed()){
			return 0;
		}
		long time = System.currentTimeMillis() - creationTime;
		
		if(time == 0 || time == visibleTime){
			return 0;
		}else if(time < visibleTime/4){
			return (float)easingInOut(time, 0, 1 , visibleTime/4);
		}else if (time > (visibleTime/4)*3){
			return (float)easingInOut(visibleTime - time, 0, 1,visibleTime/4);
		}else{
			return 1;
		}
	}
	
	/** return when the message is over and shoult not be shown anymore 
	 * @return whether the message should be removed
	 */
	public Boolean isOver(){
		return System.currentTimeMillis() > creationTime + visibleTime;
	}
	
	public JSONArray toJSON(){
		JSONArray result = new JSONArray();
		result.add(this.message);
		result.add(this.creationTime);
		result.add(this.visibleTime);
		
		return result;
	}
	
	public static GameMessage fromJSON(JSONArray json){
		
		String message = (String) json.get(0);
		long creationTime = (long) json.get(1);
		long visibleTime = (long) json.get(2);
		
		return new GameMessage(message, visibleTime, creationTime);
	}
	
	
	
	public float easingInOut(float t, float b, float c, float d){
		float ts=(t/=d)*t;
		float tc=ts*t;
		return b+c*(-2*tc + 3*ts);
	}
}
