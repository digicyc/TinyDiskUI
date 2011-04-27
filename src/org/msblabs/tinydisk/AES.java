/**
 * AES - 128 bit AES file encryptor/decryptor.
 *
 * Slimmed down from my AES stuff. Generates secure random keys
 * and does 128 bit AES encryption and decryption
 * 
 * Copyright (c) 2005, Acidus, Most Significant Bit Labs (acidus@msblabs.org)
 */
package org.msblabs.tinydisk;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;


public class AES {

    //init vector for randomness
    private static byte[] iv = { (byte)0xcb, (byte)0x53, (byte)0x03, (byte)0x0f,
                         (byte)0xe0, (byte)0x79, (byte)0x9d, (byte)0xdc,
                         (byte)0x80, (byte)0xa9, (byte)0x83, (byte)0xf1,
                         (byte)0x03, (byte)0xb6, (byte)0x59, (byte)0x83 };

//========================================= CRYPTO STUFF

    public static byte [] generateKey() {
        
        byte [] key = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        return key;
    }
                         

    public static byte [] md5sum(byte [] buffer) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(buffer);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("AES Algorithm not available on this VM");
            System.exit(1);
        }
        return null;
    }

    public static byte [] decrypt(byte [] cipherText, byte [] key) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] plainText = aes.doFinal(cipherText);
            return plainText;
        } catch(Exception e) {
            System.out.println("Decryption failed");
            e.printStackTrace();            
            System.exit(1);
        }
        return null;
    }

    public static byte [] encrypt(byte [] plainText, byte [] key) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] cipherText = aes.doFinal(plainText);
            return cipherText;
        } catch (Exception e) {
            System.out.println("Encryption failed");             
            e.printStackTrace();            
            System.exit(1);
        }
        return null;
    }

} //end class AES
