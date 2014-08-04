package com.troop.freecam.utils;

/**
 * Created by George on 6/10/14.
 */
public class Demosaic {

    public static short extractBits(final short n, final int n2, final int n3) {
        return (short)(0xFFFF & ((n & 0xFFFF) >>> n2 & -1 + (short)(1 << n3)));
    }

    public static byte[] dragonRaw(final byte[] array)
    {
        final long currentTimeMillis = System.currentTimeMillis();
        final  int x = 4208;
        final  int y = 3120;
        int n = (x + 4) /6;


        final byte[] array2 = new byte[2 * (x * y)];
        int n2 = 0;
        final byte[] array3 = new byte[2 * (x + 10)];
        int n3 = 0;
        while (true) {
            int n4;
            if (x % 6 == 0) {
                n4 = x;
            }
            else {
                n4 = x + 4;
            }
            if (n3 >= n4 * y / (n * 6)) {
                break;
            }
            int n5 = 0;
            for (int i = n3; i < n3 + n; ++i) {
                final short n6 = (short)(0xFFFF & ((0xFF & array[n2 + 1]) << 8 | (0xFF & array[n2 + 0])));
                final short bits = extractBits(n6, 0, 10);
                array3[n5] = (byte)(bits & 0xFF);
                array3[n5 + 1] = (byte)(0xFF & (0xFFFF & bits) >>> 8);
                final short n7 = (short)((0xFFFF & n6) >>> 10);
                final int n8 = n5 + 2;
                final short n9 = (short)(0xFFFF & ((0xFF & array[n2 + 2]) << 6 | (0xFFFF & n7)));
                final short bits2 = extractBits(n9, 0, 10);
                array3[n8] = (byte)(bits2 & 0xFF);
                array3[n8 + 1] = (byte)(0xFF & (0xFFFF & bits2) >>> 8);
                final short n10 = (short)((0xFFFF & n9) >>> 10);
                final int n11 = n8 + 2;
                final short n12 = (short)(0xFFFF & ((0xFF & array[n2 + 3]) << 4 | (0xFFFF & n10)));
                final short bits3 = extractBits(n12, 0, 10);
                array3[n11] = (byte)(bits3 & 0xFF);
                array3[n11 + 1] = (byte)(0xFF & (0xFFFF & bits3) >>> 8);
                final short n13 = (short)((0xFFFF & n12) >>> 10);
                final int n14 = n11 + 2;
                final short n15 = (short)(0xFFFF & ((0xFF & array[n2 + 4]) << 2 | (0xFFFF & n13)));
                final short bits4 = extractBits(n15, 0, 10);
                array3[n14] = (byte)(bits4 & 0xFF);
                array3[n14 + 1] = (byte)(0xFF & (0xFFFF & bits4) >>> 8);
                final short n16 = (short)((0xFFFF & n15) >>> 10);
                final int n17 = n14 + 2;
                final short n18 = (short)(0xFFFF & ((0xFF & array[n2 + 6]) << 8 | (0xFF & array[n2 + 5])));
                final short bits5 = extractBits(n18, 0, 10);
                array3[n17] = (byte)(bits5 & 0xFF);
                array3[n17 + 1] = (byte)(0xFF & (0xFFFF & bits5) >>> 8);
                final short n19 = (short)((0xFFFF & n18) >>> 10);
                final int n20 = n17 + 2;
                final short n21 = (short)(0xFFFF & ((0xFF & array[n2 + 7]) << 6 | (0xFFFF & n19)));
                final short bits6 = extractBits(n21, 0, 10);
                array3[n20] = (byte)(bits6 & 0xFF);
                array3[n20 + 1] = (byte)(0xFF & (0xFFFF & bits6) >>> 8);
                final short n22 = (short)((0xFFFF & n21) >>> 10);
                n5 = n20 + 2;
                n2 += 8;
            }

            System.arraycopy(array3, 0, array2, 2 * (n3 * x), x * 2);
            ++n3;
        }
        return array2;
    }
}
