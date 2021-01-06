#import color

uniform sampler2D stencilTex;

void main() {
	
	vec4 stencil = texture2D(stencilTex, texcoord);
	float factor = stencil.r;//a;
	//float age = stencil.r;
	vec2 focusXY = stencil.gb;
	
	factor *= intensity;
	
	vec2 texUV = mix(texcoord, focusXY, factor/4.0);
	
    vec4 color = texture2D(bgl_RenderedTexture, texUV);
	vec3 orig = color.rgb;
	
	float f = 1.0+0.05*factor;
	float f2 = 1.0+0.25*factor;
	float d = factor/8.0;
	float d2 = factor/6.0;	
	color.rgb = rgb2hsb(color.rgb);
	color.r = mix(color.r, 280.0/360.0, pow(factor, 2.0)*0.2);
	color.g = min(1.0, color.g*f2+d2);
	color.rgb = hsb2rgb(color.rgb);
	color.r = min(1.0, color.r*f+d);
	color.g = min(1.0, color.g*f+d);
	color.b = min(1.0, color.b*f+d);
	
	//float f3 = 0.5+0.5*sin(float(time)/10.0);
	//color = mix(color, vec4(stencil.r, stencil.g, stencil.b, 1.0), f3);
	
    gl_FragColor = vec4(color.r, color.g, color.b, color.a);
}