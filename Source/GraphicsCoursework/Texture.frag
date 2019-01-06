#version 330 core

in  vec4 color;
in  vec2 textureCoord;

out vec4 fColor;

uniform sampler2D sampler;

void main()
{
	fColor = color * texture(sampler, textureCoord);
}