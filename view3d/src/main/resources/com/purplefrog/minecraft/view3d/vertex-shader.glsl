#version 110

uniform vec4 u_tint;

attribute vec2 vTexCoord;

varying vec2 v_texCoord;

void main()
{
    gl_Position = ftransform();
    v_texCoord = vTexCoord;
}
