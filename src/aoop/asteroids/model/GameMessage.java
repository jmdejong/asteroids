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
	
	public float easingInOut(float t, float b, float c, float d){
		float ts=(t/=d)*t;
		float tc=ts*t;
		return b+c*(-2*tc + 3*ts);
	}
}
