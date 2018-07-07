package ddswriter;

import com.jme3.math.Vector4f;

import ddswriter.Texel.PixelFormat;

/**
 * Pixel
 */
public class Pixel{
    Vector4f PIXEL=new Vector4f();
    PixelFormat FORMAT;

    public Pixel(PixelFormat format,float r,float g,float b,float a){
        this(format,null);
        PIXEL.set(r,g,b,a);
    }

    public Pixel(PixelFormat format,Vector4f px){
        if(px!=null)PIXEL.set(px);
        FORMAT=format;
    }
    
    public Vector4f getRawPixel() {
        return PIXEL;
    }
    
    public float r(PixelFormat toformat) {
        return get(toformat,0);
    }

    public float g(PixelFormat toformat) {
        return get(toformat,1);
    }

    public float b(PixelFormat toformat) {
        return get(toformat,2);
    }

    public float a(PixelFormat toformat) {
        return get(toformat,3);
    }

    protected float get(PixelFormat to, int p) {
        PixelFormat from=FORMAT;
        if(from==to) return get(p);
        if(from==PixelFormat.FLOAT_NORMALIZED_RGBA){
            switch(to){
                case INT_RGBA:{
                    return get(p)*255f;
                    // Vector4f out = c.clone();
                    // out.x = (int) (c.x * 255f);
                    // out.y = (int) (c.y * 255f);
                    // out.z = (int) (c.z * 255f);
                    // out.w = (int) (c.w * 255f);
                    // return out;
                }
                case PACKED_ARGB:{
                    int x=(int)(get(0)*255f);
                    int y=(int)(get(1)*255f);
                    int z=(int)(get(2)*255f);
                    int w=(int)(get(3)*255f);

                    int packed=(int)w<<24|(int)x<<16|(int)y<<8|(int)z;

                    // Vector4f out = convert(PixelFormat.FLOAT_NORMALIZED_RGBA, PixelFormat.INT_RGBA, c);
                    // out = convert(PixelFormat.INT_RGBA, PixelFormat.PACKED_ARGB, out);
                    return packed;
                }
            }
        }else if(from==PixelFormat.INT_RGBA){
            switch(to){
                case FLOAT_NORMALIZED_RGBA:{
                    return get(p)/255f;
                }
                case PACKED_ARGB:{
                    int x=(int)(get(0));
                    int y=(int)(get(1));
                    int z=(int)(get(2));
                    int w=(int)(get(3));

                    int packed=(int)w<<24|(int)x<<16|(int)y<<8|(int)z;
                    return packed;
                    // Vector4f out = new Vector4f();
                    // int p = (int) c.w << 24 | (int) c.x << 16 | (int) c.y << 8 | (int) c.z;
                    // out.x = p;
                    // return out;
                }
            }
        }
        throw new UnsupportedOperationException();

    }

    protected float get(int p) {
        switch(p){
            case 0:
                return PIXEL.x;
            case 1:
                return PIXEL.y;
            case 2:
                return PIXEL.z;
            case 3:
                return PIXEL.w;
        }
        throw new UnsupportedOperationException();
    }

    // protected Vector4f convert(PixelFormat from, PixelFormat to, Vector4f c) {
    //     if(from==to) return c;
    //     if(from==PixelFormat.FLOAT_NORMALIZED_RGBA){
    //         switch(to){
    //             case INT_RGBA:{
    //                 Vector4f out=c.clone();
    //                 out.x=(int)(c.x*255f);
    //                 out.y=(int)(c.y*255f);
    //                 out.z=(int)(c.z*255f);
    //                 out.w=(int)(c.w*255f);
    //                 return out;
    //             }
    //             case PACKED_ARGB:{
    //                 Vector4f out=convert(PixelFormat.FLOAT_NORMALIZED_RGBA,PixelFormat.INT_RGBA,c);
    //                 out=convert(PixelFormat.INT_RGBA,PixelFormat.PACKED_ARGB,out);
    //                 return out;
    //             }
    //         }
    //     }else if(from==PixelFormat.INT_RGBA){
    //         switch(to){
    //             case FLOAT_NORMALIZED_RGBA:{
    //                 Vector4f out=c.clone();
    //                 out.x=(c.x/255f);
    //                 out.y=(c.y/255f);
    //                 out.z=(c.z/255f);
    //                 out.w=(c.w/255f);
    //                 return out;
    //             }
    //             case PACKED_ARGB:{
    //                 Vector4f out=new Vector4f();
    //                 int p=(int)c.w<<24|(int)c.x<<16|(int)c.y<<8|(int)c.z;
    //                 out.x=p;
    //                 return out;
    //             }
    //         }
    //     }
    //     return null;
    // }

    public Vector4f toVector4f(PixelFormat format) {
        return new Vector4f(r(format),g(format),b(format),a(format));
    }
    public Vector4f toVector4f(PixelFormat format,Vector4f v) {
       v.set(r(format),g(format),b(format),a(format));
        return v;
	}
}