#version 330 core
layout (location=0) in uint vertdata;

out vec3 fragPos;
out vec2 fragTexCoord;
out vec3 fragColor;
flat out uint fragFace;

uniform samplerBuffer texCoordBuffer;
uniform mat4 view;
uniform mat4 model;
uniform mat4 projection;
uniform ivec2 chunkPos;

#define POS_ID_BITMASK uint(0x1FFFF)
#define TEX_ID_BITMASK uint(0x7FE0000)
#define FACE_ID_BITMASK uint(0x38000000)
#define UV_ID_BITMASK uint(0xC0000000)
#define BASE_WIDTH uint(17)
#define BASE_HEIGHT uint(151)
#define BASE_DEPTH uint(17)

void extractPosition(in uint vertdata, out vec3 pos){
    uint posId = vertdata & POS_ID_BITMASK;
    uint x = posId % BASE_WIDTH;
    uint y = (posId - x) / BASE_WIDTH % BASE_HEIGHT;
    uint z = (posId - x - y * BASE_WIDTH) / (BASE_WIDTH * BASE_HEIGHT);

    pos = vec3(float(x), float(y), float(z));
}

void extractFace(in uint vertdata, out uint face){
    face = (vertdata & FACE_ID_BITMASK) >> 27;
}

void extractUV(in uint vertdata, out vec2 texCoords){
    uint uvId = (vertdata & UV_ID_BITMASK) >> 30;
    uint texId = (vertdata & TEX_ID_BITMASK) >> 17;
	int index = int((texId * uint(8)) + (uvId * uint(2)));
    switch(uvId)
	{
		case uint(0):
			texCoords = vec2(0.0, 0.0);
			break;
		case uint(1):
			texCoords = vec2(0.0, 1.0);
			break;
		case uint(2):
			texCoords = vec2(1.0, 0.0);
			break;
		case uint(3):
			texCoords = vec2(1.0, 1.0);
			break;
	}

	texCoords.x = texelFetch(texCoordBuffer, index + 0).r;
	texCoords.y = texelFetch(texCoordBuffer, index + 1).r;
}

void main()
{
    extractPosition(vertdata, fragPos);
    extractFace(vertdata, fragFace);
    extractUV(vertdata, fragTexCoord);

    fragPos.x += chunkPos.x * 16.0f;
    fragPos.z += chunkPos.y * 16.0f;

    gl_Position = projection * view * model * vec4(fragPos, 1.0);

	fragColor = vec3(1, 1, 1);
}