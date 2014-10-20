#version 110

precision mediump float;

uniform vec4 u_tint;

uniform sampler2D tex0;

varying vec2 v_texCoord;

void main()
{
    vec2 texCoord = vec2(v_texCoord.s, v_texCoord.t);
    gl_FragColor = texture2D(tex0, texCoord)*u_tint;
}
