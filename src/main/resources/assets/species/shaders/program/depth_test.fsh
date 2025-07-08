#version 150

uniform sampler2D DiffuseSampler;
uniform float BlurAmount = 5.0;
uniform float BloomThreshold = 0.0;
uniform float BloomIntensity = 2.5;

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

vec4 extractBloom(vec2 uv) {
    vec4 color = texture(DiffuseSampler, uv);
    float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    return max(vec4(0.0), (brightness*1.5 - BloomThreshold) / (1.0 - BloomThreshold)) * color;
}

void main() {
    // Current pixel's bloom
    vec4 bloom = extractBloom(texCoord);
    
    // Box blur setup (5x5)
    const int samples = 5;
    float weight = 1.0 / float(samples * 2 - 1);
    vec4 blurredBloom = bloom * weight;

    // Horizontal pass - only on bloom values
    for (int i = 1; i < samples; ++i) {
        vec2 offset = vec2(oneTexel.x * i * BlurAmount, 0.0);
        blurredBloom += extractBloom(texCoord + offset) * weight;
        blurredBloom += extractBloom(texCoord - offset) * weight;
    }

    // Vertical pass - only on bloom values
    for (int i = 1; i < samples; ++i) {
        vec2 offset = vec2(0.0, oneTexel.y * i * BlurAmount);
        blurredBloom += extractBloom(texCoord + offset) * weight;
        blurredBloom += extractBloom(texCoord - offset) * weight;
    }

    // Composite
    fragColor = texture(DiffuseSampler, texCoord) + blurredBloom * BloomIntensity;
	//fragColor = blurredBloom;
}