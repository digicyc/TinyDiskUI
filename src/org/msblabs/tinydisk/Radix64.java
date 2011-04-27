/*
 * Radix64
 *
 * Handles Base64 encoding and decoding. Aslo PGP/GPG Ascii armour files
 * that will be used to hide meta files. Hence the CRC24 checksum used by PGP/GPG
 * 
 */

package org.msblabs.tinydisk;


/**
 *
 * @author acidus
 */
public class Radix64 {
    

    private final static byte[] alphabet =
    {
        (byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G',
        (byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N',
        (byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U', 
        (byte)'V', (byte)'W', (byte)'X', (byte)'Y', (byte)'Z',
        (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g',
        (byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n',
        (byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u', 
        (byte)'v', (byte)'w', (byte)'x', (byte)'y', (byte)'z',
        (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', 
        (byte)'6', (byte)'7', (byte)'8', (byte)'9',
        (byte)'+', (byte)'/'
    };
 
    public static byte [] encode(byte[] in ) {
        int     iLen      = in.length;
        int     oDataLen  = ( iLen * 4 + 2 ) / 3;// output length without padding
        int     oLen      = ( ( iLen + 2 ) / 3 ) * 4;// output length including padding
        byte[]  out       = new byte[oLen];
        int     ip        = 0;
        int     op        = 0;
        int     i0;
        int     i1;
        int     i2;
        int     o0;
        int     o1;
        int     o2;
        int     o3;
        while ( ip < iLen ) {
            i0 = in[ip++] & 0xff;
            i1 = ip < iLen ? in[ip++] & 0xff : 0;
            i2 = ip < iLen ? in[ip++] & 0xff : 0;
            o0 = i0 >>> 2;
            o1 = ( ( i0 & 3 ) << 4 ) | ( i1 >>> 4 );
            o2 = ( ( i1 & 0xf ) << 2 ) | ( i2 >>> 6 );
            o3 = i2 & 0x3F;
            out[op++] = alphabet [o0];
            out[op++] = alphabet [o1];
            out[op] = op < oDataLen ? alphabet [o2] : (byte) '=';
            op++;
            out[op] = op < oDataLen ? alphabet [o3] : (byte) '=';
            op++;
      }
      return out;
   }

   //compute the CRC24 Radix checksum, and base64 it
   //ported from C code in RFC2440
   public static byte[] checksum (byte [] unencoded) {
   
        long crc = (long) 0xB704CE; //init it
        

        for(int ptr =0; ptr < unencoded.length; ptr++) {
            crc ^= (unencoded[ptr]) << 16;
	    for(int i = 0; i < 8; i++) {
                crc <<= 1;
                if ( (crc & 0x1000000) > 0)
                    crc ^= (long) 0x1864CFB; //CRC poly
            }
            
        }
        //grab the bottom 24 bits
        crc = crc & (long) 0xffffff;
        //form our bytes to base64
        byte [] in = new byte[3];
        in[0] = (byte) ((crc >>> 16) & 0xFF);
        in[1] = (byte) ((crc >>> 8) & 0xFF);
        in[2] = (byte) ((crc) & 0xFF);
        return encode(in);
    }
    
    public static byte[] decode( byte[] in)
    {
        int iLen      = in.length;
        int oLen = (iLen * 3 ) / 4;
        byte[]  out       = new byte[oLen];
        int ip = 0;
        int op = 0;

        while ( ip < iLen ) {
            //System.out.println("--- " + ip + " of " + iLen);
            
            int concat2 = (mapFrom(in[ip]) << 18) |
                          (mapFrom(in[ip + 1]) << 12);
            
            if(in[ip + 2] != '=') {
                concat2 = concat2 | (mapFrom(in[ip + 2]) << 6);
            }
            
            if(in[ip + 3] != '=') {
                concat2 = concat2 | (mapFrom(in[ip + 3]));
            }
            ip += 4;
            
            out[op] = (byte)(((concat2 >> 16) & 0xFF));
            
            //System.out.println("\'" + (char) out[op] + "\'");
            out[op + 1] = (byte)(((concat2 >> 8) & 0xFF));
            //System.out.println("\'" + (char) out[op + 1] + "\'");
            out[op + 2] = (byte)((concat2 & 0xFF));            
            //System.out.println("\'" + (char) out[op + 2] + "\'");     
            op += 3;
        }
        
        //take care of anything the padding did
        
        //check for chopping off 2 empty array elements because of ==
        if(in[in.length -2 ] == '=') {
            byte [] tmp = new byte[out.length - 2];
            System.arraycopy(out,0, tmp,0,out.length - 2);
            out = tmp;
        }
        //check for chopping off 1 empty array element because of =
        else if(in[in.length - 1 ] == '=') {
            byte [] tmp = new byte[out.length - 1];
            System.arraycopy(out,0, tmp,0,out.length - 1);
            out = tmp;            
        }
        return out;
    }

    //Get Base64 char code from ASCII code
    static int mapFrom(int val) {
        if(val == '/') {
            return 63;
        } else if(val == '+') {
            return 62;
        } else if((val >= '0') && (val <= '9')){
            return 52 + val - '0';
        } else if((val >= 'A') && (val <= 'Z')) {
            return val - 'A';
        } else if((val >= 'a') && (val <= 'z')) {
            return 26 + val - 'a';
        } else {
            System.out.println("Not a possible six-bit value");
            System.exit(0);
        }
        return -1;
    }//end mapFrom

}
