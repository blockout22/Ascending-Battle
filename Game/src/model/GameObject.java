package model;

import shader.Vector3f;

public class GameObject {
	
	private Vector3f position;
	private Vector3f rotation;
	private Vector3f scale;
	
	private int textureID = 1;

	public GameObject(Vector3f position, Vector3f rotation, Vector3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public void update()
	{
	}
	
	public void setPositionX(float x)
	{
		getPosition().x = x;
	}
	
	public void setPositionY(float y)
	{
		getPosition().y = y;
	}
	
	public void setPositionZ(float z)
	{
		getPosition().z = z;
	}
	
	public void setScaleX(float x)
	{
		getScale().x = x;
	}
	
	public void setScaleY(float y)
	{
		getScale().y = y;
	}
	
	public void setScaleZ(float z)
	{
		getScale().z = z;
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

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public int getTextureID() {
		return textureID;
	}

	public void setTextureID(int textureID) {
		this.textureID = textureID;
	}
}
