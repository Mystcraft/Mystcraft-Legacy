#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

uniform float iGlobalTime;
uniform float damage;
uniform float dWave;
uniform float dColor;
uniform vec4 linkColor;
uniform vec2 iResolution;

varying vec2 texCoord;

float hash( float n )
{
    return fract( sin(n)* 43758.5453123 );
}

float noise1( float x )
{
    float p = floor(x);
    float f = fract(x);

    f = f*f*(3.0-2.0*f);

    return mix( hash(p+0.0), hash(p+1.0), f );
}

float fbm( float p )
{
    float f = 0.0;

    f += 0.5000*noise1( p ); p = p*2.02;
    f += 0.2500*noise1( p ); p = p*2.03;
    f += 0.1250*noise1( p ); p = p*2.01;
    f += 0.0625*noise1( p );

    return f/0.9375;
}

float random(float p) {
  return fract(sin(fract(tan(p)))*12345.);
}

float flick(float y, float d)
{
    if(hash(iGlobalTime)<d/50.){
        float flickLength = random(iGlobalTime+120.);
        float flickStart = random(iGlobalTime-25.);

        if(y<flickStart&&y>flickStart-(flickLength*d))
            return 0.1;
    }
    return 1.;
}

float snoise(vec3 uv, float res)
{
    const vec3 s = vec3(1e0, 1e2, 1e4);

    uv *= res;

    vec3 uv0 = floor(mod(uv, res))*s;
    vec3 uv1 = floor(mod(uv+vec3(1.), res))*s;

    vec3 f = fract(uv); f = f*f*(3.-2.*f);

    vec4 v = vec4(uv0.x+uv0.y+uv0.z, uv1.x+uv0.y+uv0.z,
                  uv0.x+uv1.y+uv0.z, uv1.x+uv1.y+uv0.z);

    vec4 r = fract(sin(v*1e-3)*1e5);
    float r0 = mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y);
    
    r = fract(sin((v + uv1.z - uv0.z)*1e-3)*1e5);
    float r1 = mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y);
    
    return mix(r0, r1, f.z)*2.-0.3*(-2.0);
}

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

vec4 damageStatic()
{
    if(damage<=0.){
        vec4 c = texture2D(u_texture,texCoord);
        c.a = 1.;
        return c;
    }

    float w = sin(iGlobalTime+texCoord.y*24.)*dWave/2.;
    vec2 uv2 = vec2(texCoord.x+w*damage,texCoord.y);

    float c = dot( vec3( fbm( texCoord.y * 5.134 + iGlobalTime * 2.013 ),
                         fbm( texCoord.y * 15.002 + iGlobalTime * 3.591 ),
                         fbm( texCoord.y * 25.922 + iGlobalTime * 4.277 ) ),
           vec3( .85, .35, .17 ) );

    float scanline = sin(texCoord.y*800.0)*0.1;
    c -= scanline;

    c*=rand(gl_FragCoord.xy+vec2(iGlobalTime));
    c/=damage;
    c= clamp(c,0.,1.);
    float c2=clamp(dColor,0.,damage);
    vec4 colorFinal = (texture2D(u_texture,uv2)*(1.-c2)+c2*linkColor)*c*flick(texCoord.y,damage);
    colorFinal.a = 1.; 
    return colorFinal;
}

void main(void) 
{
    float scale = 0.54*iGlobalTime;
    if(iGlobalTime>3.)
    {
        gl_FragColor = damageStatic();
    }
    else if(scale>0.)
    {
        vec2 p = vec2(-.45,-0.424) + texCoord;
        p/=scale;
        p*=iResolution.x/iResolution.y;

        float color = 3.3 - (3.*length(2.*p));

        vec3 coord = vec3(atan(p.x,p.y)/6.2832+.5, length(p)*.4, .5);

        scale-=0.065;
        float color2 = 0.;
        vec3 coord2;
        if(scale>0.)
        {
            p = vec2(-.67,-0.62) + texCoord;
            p/=scale;
            p*=iResolution.x/iResolution.y;
            
            color2 = 3.3 - (3.*length(2.*p));

            coord2 = vec3(atan(p.x,p.y)/6.2832+.5, length(p)*.4, .5);
        }   
        for(int i = 0; i <= 7; ++i)
        {
            float power = pow(2.0, float(i));
            color += (1.5 / power) * snoise(coord + vec3(0.,-iGlobalTime*.25, iGlobalTime*.11), power*11.);
            if(scale>0.)
                color2 += (1.5 / power) * snoise(coord2 + vec3(0.,-(iGlobalTime+12.)*.25, (iGlobalTime+12.)*.11), power*11.);
        }

        color=max(color,color2);

        vec4 color4 = linkColor*color;

        float grey = clamp((color*3.*iGlobalTime-.9)*10.,0.,1.);
        vec4 colorTexture = vec4(1.,1.,1.,1.);
        if(grey>0.)
        {
            colorTexture = damageStatic();
        }
        gl_FragColor = color4*(1.-grey)+grey*colorTexture;
        gl_FragColor.a = 1.;
    }
    else
        gl_FragColor = vec4(0.,0.,0.,1.);
}
