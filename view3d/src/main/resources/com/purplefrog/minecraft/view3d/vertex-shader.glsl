uniform mat4 u_modelViewProjMatrix;
uniform vec4 u_tint;

attribute vec2 vTexCoord;
attribute vec4 vPosition;


varying vec2 v_texCoord;
varying vec4 v_tint;

void main()
{
    gl_Position = u_modelViewProjMatrix * vPosition;
    v_texCoord = vTexCoord;
    v_tint = u_tint;
}
