#version 400

in vec3 position;
in vec2 texCoords;

out vec2 textureCoords;

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(){
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
	textureCoords = texCoords;
}