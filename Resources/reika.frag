uniform float distance;
uniform float colorIntensity;
uniform float distortionIntensity;

void main() {
    vec2 focusXY = getScreenPos(0.0, 0.0, 0.0);
	
	float distv = distsq(focusXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 1.0-10.0*distv*distance));
	float distfac_vertex = max(0.0, min(1.0, 2.0-15.0*distv*distance));
	float cf = colorIntensity*intensity*distfac_color;
	float vf = distortionIntensity*intensity*distfac_vertex;
	
	texcoord = mix(texcoord, focusXY, vf/6.0);
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float f = 1.0+0.5*cf;
	float d = cf/4.0;
	color.r = min(1.0, color.r*f+d);
	color.g = min(1.0, color.g*f+d);
	color.b = min(1.0, color.b*f+d);
	
	
    gl_FragColor = vec4(color.r, color.g, color.b, color.a);
}