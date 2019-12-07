uniform float distance;
uniform int stencilTex;

void main() {
    vec2 focusXY = getScreenPos(0.0, 0.0, 0.0);
	
	vec4 stencil = texture2D(stencilTex, texcoord);
	float factor = stencil.r;
	
	texcoord = mix(texcoord, focusXY, factor/6.0);
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float f = 1.0+0.25*factor;
	float d = cf/4.0;
	color.r = min(1.0, color.r*f+d);
	color.g = min(1.0, color.g*f+d);
	color.b = min(1.0, color.b*f+d);	
	
    gl_FragColor = vec4(color.r, color.g, color.b, color.a);
}