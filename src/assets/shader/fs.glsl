#version 330 core

in vec3 fragPos;
in vec2 fragTexCoord;
in vec3 fragColor;
flat in uint fragFace;

out vec4 FragColor;
uniform sampler2D txt;
uniform vec3 playerPos;

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
    vec3 sunPosition = vec3(1, 800, 1000);
    vec3 lightColor = vec3(1, 1, 1);

    vec3 fragNormal;
    faceToNormal(fragFace, fragNormal);
    
    float ambientStrength = 0.3;
    vec3 ambient = ambientStrength * lightColor;

	vec3 lightDir = normalize(sunPosition - fragPos);

	float diff = max(dot(fragNormal, lightDir), 0.0);
	vec3 diffuse = diff * lightColor;


	float distanceToPlayer = length(fragPos - playerPos);
	float d = (distanceToPlayer / 96.0) - 0.5;
	d = clamp(d, 0, 1);
	vec4 fogColor = vec4(153.0 / 255.0, 204.0 / 255.0, 1.0, 1.0);
	
	vec4 objectColor = texture(txt, fragTexCoord);
	vec3 result = (diffuse + ambient) * objectColor.rgb;

    FragColor = (vec4(result, 1.0) * (1 - d) + fogColor * d) * vec4(fragColor, 1);
	
	if (objectColor.a == 0) 
	{
		discard;
	}
}