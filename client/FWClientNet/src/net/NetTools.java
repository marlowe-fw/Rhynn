/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net;

/**
 *
 * @author marlowe
 */
public class NetTools {
    
    /**
     * Converts 2 Bytes to their corresponding integer value.
     * @param b1 The first byte (sign byte + 7 high order bits)
     * @param b2 The second byte (8 low order bits)
     * @return The integer value
     */
    public static int intFrom2Bytes(byte b1, byte b2) {
        int i = ((b1&127) << 8) | (b2&255);
        if ((b1 & 128) == 128) {
            i -= 32768;
        }
        return i;
    }

    public static int uintFrom1Byte(byte b1) {
        return (int)(b1 & 0xFF);
    }


    /**
     * Converts 2 Bytes to their corresponding integer value.
     * @param b1 The first byte (sign byte + 7 high order bits)
     * @param b2 The second byte
     * @param b3 The third byte
     * @param b4 The fourth byte
     * @return The integer value
     */

    public static int intFrom4Bytes(byte b1, byte b2, byte b3, byte b4) {
        int i = ((b1&127) << 24) | ((b2&255) << 16) | ((b3&255) << 8) | (b4&255);
        // never assume int is 4 bytes long
        if ((b1 & 128) == 128) {
            i = i + (-2147483648);
        }
        return i;
    }

    public static int intFrom3Bytes(byte b1, byte b2, byte b3) {
        int i = ((b1&127) << 16) | ((b2&255) << 8) | (b3&255);
        // never assume int is 4 bytes long
        if ((b1 & 128) == 128) {
            i = i + (-8388608);
        }
        return i;
    }



    /**
     * Converts a short to its equivalent representation splitted into two
     * bytes, the resulting two bytes will be placed in the byte array provided
     * as a prameter.
     * @param s The short value
     * @param bytes The byte array to put the two byte values to
     * @param startIndex The index in the bytes array to use for the first byte,
     * the second byte that will be set is at startIndex + 1
     */
    public static void shortTo2Bytes(short s, byte[] bytes, int startIndex) {
        bytes[startIndex] = (byte)((s >> 8) & 127);
        bytes[startIndex + 1] = (byte)(s & 255);
        if (s<0) {
            bytes[startIndex] = (byte)(bytes[startIndex] | 128);
        }
    }



    /**
     * Converts an integer to its equivalent representation splitted into two
     * bytes, the resulting two bytes will be placed in the byte array provided
     * as a prameter.
     * @param i The integer value
     * @param bytes The byte array to put the two byte values to
     * @param startIndex The index in the bytes array to use for the first byte,
     * the second byte that will be set is at startIndex + 1
     */
    public static void intTo2Bytes(int i, byte[] bytes, int startIndex) {
        bytes[startIndex] = (byte)((i >> 8) & 127);
        bytes[startIndex + 1] = (byte)(i & 255);

        if (i<0) {
            bytes[startIndex] = (byte)(bytes[startIndex] | 128);
        }
    }



    /**
     * Converts an integer to its equivalent representation splitted into four
     * bytes, the resulting four bytes will be placed in the byte array provided
     * as a prameter.
     * @param i The integer value
     * @param bytes The byte array to put the two byte values to
     * @param startIndex The index in the bytes array to use for the first byte,
     * the other three bytes that will be set are at startIndex + 1,
     * startIndex + 2 and startIndex + 3
     */
    public static void intTo4Bytes(int i, byte[] bytes, int startIndex) {
        bytes[startIndex] = (byte)((i >> 24) & 127);
        bytes[startIndex + 1] = (byte)((i >> 16) & 255);
        bytes[startIndex + 2] = (byte)((i >> 8) & 255);
        bytes[startIndex + 3] = (byte)(i & 255);
        if (i<0) {
            bytes[startIndex] = (byte)(bytes[startIndex] | 128);
        }
    }


    public static void intTo3Bytes(int i, byte[] bytes, int startIndex) {
        bytes[startIndex] = (byte)((i >> 16) & 127);
        bytes[startIndex + 1] = (byte)((i >> 8) & 255);
        bytes[startIndex + 2] = (byte)(i & 255);
        if (i<0) {
            bytes[startIndex] = (byte)(bytes[startIndex] | 128);
        }
    }

    public static void intToUnsignedByte(int i, byte[] bytes, int startIndex) {
        bytes[startIndex] = (byte)(i & 255);
    }
    
}
