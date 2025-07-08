#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    vec2 centered = texCoord - 0.5;
    float dist = length(centered);

    // Original color sample
    vec4 c = texture(DiffuseSampler, texCoord);
    float gray = dot(c.rgb, vec3(0.3, 0.59, 0.11));

    // Base brass tint for whole screen (replaces sepia)
    vec3 brassBase = vec3(0.718, 0.49, 0.357) * gray;

    // Gear distortion (subtle at center, wider at edge)
    float gearPulse = sin(Time * 3.0 + dist * 20.0) * 0.002;
    vec2 offset = centered * gearPulse;
    vec4 distorted = texture(DiffuseSampler, texCoord + offset);

    // Chromatic aberration
    float shift = 0.0015;
    vec3 chroma;
    chroma.r = texture(DiffuseSampler, texCoord + vec2(shift, 0.0)).r;
    chroma.g = texture(DiffuseSampler, texCoord).g;
    chroma.b = texture(DiffuseSampler, texCoord - vec2(shift, 0.0)).b;

    // Edge vignette (0 at center, 1 at edges)
    float edgeVignette = smoothstep(0.1, 1.0, dist);

    // Enrich brass tone at the edges
    vec3 enrichedBrass = brassBase + brassBase * 0.5 * edgeVignette;

    // Blend with chromatic aberration for slight shifting color
    vec3 finalColor = mix(enrichedBrass, chroma, 0.1);

    // Edge sparkle flicker (only near edges)
    float sparkNoise = fract(sin(dot(texCoord * Time, vec2(12.9898, 78.233))) * 43758.5453);
    float sparkAmount = smoothstep(0.1, 1.0, dist) * sparkNoise;

    fragColor = vec4(finalColor + sparkAmount, 1.0);
}
