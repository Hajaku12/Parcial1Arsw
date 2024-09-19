package edu.eci.arsw.math;

public class PiCalculatorThread extends Thread {
    private int start, count;
    private byte[] result;
    private boolean paused = false;

    public PiCalculatorThread(int start, int count) {
        this.start = start;
        this.count = count;
        this.result = new byte[count];
    }

    @Override
    public void run() {
        result = PiDigits.getDigits(start, count);
    }

    public byte[] getResult() {
        return result;
    }

    // Método para pausar el hilo
    public synchronized void pauseThread() {
        paused = true;
    }

    // Método para reanudar el hilo
    public synchronized void resumeThread() {
        paused = false;
        notify();
    }

    // Progreso actual del hilo
    public int getProgress() {
        return result.length;
    }
}