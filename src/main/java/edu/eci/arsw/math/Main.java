package edu.eci.arsw.math;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int start = 0;
        int count = 1000000;
        int nThreads = 4;

        long inicio = System.currentTimeMillis();
        byte[] piDigits = PiDigits.getDigits(start, count, nThreads);
        long fin = System.currentTimeMillis();

        System.out.println("Dígitos calculados: " + bytesToHex(piDigits));
        System.out.println("Tiempo de cálculo: " + (fin - inicio) + " ms");
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length];
        for (int j = 0; j < bytes.length; j++) {
            hexChars[j] = hexArray[bytes[j] & 0xF];
        }
        return new String(hexChars);
    }
}