package aoop.asteroids.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientConnection {
	private InetSocketAddress socketAddress;
	private long lastPingTime = 0;
	private long lastPacketId = 0;
	private boolean disconnected = false;
	private String name = "";

	public ClientConnection(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}
	private ClientConnection(InetSocketAddress socketAddress, long lastPingTime, long lastPacketId, boolean disconnected, String name){
		this(socketAddress);
		this.lastPingTime = lastPingTime;
		this.lastPacketId = lastPacketId;
		this.disconnected = disconnected;
		this.name = name;
	}

	public InetAddress getAddress() {
		return this.getSocketAddress().getAddress();
	}

	public int getPort() {
		return this.getSocketAddress().getPort();
	}

	public long getLastPingTime() {
		return lastPingTime;
	}

	public void setLastPingTime(long lastPingTime) {
		this.lastPingTime = lastPingTime;
	}

	public long getLastPacketId() {
		return lastPacketId;
	}

	public void updateLastPacketId(long lastPacketId) {
		this.lastPacketId = Math.max(this.lastPacketId, lastPacketId);
	}

	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}

	public void setSocketAddress(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}
	
	public boolean isDisconnected(){
		return this.disconnected;
	}
	
	public void tagAsDisconnectedIfNotResponding(){
		long currentTime = System.currentTimeMillis();
		if(this.getLastPingTime() < currentTime - Server.MaxNonRespondTime){
			this.disconnected = true;
			System.err.println("Connection is not responding:"+this.toDebugString());
		}
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	
	@Override
	public String toString(){
		return this.socketAddress.toString();
	}
	
	public String toDebugString(){
		return this.toString() + " last ping time:"+ this.lastPingTime + "last packet ID:"+this.lastPacketId;
	}
	
	public ClientConnection clone(){
		return new ClientConnection(socketAddress, lastPingTime, lastPacketId, disconnected, name);
	}
	
	@Override
	public boolean equals(Object b){
		if(! (b instanceof ClientConnection)){
			return false;
		}else{
			return toString().equals(b.toString());
		}
	}
	
	@Override
	public int hashCode(){
		return this.toString().hashCode();
		
	}
}
