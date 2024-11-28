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

//        // Reads in the data into a strings
//        String binaryData = "";
//        while (!BinaryStdIn.isEmpty()) {
//
//            // Converts the read true / false into a bit
//            boolean data = BinaryStdIn.readBoolean();
//            if (data) {
//                binaryData += "1";
//            }
//            else {
//                binaryData += "0";
//            }
//        }
//
//        int max = 0;
//        int localMax = 0;
//        int length = 0;
//        // Finds the starting bit
//        boolean currentIsZero = !(binaryData.charAt(0) == '0');
//
//        // Finds the length of the longest sequence of 0s or 1s
//        for (int i = 0; i < binaryData.length(); i++) {
//
//            // If the bit is the current bit, the sequence is continued
//            if ((binaryData.charAt(i) == '0')  == currentIsZero) {
//                localMax++;
//            }
//            else {
//
//                // If the bits don't match, the sequence is ended
//                if (localMax > max) {
//                    max = localMax;
//                }
//                localMax = 1;
//                currentIsZero = !currentIsZero;
//                length++;
//            }
//        }
//
//        // Finds the number of bits needed to represent the sequences
//        int integerLength = findNumBits(max);
//
//        // Resets the bit to the starting bit
//        currentIsZero = (binaryData.charAt(0) == '0');


//        // Meta data
//        // Start bit
//        BinaryStdOut.write(currentIsZero);
//
//        // Length used to store integers
//        BinaryStdOut.write(integerLength, 32);
//
//        // Number of integer sequences to read in
//        BinaryStdOut.write(length, 32);
//
//        // Write main data in by adding lengths of sequences
//        for (int i = 0; i < binaryData.length() - 1; i++) {
//
//            // Sequence continues
//            if ((binaryData.charAt(i) == '0')  == currentIsZero) {
//                localMax++;
//            }
//            else {
//
//                // Sequence ends so data is added
//                BinaryStdOut.write(localMax, integerLength);
//                currentIsZero = !currentIsZero;
//                localMax = 1;
//            }
//        }
//
//        // Writes the last sequence in
//        BinaryStdOut.write(localMax + 1, integerLength);
//        BinaryStdOut.close();


        Boolean[] data = readData();

        int max = 0;
        int localMax = 0;
        int length = 0;
        // Finds the starting bit
        boolean currentBit = data[0];


        int sequenceLength = 0;
        // Finds the length of the longest sequence of 0s or 1s
        for (int i = 0; i < data.length; i++) {
            // If the bit is the current bit, the sequence is continued
            if (data[i] == currentBit) {
                localMax++;
            }
            else {
                // If the bits don't match, the sequence is ended
                if (localMax > max) {
                    max = localMax;
                }
                localMax = 1;
                currentBit = !currentBit;
                length++;
            }
        }

        int integerLength = findNumBits(length);
//        integerLength = findOptimal(data, integerLength);

        // Meta data
        // Start bit
        BinaryStdOut.write(data[0]);

        // Length used to store integers
        BinaryStdOut.write(integerLength);

        // Number of integer sequences to read in
        BinaryStdOut.write(length + 1);


        int maxVal = 2;
        for (int i = 1; i < integerLength; i++) {
            maxVal *= 2;
        }

        currentBit = data[0];
        localMax=0;
        // Write main data in by adding lengths of sequences
        for (int i = 0; i < data.length - 1; i++) {
            if (data[i] != currentBit) {
                BinaryStdOut.write(localMax, integerLength);
                currentBit = !currentBit;
                localMax = 1;
            }
            else if (localMax >= maxVal - 1) {
                BinaryStdOut.write(maxVal-1, integerLength);
                BinaryStdOut.write(0, integerLength);
                localMax = 1;
            }
            else {
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
        int fileLength = BinaryStdIn.readInt();

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

    public static Boolean[] readData() {
        ArrayList<Boolean> list = new ArrayList<>();
        while (!BinaryStdIn.isEmpty()) {
            list.add(BinaryStdIn.readBoolean());
        }
        return list.toArray(new Boolean[0]);
    }

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
        BinaryStdOut.write(integerLength, 32);
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

    public static int findOptimal(Boolean[] data, int initial) {

        int initialLength = findCompressLength(data, initial);

        int currentLength = initial - 1;
        int newLength = findCompressLength(data, currentLength);
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