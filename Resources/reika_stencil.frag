uniform float distance;
uniform float age;

void main() {
    vec2 focusXY = getScreenPos(0.0, 0.0, 0.0);
	
	float distv = distsq(focusXY, texcoord);
	float distfac_vertex = max(0.0, min(1.0, 2.0-15.0*distv*distance));
	float vf = intensity*distfac_vertex;
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float gs = max(color.a, vf);
	float new = (color.a-gs)/(color.a-vf);
	float xy = mix(vec2(color.g, color.b), focusXY, new);
	
    gl_FragColor = vec4(age, xy.x, xy.y, gs);
}