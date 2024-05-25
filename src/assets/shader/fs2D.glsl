#version 330 core

in vec2 fTexCoords;

uniform sampler2D uFontTexture;

out vec4 color;

void main()
{
    color = texture(uFontTexture, fTexCoords);
}