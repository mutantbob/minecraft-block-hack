    precision mediump float;

    uniform sampler2D tex0;

    varying vec2 v_texCoord;
    varying vec4 v_tint;

    void main()
    {
        vec2 texCoord = vec2(v_texCoord.s, 1.0 - v_texCoord.t);
        gl_FragColor = texture2D(tex0, texCoord)*v_tint;
    }
