package aoop.asteroids.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientConnection {
	private InetSocketAddress socketAddress;
	private long lastPingTime = 0;
	private long lastPacketId = 0;
	private boolean disconnected = false;

	public ClientConnection(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
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
		if(this.getLastPingTime() < currentTime - 5000){
			this.disconnected = true;
			System.err.println("Connection is not responding:"+this);
		}
	}
	
	
	@Override
	public String toString(){
		return this.socketAddress.toString();
	}
	
	public String toDebugString(){
		return this.toString() + " last ping time:"+ this.lastPingTime + "last packet ID:"+this.lastPacketId;
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
