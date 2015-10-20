package aoop.asteroids.model;


import org.json.simple.JSONArray;

/**
 * A simple text message that should only be displayed a certain amount of time on the screen.<br/>
 * It should fade in when starting display, and fade out afterwards.<br/>
 * Instances of Message are passed from Server to Client just like all GameObjects, to keep players updated about what happens.<br/>
 * <br/>
 * <b>Messages are used to display:</b><br/>
 * ->Game-related information (winning, dying, restarting)<br>
 * ->Connection-related information. (joining, losing connection)<br>
 * 
 * @author qqwy
 *
 */
public class Message {
	
	/* TODO:
	 * - find some way to annotate that GameMessage objects are immutable
	
	 * DONE:
	 * - rename to Message?
	 */
	
	/**
	 * The time this message was created at, in milliseconds.
	 */
	private long creationTime;
	
	/**
	 * The amount of milliseconds this message should be visible.
	 */
	private long visibleTime;
	
	/**
	 * The actual text of the message.
	 */
	private String message;
	
	/**
	 * This is tracked and sent to the client serialized to ensure that the client will not re-add messages it already recieved earlier.
	 * It is set to the Message's hashCode() on creation at the Server.
	 */
	private long id;
	
	/**
	 * Creates a new Message with given `message` that will display `visibleTime` milliseconds.
	 */
	public Message(String message, long visibleTime){
		this.message = message;
		this.creationTime = System.currentTimeMillis();
		this.visibleTime = visibleTime;
		this.id = this.hashCode();
	}
	
	/**
	 * A constructor that displays a message for the default of 5 seconds.
	 * @param message
	 */
	public Message(String message){
		this(message, 5000);
	}
	
	/**
	 * This special private constructor is used for deserialization.
	 * @see Message#fromJSON(JSONArray)
	 * @see Message#Message(String, long)
	 */
	private Message(String message, long visibleTime, long creationTime, long id){
		this(message, visibleTime);
		this.creationTime = creationTime;
		this.id = id;
	}
	
	/**
	 * A message should only be marked for destruction after it has faded out.
	 * @return true if it is marked for destruction.
	 */
	public boolean isDestroyed(){
		return System.currentTimeMillis() > creationTime + this.visibleTime ;
	}
	
	/**
	 * Using the principle of least surprise, this is the most logical name for a getter of the actual Message contents.
	 */
	@Override
	public String toString(){
		return this.message;
	}
	
	/**
	 * Used to check if this message is already known in the Client-Side Messages list.
	 * A Message is the same as another message if both have the same `id`. 
	 * @see Message#id
	 */
	@Override
	public boolean equals(Object o){
		if(o instanceof Message){
			return ((Message) o).id == this.id;
		}else{
			return false;
		}
	}
	
	/**
	 * Depending on the current offset from the creationTime, fades in the message, displays it at full opacity, or fades it out.
	 * For the actual fading algorithm, see Message#easingInOut
	 * @see Message#easingInOut(float, float, float, float)
	 * @return the opacity in (0.0-1.0) range
	 */
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
	
	
	/**
	 * @return a JSONArray containing the important characteristics of this Message
	 * (the message, the creationTime, the visibleTime and the id)
	 * @see Message#fromJSON()
	 */
	@SuppressWarnings("unchecked")
	public JSONArray toJSON(){
		JSONArray result = new JSONArray();
		result.add(this.message);
		result.add(this.creationTime);
		result.add(this.visibleTime);
		result.add(this.id);
		
		return result;
	}
	
	/**
	 * Reconstructs a message from the given JSONArray.
	 * @see Message#toJSON()
	 */
	public static Message fromJSON(JSONArray json){
		
		String message = (String) json.get(0);
		long creationTime = (long) json.get(1);
		long visibleTime = (long) json.get(2);
		long id = (long) json.get(3);
		
		return new Message(message, visibleTime, creationTime, id);
	}
	
	
	/**
	 * A standard Robert Penner Easing Equation.<br/>
	 * 
	 * The easing this function describes is a more-or-less quadratic-in-out easing.
	 * @param t The current time step, from the start of the animation (in the same unit as d)
	 * @param b the beginning value of the property
	 * @param c the change from the beginning value of the property
	 * @param d the total time the animation should take (in the same unit as t)
	 * @return a number between 0-1
	 */
	public float easingInOut(float t, float b, float c, float d){
		float ts=(t/=d)*t;
		float tc=ts*t;
		return b+c*(-2*tc + 3*ts);
	}
}
