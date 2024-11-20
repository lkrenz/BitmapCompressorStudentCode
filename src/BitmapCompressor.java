/******************************************************************************
 *  Compilation:  javac BitmapCompressor.java
 *  Execution:    java BitmapCompressor - < input.bin   (compress)
 *  Execution:    java BitmapCompressor + < input.bin   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   q32x48.bin
 *                q64x96.bin
 *                mystery.bin
 *
 *  Compress or expand binary input from standard input.
 *
 *  % java DumpBinary 0 < mystery.bin
 *  8000 bits
 *
 *  % java BitmapCompressor - < mystery.bin | java DumpBinary 0
 *  1240 bits
 ******************************************************************************/


/**
 *  The {@code BitmapCompressor} class provides static methods for compressing
 *  and expanding a binary bitmap input.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 *  @author YOUR NAME HERE
 */
public class BitmapCompressor {

    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress() {
//
        // TODO: complete compress()
        String binaryData = "";
        while (!BinaryStdIn.isEmpty()) {
            boolean data = BinaryStdIn.readBoolean();
            if (data) {
                binaryData += "1";
            }
            else {
                binaryData += "0";
            }
        }

        int max = 0;
        int localMax = 0;
        int length = 0;
        boolean currentIsZero = !(binaryData.charAt(0) == '0');
        for (int i = 0; i < binaryData.length(); i++) {
            if ((binaryData.charAt(i) == '0')  == currentIsZero) {
                localMax++;
            }
            else {
                if (localMax > max) {
                    max = localMax;
                }
                localMax = 1;
                currentIsZero = !currentIsZero;
                length++;
            }
        }
        int integerLength = findNumBits(max) + 1;

        localMax = 0;
        currentIsZero = (binaryData.charAt(0) == '0');
        // Meta data
        // Start bit
        BinaryStdOut.write(currentIsZero);

        // Length used to store integers
        BinaryStdOut.write(integerLength, 32);

        // Number of integer sequences to read in
        BinaryStdOut.write(length, 32);

        // Write main data in
        for (int i = 0; i < binaryData.length() - 1; i++) {
            if ((binaryData.charAt(i) == '0')  == currentIsZero) {
                localMax++;
            }
            else {
                BinaryStdOut.write(localMax, integerLength);
                currentIsZero = !currentIsZero;
                localMax = 1;
            }
        }
        BinaryStdOut.write(localMax + 1, integerLength);
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {

        // TODO: complete expand()
        boolean currentIsZero = !BinaryStdIn.readBoolean();
        int integerLength = BinaryStdIn.readInt();
        int fileLength = BinaryStdIn.readInt();

        for (int i = 0; i < fileLength; i++) {
            int numCharacters = BinaryStdIn.readInt(integerLength);
            for (int j = 0; j < numCharacters; j++) {
                BinaryStdOut.write(currentIsZero);
            }
            currentIsZero = !currentIsZero;
        }
        BinaryStdOut.close();
    }

    public static int findNumBits(int num) {
        int count = 0;
        while (num > 0) {
            num /= 2;
            count++;
        }
        count += 1;
        return count;
    }

    /**
     * When executed at the command-line, run {@code compress()} if the command-line
     * argument is "-" and {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}