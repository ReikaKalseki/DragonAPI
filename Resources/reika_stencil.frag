uniform float distance;

void main() {
    vec2 focusXY = getScreenPos(0.0, 0.0, 0.0);
	
	float distv = distsq(focusXY, texcoord);
	float distfac_vertex = max(0.0, min(1.0, 2.0-15.0*distv*distance));
	float vf = intensity*distfac_vertex;
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float gs = max(color.r, vf);
	float count = color.a;
	count++;
	float new = (gs-color.r)/(color.r-vf);
	float xy = mix(vec2(color.g, color.b), focusXY, new);
	
    gl_FragColor = vec4(gs, xy.x, xy.y, count);
}