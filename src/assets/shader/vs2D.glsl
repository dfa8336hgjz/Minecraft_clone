#version 330 core
layout(location=0) in vec2 aPos;
layout(location=1) in vec2 aTexCoords;

out vec2 fTexCoords;

uniform mat4 uProjection;

void main()
{
    fTexCoords = aTexCoords;
    gl_Position = uProjection * vec4(aPos, -5, 1);
}