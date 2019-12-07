uniform sampler2D stencilTex;

void main() {
	
	vec4 stencil = texture2D(stencilTex, texcoord);
	float factor = stencil.a;
	float age = stencil.r;
	vec2 focusXY = stencil.gb;
	
	texcoord = mix(texcoord, focusXY, factor/6.0);
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float f = 1.0+0.25*factor;
	float d = factor/4.0;
	color.r = min(1.0, color.r*f+d);
	color.g = min(1.0, color.g*f+d);
	color.b = min(1.0, color.b*f+d);	
	
	
	float f3 = 0.5+0.5*sin(float(time)/10.0);
	color = mix(color, stencil, f3);
	
    gl_FragColor = vec4(color.r, color.g, color.b, color.a);
}