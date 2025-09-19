package edu.eci.arsw.math;

public class PiDigitThread extends Thread {
    private int start;
    private int count;
    private byte[] digits;
    private int threadId;
    private static final Object LOCK = new Object();

    public PiDigitThread(int start, int count, int threadId) {
        this.start = start;
        this.count = count;
        this.threadId = threadId;
        this.digits = new byte[count];
    }

    @Override
    public void run() {
        double sum = 0;
        int digitsProcessed = 0;

        for (int i = 0; i < count; i++) {
            if (i % PiDigits.DigitsPerSum == 0) {
                sum = 4 * PiDigits.sum(1, start)
                        - 2 * PiDigits.sum(4, start)
                        - PiDigits.sum(5, start)
                        - PiDigits.sum(6, start);
                start += PiDigits.DigitsPerSum;
            }

            sum = 16 * (sum - Math.floor(sum));
            digits[i] = (byte) sum;
            digitsProcessed++;

            if (digitsProcessed % 100 == 0) {
                synchronized (LOCK) {
                    try {
                        System.out.println("Thread " + threadId + " processed " + digitsProcessed + " digits. Press Enter to continue.");
                        System.in.read();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public byte[] getDigits() {
        return digits;
    }
}

