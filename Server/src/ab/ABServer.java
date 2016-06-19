package ab;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import packets.Packet;

public class ABServer {

	private boolean running = false;
	private DatagramSocket socket;
	private int port;

	private ArrayList<Client> clients = new ArrayList<Client>();
	private ArrayList<Client> clientsToRemove = new ArrayList<Client>();

	public ABServer(int port) {
		try {
			socket = new DatagramSocket(port);
			this.port = port;
			running = true;
			System.out.println("[Server] Listening on port... " + port);
		} catch (SocketException e) {
			e.printStackTrace();
			running = false;
			return;
		}

		console();
		ping();
		listen();
	}

	private void console() {
		new Thread(new Runnable() {
			public void run() {
				Scanner scanner = new Scanner(System.in);
				while (running) {
					if (scanner.next().equals("stop")) {
						System.out.println("Stopping Server...");
						try {
							stopServer();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				scanner.close();
			}
		}).start();
	}

	private void ping() {
		new Thread(new Runnable() {
			public void run() {
				while (running) {
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).getPingAttempts() < 5) {
							send(Packet.PING, clients.get(i).getIp(), clients.get(i).getPort());
							clients.get(i).addPingAttempt();
						} else {
							clientsToRemove.add(clients.get(i));
						}
					}

					for (int i = 0; i < clientsToRemove.size(); i++) {
						System.out.println("Removed " + clientsToRemove.get(i).getUsername());
						clients.remove(clientsToRemove.get(i));
					}
					clientsToRemove.clear();

					sendAllPositions();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	private void listen() {
		// deltaThread();
		while (running) {
			byte[] buffer = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String s = new String(packet.getData()).trim();

			if(s.startsWith(Packet.CHECK_SERVER_ONLINE)){
				send(Packet.CHECK_SERVER_ONLINE, packet.getAddress(), packet.getPort());
			}else if (s.startsWith(Packet.CONNECT)) {
				handleConnect(s, packet);
			} else if (s.startsWith(Packet.DISCONNECT)) {
				handleDisconnect(s, packet);
			} else if (s.startsWith(Packet.PING)) {
				handlePingResponse(packet);
			} else if (s.startsWith(Packet.FORWARD)) {
				handleMove(s, packet, 0);
			} else if (s.startsWith(Packet.BACK)) {
				handleMove(s, packet, 1);
			} else if (s.startsWith(Packet.LEFT)) {
				handleMove(s, packet, 2);
			} else if (s.startsWith(Packet.RIGHT)) {
				handleMove(s, packet, 3);
			}else if(s.startsWith(Packet.ROTATE))
			{
				//TODO read direction
				handleRotation(s, packet, 0);
			}
		}

		socket.close();
	}

	private void handleRotation(String s, DatagramPacket packet, int direction) {
		for(int i = 0; i < clients.size(); i++)
		{
			if (clients.get(i).getIp().equals(packet.getAddress())) {
				if (clients.get(i).getPort() == packet.getPort()) {
					float sensitivity = 0.1f;
					
					if(direction == 0)
					{
						clients.get(i).getRotation().y += sensitivity;
						sendAllPositions();
						break;
					}
				}
			}
		}
	}

	private void handlePingResponse(DatagramPacket packet) {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getIp().equals(packet.getAddress())) {
				if (clients.get(i).getPort() == packet.getPort()) {
					clients.get(i).resetPingAttempts();
					break;
				}
			}
		}
	}

	// 0 = forward; 1 = back; 2 = left; 3 = right;
	private void handleMove(String s, DatagramPacket packet, int direction) {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getIp().equals(packet.getAddress())) {
				if (clients.get(i).getPort() == packet.getPort()) {
					float speed = 0.1f;
					float x = 0;
					float z = 0;
					if (direction == 0) {
						x = (float) (Math.sin(clients.get(i).getRotation().y * Math.PI / 180) * speed);
						z = (float) (-Math.cos(clients.get(i).getRotation().y * Math.PI / 180) * speed);
					} else if (direction == 1) {
						x = (float) -(Math.sin(clients.get(i).getRotation().y * Math.PI / 180) * speed);
						z = (float) -(-Math.cos(clients.get(i).getRotation().y * Math.PI / 180) * speed);
					} else if (direction == 2) {
						x = (float) (Math.sin((clients.get(i).getRotation().y - 90) * Math.PI / 180) * speed);
						z = (float) (-Math.cos((clients.get(i).getRotation().y - 90) * Math.PI / 180) * speed);
					} else if (direction == 3) {
						x = (float) (Math.sin((clients.get(i).getRotation().y + 90) * Math.PI / 180) * speed);
						z = (float) (-Math.cos((clients.get(i).getRotation().y + 90) * Math.PI / 180) * speed);
					}
					clients.get(i).moveX(x);
					clients.get(i).moveZ(z);
//					System.out.println(x + " : " + z);
//					System.out.println(clients.get(i).getPosition());
//					updateClients();
					sendAllPositions();
					break;
				}
			}
		}
	}

	private void handleConnect(String message, DatagramPacket packet) {
		String username = message.substring(Packet.CONNECT.length());
		if (username.length() > 5) {
			boolean userTaken = false;
			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i).getUsername().equals(username)) {
					userTaken = true;
					break;
				}
			}
			if (userTaken) {
				send(Packet.USER_TAKEN, packet.getAddress(), packet.getPort());
				return;
			} else {
				Client client = new Client(generateID(), username, packet.getAddress(), packet.getPort());
				clients.add(client);
				System.out.println(username + " Connected");
				System.out.println(client.getPosition());
				System.out.println(client.getRotation());
				System.out.println(client.getScale());
				send("Welcome " + username, packet.getAddress(), packet.getPort());
				sendToAllOthers(Packet.USER_CONNECT + username);
//				updateClients();
				sendAllPositions();
			}
			// send all clients connected
		}else{
			send(Packet.USER_TAKEN, packet.getAddress(), packet.getPort());
			return;
		}
	}

	private void handleDisconnect(String message, DatagramPacket packet) {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getIp().equals(packet.getAddress())) {
				if (clients.get(i).getPort() == packet.getPort()) {
					System.out.println(clients.get(i).getUsername() + " Disconnected");
					sendToAll(Packet.USER_DISCONNECT + clients.get(i).getUsername());
					clientsToRemove.add(clients.get(i));
					break;
				}
			}
		}
	}
	
	private void sendAllPositions()
	{
		for(int i = 0; i < clients.size(); i++)
		{
			for(int j = 0; j < clients.size(); j++)
			{
				send(Packet.MOVE + clients.get(j).getUsername() + ":" + clients.get(j).getPosition() + ":" + clients.get(j).getRotation() + ":" + clients.get(j).getScale(), clients.get(i).getIp(), clients.get(i).getPort());
			}
		}
	}

