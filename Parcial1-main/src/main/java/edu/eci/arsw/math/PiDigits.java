package edu.eci.arsw.math;

public class PiDigits {

    public static int DigitsPerSum = 8;
    public static double Epsilon = 1e-17;

    public static byte[] getDigits(int start, int count) {
        if (start < 0 || count < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        byte[] digits = new byte[count];
        double sum = 0;

        for (int i = 0; i < count; i++) {
            if (i % DigitsPerSum == 0) {
                sum = 4 * sum(1, start)
                        - 2 * sum(4, start)
                        - sum(5, start)
                        - sum(6, start);
                start += DigitsPerSum;
            }

            sum = 16 * (sum - Math.floor(sum));
            digits[i] = (byte) sum;
        }

        return digits;
    }

    public static byte[] getDigits(int start, int count, int numThreads) {
        byte[] digits = new byte[count];
        int digitsPerThread = count / numThreads;

        PiDigitThread[] threads = new PiDigitThread[numThreads];
        int currentStart = start;

        for (int i = 0; i < numThreads; i++) {
            int threadCount = (i == numThreads - 1) ? (count - i * digitsPerThread) : digitsPerThread;
            threads[i] = new PiDigitThread(currentStart, threadCount, i);
            threads[i].start();
            currentStart += threadCount;
        }

        try {
            for (int i = 0; i < numThreads; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int currentIndex = 0;
        for (PiDigitThread thread : threads) {
            byte[] threadDigits = thread.getDigits();
            System.arraycopy(threadDigits, 0, digits, currentIndex, threadDigits.length);
            currentIndex += threadDigits.length;
        }

        return digits;
    }

    public static double sum(int m, int n) {
        double sum = 0;
        int d = m;
        int power = n;

        while (true) {
            double term;

            if (power > 0) {
                term = (double) hexExponentModulo(power, d) / d;
            } else {
                term = Math.pow(16, power) / d;
                if (term < Epsilon) {
                    break;
                }
            }

            sum += term;
            power--;
            d += 8;
        }

        return sum;
    }

    public static int hexExponentModulo(int p, int m) {
        int power = 1;
        while (power * 2 <= p) {
            power *= 2;
        }

        int result = 1;

        while (power > 0) {
            if (p >= power) {
                result *= 16;
                result %= m;
                p -= power;
            }

            power /= 2;

            if (power > 0) {
                result *= result;
                result %= m;
            }
        }

        return result;
    }
}
