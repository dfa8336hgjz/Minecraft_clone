#version 330 core

in vec3 fragPos;
in vec2 fragTexCoord;
flat in uint fragFace;

out vec4 fragColor;
uniform sampler2D txt;

void faceToNormal(in uint face, out vec3 normal){
    switch(face)
	{
		case uint(0):
			normal = vec3(0, 1, 0);
			break;
		case uint(1):
			normal = vec3(0, -1, 0);
			break;
		case uint(2):
			normal = vec3(0, 0, 1);
			break;
		case uint(3):
			normal = vec3(0, 0, -1);
			break;
		case uint(4):
			normal = vec3(-1, 0, 0);
			break;
		case uint(5):
			normal = vec3(1, 0, 0);
			break;
	}
}

void main()
{
    vec3 sunPosition = vec3(1, 200, 2);
    vec3 lightColor = vec3(1, 1, 1);

    vec3 fragNormal;
    faceToNormal(fragFace, fragNormal);
    
    float ambientStrength = 0.4;
    vec3 ambient = ambientStrength * lightColor;

	vec3 lightDir = normalize(sunPosition - fragPos);

	float diff = max(dot(fragNormal, lightDir), 0.0);
	vec3 diffuse = diff * lightColor;

	vec3 objectColor = texture(txt, fragTexCoord).rgb;
	vec3 result = (diffuse + ambient) * objectColor;

    fragColor = vec4(result, 0.0);
}