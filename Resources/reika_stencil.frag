#import math
#import geometry

uniform float distance;
uniform float age;

uniform float dx;
uniform float dy;
uniform float dz;

void main() {
    vec2 focusXY = getScreenPos(dx, dy, dz);
	
	float distv = distsq(focusXY, texcoord);
	float distfac_vertex = max(0.0, min(1.0, 1.5-5.0*distv*distance/intensity));
	float vf = distfac_vertex;
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float gs = max(color.r, vf);
	float new = gs == vf ? 1.0 : 0.0;
	vec2 xy = mix(vec2(color.g, color.b), focusXY, new);
	//float r = mix(color.r, age, new);
	
    gl_FragColor = vec4(gs, xy.x, xy.y, 1);
	//gl_FragColor = vec4(gs, gs, gs, 1);
}