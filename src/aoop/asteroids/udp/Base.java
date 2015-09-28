package aoop.asteroids.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Base {
	public Base(){
		byte[] buffer = new byte[65508];
		InetAddress address;
		try {
			address = InetAddress.getByAddress();
			DatagramPacket packet = new DatagramPacket(
		    buffer, buffer.length, address, 9000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
