package aoop.asteroids.udp;

public abstract class Base {
	protected BaseReciever reciever;
	protected BaseSender sender;
	
	/**
	 * If the other party takes longer than this to send a packet, consider the connection as lost.
	 */
	public static int MaxNonRespondTime = 5000;
	

}
