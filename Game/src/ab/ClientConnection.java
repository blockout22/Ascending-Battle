package ab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import packets.Packet;
import shader.Vector3f;

public class ClientConnection {

	private static boolean running = false;
	private static DatagramSocket socket;
	private static String ip;
	private static int port = 1234;
	static long startTime = System.currentTimeMillis();
	static long endTime = 0;

	public static ArrayList<Player> players = new ArrayList<Player>();

	public static void connect() {
		new Thread(new Runnable() {
			public void run() {
				try {
					URL url = new URL("http://blockout22.com/Ascending_Battle");
					BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

					ip = br.readLine();
					ip = "localhost";
					System.out.println(ip);
					br.close();
					socket = new DatagramSocket();
					send(Packet.CHECK_SERVER_ONLINE);
					byte[] buffer = new byte[Packet.CHECK_SERVER_ONLINE.length()];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					socket.setSoTimeout(5000);
					socket.receive(packet);
					String response = new String(packet.getData());
					if (!response.equals(Packet.CHECK_SERVER_ONLINE)) {
						return;
					}
					running = true;
						String username = JOptionPane.showInputDialog("Enter Username");
						Player player = new Player(username, new Vector3f(0, 0f, -15f), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
						players.add(player);
						send(Packet.CONNECT + player.getUsername());
					// byte[] buffer = "Hello Server".trim().getBytes();
					// DatagramPacket packet = new DatagramPacket(buffer,
					// buffer.length, InetAddress.getByName(ip), 1234);
					// socket.send(packet);

					listen();

				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "No response from server");
					return;
				}
			}
		}).start();
	}

	private static void listen() {
		new Thread(new Runnable() {
			public void run() {
				while (running) {
					// System.out.println("SIZEL: " + players.size());
					// System.out.println("LISTENING FOR SERVER MESSAGES");
					byte[] buffer = new byte[1024];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						socket.setSoTimeout(5000);
						socket.receive(packet);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
					endTime = System.currentTimeMillis();

					String s = new String(packet.getData()).trim();

					if (s.startsWith(Packet.PING)) {
						send(Packet.PING);
					} else if (s.startsWith(Packet.USER_TAKEN)) {
						System.err.println("User already connected");
						disconnect();
					} else if (s.startsWith(Packet.USER_CONNECT)) {
						String username = s.substring(Packet.USER_CONNECT.length());
						Player player = new Player(username, new Vector3f(0, 0, -15), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
						players.add(player);
						System.out.println("USER CONNECT: " + player.getUsername());
					} else if (s.startsWith(Packet.MOVE)) {
						String message = s.substring(Packet.MOVE.length());
						// x = Float.valueOf(message);
						// System.out.println("@@@@@@@@@@@@@" + message);
						try {
							String username = message.split(":")[0];
							String pos = message.split(":")[1];
							String rot = message.split(":")[2];
							String scl = message.split(":")[3];

							Vector3f position = new Vector3f(Float.valueOf(pos.split(",")[0]), Float.valueOf(pos.split(",")[1]), Float.valueOf(pos.split(",")[2]));
							Vector3f rotation = new Vector3f(Float.valueOf(rot.split(",")[0]), Float.valueOf(rot.split(",")[1]), Float.valueOf(rot.split(",")[2]));
							Vector3f scale = new Vector3f(Float.valueOf(scl.split(",")[0]), Float.valueOf(scl.split(",")[1]), Float.valueOf(scl.split(",")[2]));

							// System.out.println(position);
//							System.out.println(players.get(0).getUsername() + " : " + username);
							if (players.get(0).getUsername().equals(username)) {
								players.get(0).setPosition(position);
								players.get(0).setRotation(rotation);
								players.get(0).setScale(scale);
								// continue;
							} else {
								boolean userExists = false;
								for (int i = 1; i < players.size(); i++) {
									if (players.get(i).getUsername().equals(username)) {
										players.get(i).setPosition(position);
										players.get(i).setRotation(rotation);
										players.get(i).setScale(scale);
										userExists = true;
										break;
									}
								}
//								System.out.println(userExists);
								if (!userExists) {
									System.out.println("NEW USER");
									players.add(new Player(username, position, rotation, scale));
									System.out.println("Added User");
								}
							}
						} catch (IndexOutOfBoundsException e) {
							e.printStackTrace();
							return;
						}
					} else if (s.startsWith(Packet.USER_DISCONNECT)) {
						int userToRemove = -1;
						System.out.println("USER DISCONNECT PACKET: " + s);
						for (int i = 0; i < players.size(); i++) {
							String username = s.substring(Packet.USER_DISCONNECT.length());
							if (players.get(i).getUsername().equals(username)) {
								userToRemove = i;
								break;
							}
						}
						if (userToRemove != -1) {
							System.out.println("Removed User: " + players.get(userToRemove).getUsername());
							players.remove(userToRemove);
						}
					}

					// System.out.println(s + " : [" + (endTime - startTime) +
					// "ms]");
				}
			}
		}).start();
	}

	public static void send(String message) {
		byte[] buffer = message.getBytes();
		send(buffer);
	}

	private static void send(byte[] buffer) {
		try {
			startTime = System.currentTimeMillis();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void disconnect() {
		running = false;
		synchronized (socket) {
			// send("Bye I'm Disconnecting");
			send(Packet.DISCONNECT);
			socket.close();
		}
	}

}
