package edu.eci.arsw.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que calcula los dígitos de pi en hexadecimal.
 */
public class PiDigits {

    private static int DigitsPerSum = 8;
    private static double Epsilon = 1e-17;

    /**
     * Returns a range of hexadecimal digits of pi using multiple threads.
     *
     * @param start    The starting location of the range.
     * @param count    The number of digits to return.
     * @param nThreads The number of threads to use for parallel computation.
     * @return An array containing the hexadecimal digits.
     */
    public static byte[] getDigits(int start, int count, int nThreads) throws InterruptedException {
        if (start < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        if (count < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        byte[] digits = new byte[count];

        // Dividir los hilos
        int rangePerThread = count / nThreads;
        List<PiCalculatorThread> threads = new ArrayList<>();

        for (int i = 0; i < nThreads; i++) {
            int startRange = start + i * rangePerThread;
            int rangeCount = (i == nThreads - 1) ? count - i * rangePerThread : rangePerThread;
            PiCalculatorThread thread = new PiCalculatorThread(startRange, rangeCount);
            threads.add(thread);
            thread.start();
        }

        // Wait and Join
        for (PiCalculatorThread thread : threads) {
            thread.join();
        }

        // Combinar hilos
        int offset = 0;
        for (PiCalculatorThread thread : threads) {
            byte[] partialResult = thread.getResult();
            System.arraycopy(partialResult, 0, digits, offset, partialResult.length);
            offset += partialResult.length;
        }

        return digits;
    }

    /// <summary>
    /// Returns the sum of 16^(n - k)/(8 * k + m) from 0 to k.
    /// </summary>
    /// <param name="m"></param>
    /// <param name="n"></param>
    /// <returns></returns>
    private static double sum(int m, int n) {
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

    /// <summary>
    /// Return 16^p mod m.
    /// </summary>
    /// <param name="p"></param>
    /// <param name="m"></param>
    /// <returns></returns>
    private static int hexExponentModulo(int p, int m) {
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

    public static byte[] getDigits(int start, int count) {
        return null;
    }

    static class PiCalculatorThread extends Thread {

        private int start;
        private int count;
        private byte[] result;
        private boolean paused;

        public PiCalculatorThread(int start, int count) {
            this.start = start;
            this.count = count;
            this.result = new byte[count];
            this.paused = false;
        }

        public byte[] getResult() {
            return result;
        }

        public synchronized void pauseThread() {
            paused = true;
        }

        public synchronized void resumeThread() {
            paused = false;
            notify();
        }

        private synchronized void checkPaused() throws InterruptedException {
            while (paused) {
                wait();
            }
        }

        @Override
        public void run() {
            try {
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
                    result[i] = (byte) sum;

                    // Detenerse cada 5 segundos
                    if (i % 5000 == 0) {
                        System.out.println("Hilo " + this.getId() + " ha procesado: " + i + " dígitos.");
                        checkPaused();
                    }

                    // Simulación retraso
                    Thread.sleep(1);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}