package com.zf;

import java.util.HashMap;
import java.util.Map;

/**
 * A brain teaser quiz that evaluates numbers to its maximum obtainable number in digits
 */
public class Teaser {

    private final static Map<Integer, Integer> encodings = new HashMap<Integer, Integer>();

    static {
        encodings.put(0, 0x7E);
        encodings.put(1, 0x30);
        encodings.put(2, 0x6D);
        encodings.put(3, 0x79);
        encodings.put(4, 0x33);
        encodings.put(5, 0x5B);
        encodings.put(6, 0x5F);
        encodings.put(7, 0x70);
        encodings.put(8, 0x7F);
        encodings.put(9, 0x7B);
    }

    private int require;
    private int remove;
    private int count, replaced = 0;

    public Teaser(int count) {
        this.count = count;
    }

    /**
     * Matches digit to check if such digit is valid
     *
     * @param binaryDigit the digit to match
     * @return returns array of inputted match in hex, the require digit to complete a maximum character, the number of digits that is to be removed
     */
    private static Integer[] matchDigit(String binaryDigit) {
        Integer shiftedDigit = 0x00;
        Integer carry = 0x00;
        Integer require = 0x00;
        switch (binaryDigit) {
            case "1111110":    //0
                shiftedDigit = 0x1111110;
                carry = 1;
                require = 1;
                break;
            case "0110000": //1
                shiftedDigit = 0x0110000;
                carry = 0;
                require = 4;
                break;
            case "1101101": //2
                shiftedDigit = 0x1111101;
                carry = 1;
                require = 2;
                break;
            case "1111001": //3
                shiftedDigit = 0x1111001;
                require = 1;
                carry = 0;
                break;
            case "0110011": //4
                shiftedDigit = 0x1111011;
                carry = 0;
                require = 2;
                break;
            case "1011011": //5
                shiftedDigit = 0x1111011;
                carry = 0;
                require = 1;
                break;
            case "1011111": //6
                shiftedDigit = 0x1011111;
                carry = 1;
                require = 1;
                break;
            case "1110000": //7
                shiftedDigit = 0x1110000;
                carry = 0;
                require = 3;
                break;
            case "1111111": //8
                shiftedDigit = 0x1111111;
                carry = 1;
                require = 0;
                break;
            case "1111011": //9
                shiftedDigit = 0x1111011;
                carry = 0;
                require = 0;
                break;
            default:
                carry = 0;
                require = 0;
                shiftedDigit = 0x0;

        }
        return new Integer[]{shiftedDigit, carry, require};
    }

    public static void main(String args[]) {
        Teaser t = new Teaser(2);
        String input = "5008";
        t.findLargestDigits(input);
    }

    private char[] getBinaryDigits(int digit) {
        int code = encode(digit);
        String toBinaryString = Integer.toBinaryString(code);
        return String.format("%7s", toBinaryString)
                .replace(' ', '0').toCharArray();
    }

    /**
     * Write in seven segment display the result of the inputed character array
     *
     * @param bits the array character
     */
    public void printDigit(char[] bits) {
        lightSegment(bits[0] == '1', " _ \n", "   \n");
        lightSegment(bits[5] == '1', "|", " ");
        lightSegment(bits[6] == '1', "_", " ");
        lightSegment(bits[1] == '1', "|\n", " \n");
        lightSegment(bits[4] == '1', "|", " ");
        lightSegment(bits[3] == '1', "_", " ");
        lightSegment(bits[2] == '1', "|\n", " \n");
    }

    /**
     * Displays in seven segment
     *
     * @param on       check for on
     * @param onValue  1
     * @param offValue 0
     */
    private void lightSegment(boolean on, String onValue, String offValue) {
        System.out.print(on ? onValue : offValue);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encode decimal to binary
     *
     * @param digit
     * @return
     */
    private int encode(int digit) {
        return encodings.getOrDefault(digit, 0x00);
    }

    /**
     * Decodes binary to decimal
     *
     * @param digit
     * @return
     */
    private Integer decode(int digit) {
        Integer decoded = 0x00;
        for (Map.Entry<Integer, Integer> mapEntry : encodings.entrySet()) {
            Integer mapEntryValue = mapEntry.getValue();
            if (mapEntryValue.equals(digit)) {
                decoded = mapEntry.getKey();
                break;
            }
        }
        return decoded;
    }

    /**
     * Manipulates the binary and rearrange the position and value
     *
     * @param binaryDigits    the bit array of the single digit of the inputted number
     * @param digitSingleChar single digit from the @binaryDigits array
     * @param position        index of the @digitSingleChar from the array
     */
    public char[] digitSelector(final char binaryDigits[], char digitSingleChar, int position) {
        if (digitSingleChar == '0' && position != 4 && require != 0) {
            binaryDigits[position] = '1';
            this.require--;
            this.count--;
            replaced++;
        } else if (digitSingleChar == '1' && position == 4 && replaced != 0) {
            binaryDigits[position] = '0';
            this.remove--;
            replaced--;
        }
        return binaryDigits;
    }

    /**
     * Checks through for a maximum number that can be formed by a char []
     *
     * @param input string containing char[]
     */
    public void findLargestDigits(String input) {
        String[] split = input.split("");
        char[] selector = new char[0];
        for (int j = 0; j < split.length; j++) {
            char[] encode = getBinaryDigits(new Integer(split[j]));
            Integer[] integers = matchDigit(new String(encode));
            if (count > 0) {
                require += integers[2];
                remove += integers[1];
                //count--;
            } else {
                require = 0;
                remove = 0;
            }
            for (int i = 0; i < encode.length; i++) {
                char c = encode[i];
                selector = digitSelector(encode, c, i);

            }
            Integer[] matchedDigit = matchDigit(new String(selector));
            Integer integer = matchedDigit[0];
            if (integer == 0x0) {
                //replaced++; //reassign replaced to next digit
                int replaced = integers[2];
                for (int k = 0; k < selector.length; k++) {
                    if (selector[k] == '0' && replaced != 0) {
                        selector[k] = '1';
                        replaced--;
                    }
                }
                //selector[i] = '1'; // initial passed digit
            }
            System.out.println(encode);
            printDigit(selector);
//
//            char [] selector;
//            for (int i = 0; i < encode.length; i++) {
//                char c = encode[i];
//                selector = t.digitSelector(encode, c, i);
//                t.printDigit(selector);
//            }
        }


    }


}
