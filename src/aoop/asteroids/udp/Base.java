package aoop.asteroids.udp;

/**
 * The 'base' UDP-connection class that both the Client and Server inherit from.
 * It stores some standard settings for both sides to use.
 * 
 * Both the Client and the Server have a Reciever and a Sender object, to handle the technical details of the connection.
 * @author Wiebe-Marten Wijnja, Michiel de Jong
 *
 */
public abstract class Base {
	protected BaseReceiver reciever;
	
	/**
	 * If the other party takes longer than this to send a packet, consider the connection as lost.
	 */
	public static int MaxNonRespondTime = 2500;
	

}
