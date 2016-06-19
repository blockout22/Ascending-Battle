package ab;

import input.Input;
import input.Key;
import model.GameObject;
import packets.Packet;
import shader.Vector3f;
import window.Window;

public class Player extends GameObject{
	
	private String username;

	public Player(String username, Vector3f position, Vector3f rotation, Vector3f scale) {
		super(position, rotation, scale);
		this.username = username;
	}
	
	public void move() {
		if (Input.isKeyDown(Window.getWindowID(), Key.KEY_W)) {
			ClientConnection.send(Packet.FORWARD);
		}

		if (Input.isKeyDown(Window.getWindowID(), Key.KEY_S)) {
			ClientConnection.send(Packet.BACK);
		}

		if (Input.isKeyDown(Window.getWindowID(), Key.KEY_A)) {
			ClientConnection.send(Packet.LEFT);
		}

		if (Input.isKeyDown(Window.getWindowID(), Key.KEY_D)) {
			ClientConnection.send(Packet.RIGHT);
		}
		
		if(Input.isKeyDown(Window.getWindowID(), Key.KEY_Q))
		{
			ClientConnection.send(Packet.ROTATE + "LEFT");
		}
		
		if(Input.isKeyDown(Window.getWindowID(), Key.KEY_E))
		{
			ClientConnection.send(Packet.ROTATE + "RIGHT");
		}
	}

	public String getUsername() {
		return username;
	}

	public void setTranslation(Player player) {
		setPosition(player.getPosition());
		setRotation(player.getRotation());
		setScale(player.getScale());
	}
}