//	private void updateClients() {
//		for (int i = 0; i < clients.size(); i++) {
//			send(Packet.MOVE + clients.get(i).getUsername() + ":" + clients.get(i).getPosition() + ":" + clients.get(i).getRotation() + ":" + clients.get(i).getScale(), clients.get(i).getIp(), clients.get(i).getPort());
//		}
//	}

	public void send(String message, InetAddress ip, int port) {
		byte[] buffer = message.getBytes();
		send(buffer, ip, port);
	}

	private void send(byte[] buffer, InetAddress ip, int port) {
		try {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ip, port);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendToAll(String message)
	{
		for(int i = 0; i < clients.size(); i++)
		{
			send(message, clients.get(i).getIp(), clients.get(i).getPort());
		}
	}
	
	private void sendToAllOthers(String message)
	{
		for(int i = 0; i < clients.size() - 1; i++)
		{
			send(message, clients.get(i).getIp(), clients.get(i).getPort());
		}
	}

	private UUID generateID() {
		UUID id = UUID.randomUUID();
		return id;
	}

	private void stopServer() throws Exception {
		running = false;
		byte[] buffer = "".getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(""), port);
		clients.clear();
		socket.send(packet);
	}

	public static void main(String[] args) {
		new ABServer(1234);
	}

}
