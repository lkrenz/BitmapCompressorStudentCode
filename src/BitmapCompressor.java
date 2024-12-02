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


import java.util.ArrayList;

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
        Boolean[] data = readData();

        int localMax = 0;
        int length = 0;
        // Finds the starting bit
        boolean currentBit = data[0];
//
//        int sequenceLength = 0;
//        // Finds the length of the longest sequence of 0s or 1s
//        for (int i = 0; i < data.length; i++) {
//            // If the bit is the current bit, the sequence is continued
//            if (data[i] == currentBit) {
//                localMax++;
//            }
//            else {
//                // If the bits don't match, the sequence is ended
//                if (localMax > max) {
//                    max = localMax;
//                }
//                localMax = 1;
//                currentBit = !currentBit;
//                length++;
//            }
//        }

        int integerLength = 8;
//        integerLength = findOptimal(data, integerLength) -1;

        // Meta data
        // Start bit
        BinaryStdOut.write(data[0]);

        // Length used to store integers
        BinaryStdOut.write(integerLength);

        // Number of integer sequences to read in
        //BinaryStdOut.write(length + 1);

        // Find maximum value representable by integerLength
        int maxVal = 2;
        for (int i = 1; i < integerLength; i++) {
            maxVal *= 2;
        }

        currentBit = data[0];
        localMax = 0;

        // Write main data in by adding lengths of sequences
        for (int i = 0; i < data.length - 1; i++) {

            // End of sequence
            if (data[i] != currentBit) {
                BinaryStdOut.write(localMax, integerLength);
                currentBit = !currentBit;
                localMax = 1;
            }
            else if (localMax >= maxVal - 1) {
                // Reached maximum value representable by integerLength
                BinaryStdOut.write(maxVal-1, integerLength);
                BinaryStdOut.write(0, integerLength);
                localMax = 1;
            }
            else {
                // Sequence continues
                localMax++;
            }
        }

        // Writes the last sequence in
        BinaryStdOut.write(localMax + 1, integerLength);
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {

        // Reads in metadata from the file
        boolean currentIsZero = BinaryStdIn.readBoolean();
        int integerLength = BinaryStdIn.readInt();

//        // Iterates through the sequences, using the number of bits per integer to get the length
//        for (int i = 0; i < fileLength; i++) {
//            int numCharacters = BinaryStdIn.readInt(integerLength);
//
//            // Adds the number of bits represented by the integer length
//            for (int j = 0; j < numCharacters; j++) {
//                BinaryStdOut.write(currentIsZero);
//            }
//            currentIsZero = !currentIsZero;
//        }
//        BinaryStdOut.close();


        // Use try and accept blocks to solve problems of bit alignment and including file length
        while (true) {
            try {
                int numCharacters = BinaryStdIn.readInt(integerLength);

                // Adds the number of bits represented by the integer length
                for (int j = 0; j < numCharacters; j++) {
                    BinaryStdOut.write(currentIsZero);
                }
                currentIsZero = !currentIsZero;
            } catch (Exception e) {
                break;
            }
        }
        BinaryStdOut.close();
    }

    // Finds the number of bits needed to represent an integer num
    public static int findNumBits(int num) {
        int count = 0;
        while (num > 0) {
            num /= 2;
            count++;
        }
        count += 1;
        return count;
    }

    // Reads in data into a Boolean[] array
    public static Boolean[] readData() {
        ArrayList<Boolean> list = new ArrayList<>();
        while (!BinaryStdIn.isEmpty()) {
            list.add(BinaryStdIn.readBoolean());
        }
        return list.toArray(new Boolean[0]);
    }

    // Determines length of data when compressed using a certain integerLength
    public static int findCompressLength (Boolean[] data, int integerLength) {

        int maxLength = 2;

        for (int i = 1; i < integerLength; i++) {
            maxLength *= 2;
        }

        ArrayList<Boolean> arr = new ArrayList<>();
        // Meta data
        // Start bit
        arr.add(data[0]);

        // Length used to store integers
        for (int i = 0; i < 64; i++) {
            arr.add(false);
        }

        int localMax = 0;
        boolean currentBit = data[0];


        for (int i = 0; i < data.length - 1; i++) {

            if (data[i] != currentBit) {
                for (int j = 0; j < integerLength; j++) {
                    arr.add(false);
                }
                currentBit = !currentBit;
            }
            else if (localMax > maxLength) {
                localMax = 1;
                for (int j = 0; j < integerLength * 2; j++) {
                    arr.add(false);
                }
            }
            else {
                localMax++;
            }
        }
        for (int i = 0; i < integerLength; i++) {
            arr.add(false);
        }

        return arr.size();
    }

    // Iterates through possible integerLengths to find optimal
    public static int findOptimal(Boolean[] data, int initial) {

        int initialLength = findCompressLength(data, initial);

        int currentLength = initial - 1;
        int newLength = findCompressLength(data, currentLength);
        if (initialLength < newLength) {
            return initial;
        }

        while(newLength < currentLength) {
            newLength = findCompressLength(data, currentLength);
        }

        return currentLength;
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