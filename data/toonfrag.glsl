varying vec3 normal;
	
void main() {
  float intensity;
  vec4 color;
  vec3 n = normalize(normal);
 
  intensity = dot(vec3(gl_LightSource[0].position),n);
		
  /*if (intensity > 0.95)
    color = vec4(0,0.04,0.225,1.0);
  else if (intensity > 0.5)
    color = vec4(0,0.03,0.2,1.0);
  else if (intensity > 0.25)
    color = vec4(0,0.02,0.175,1.0);
  else
    color = vec4(0,0.01,0.15,1.0);*/

  if (intensity > 0.95)
    color = vec4(1,1,1,1.0);
  else if (intensity > 0.5)
    color = vec4(0.975,0.975,0.975,1.0);
  else if (intensity > 0.25)
    color = vec4(0.95,0.95,0.95,1.0);
  else
    color = vec4(0.9,0.9,0.9,1.0);
		
  gl_FragColor = color;
} 

