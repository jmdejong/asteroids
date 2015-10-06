package aoop.asteroids.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientConnection {
	private InetSocketAddress socketAddress;
	private long lastPingTime = 0;
	private long lastPacketId = 0;

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

	public void setLastPacketId(long lastPacketId) {
		this.lastPacketId = lastPacketId;
	}

	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}

	public void setSocketAddress(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}
	
	public String toString(){
		return this.socketAddress.toString();
	}
	
	
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
