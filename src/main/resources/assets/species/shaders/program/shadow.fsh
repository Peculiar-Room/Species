#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

float wave(vec2 p, float t) {
    return sin(p.y * 40.0 + t * 0.9) * 0.003 +
    cos(p.x * 60.0 + t * 1.3) * 0.003;
}

vec3 closestColor(vec3 color) {
    vec3 c1 = vec3(253.0, 228.0, 109.0) / 255.0; // fde46d
    vec3 c2 = vec3(117.0, 13.0, 131.0) / 255.0;  // 750d83
    vec3 c3 = vec3(231.0, 42.0, 139.0) / 255.0;  // e72a8b
    vec3 c4 = vec3(255.0, 94.0, 62.0) / 255.0;   // ff5e3e

    float d1 = distance(color, c1);
    float d2 = distance(color, c2);
    float d3 = distance(color, c3);
    float d4 = distance(color, c4);

    float minDist = min(min(d1, d2), min(d3, d4));

    if (minDist == d1) return c1;
    if (minDist == d2) return c2;
    if (minDist == d3) return c3;
    return c4;
}

void main() {
    float t = sin(Time * 6.28);

    vec2 offset = vec2(
    wave(texCoord + vec2(0.0, 0.3), t),
    wave(texCoord + vec2(0.5, 0.1), t + 10.0)
    );

    vec2 distortedCoord = texCoord + offset;

    vec4 color = texture(DiffuseSampler, distortedCoord);

    vec4 blur = (
    texture(DiffuseSampler, texCoord + oneTexel * vec2(-1, -1)) +
    texture(DiffuseSampler, texCoord + oneTexel * vec2( 1, -1)) +
    texture(DiffuseSampler, texCoord + oneTexel * vec2(-1,  1)) +
    texture(DiffuseSampler, texCoord + oneTexel * vec2( 1,  1))
    ) * 4;

    vec3 mixed = mix(color.rgb, blur.rgb, 0.25);

    vec3 magicOverlay = closestColor(mixed);
    vec3 finalColor = max(mixed, magicOverlay * 0.7);

    fragColor = vec4(finalColor, 1.0);
}
