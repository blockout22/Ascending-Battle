package ab;

import java.net.InetAddress;
import java.util.UUID;

public class Client {
	
	private UUID id;
	private String username;
	private Vector3f position = new Vector3f(0f, 0f, -15f);
	private Vector3f rotation = new Vector3f();
	private Vector3f scale = new Vector3f(1f);
	private InetAddress ip;
	private int port;
	private int pingAttempts = 0;
	
	public Client(UUID id, String username, InetAddress ip, int port) {
		this.id = id;
		this.username = username;
		this.ip = ip;
		this.port = port;
	}

	public void moveX(float amt) {
		this.getPosition().x += amt;
	}

	public void moveY(float amt) {
		this.getPosition().y += amt;
	}

	public void moveZ(float amt) {
		this.getPosition().z += amt;
	}
	
	public UUID getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}
	
	public void resetPingAttempts()
	{
		pingAttempts = 0;
	}
	
	public void addPingAttempt()
	{
		pingAttempts++;
	}
	
	public int getPingAttempts()
	{
		return pingAttempts;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}
}
