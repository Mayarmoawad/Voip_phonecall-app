package Audio;

import org.zoolu.sound.codec.G711;

/**
 * Audio.AudioSinusoidalThread Class
 * <p>
 * This class implements runnable because it must be instantiated inside a thread.
 * Once the connection is set and the sendSinusoidal method is called, the class
 * creates a sinusoidal wave to send to the mjUA.
 */
public class AudioSinusoidalThread implements Runnable {

    private static double frequency = 200;
    private static double amplitude = 4000;

    /**
     * Set methods
     */
    public static void setFrequency(double newValue) {
        frequency = newValue;
    }

    public static void setAmplitude(double newValue) {
        amplitude = newValue;
    }

    /**
     * Create a sinusoidal wave given the Width of the wave, its frequency and
     * the time incrementation when calculating it. The method creates a new RTP Packet,
     * then start calculating the values of the wave, and for each value obtained,
     * compresses it using the G711 method for compressing bytes with the PCM algorithm
     * in the sip.jar library. Keep sending the wave until the sendingAudio value is set to false.
     */
    @Override
    public void run() {
        RTPPacket rtpPacket = new RTPPacket();
        byte[] rtpBody = new byte[160];
        byte[] rtpMessage = new byte[172];
        OutputAudio.setRunning(true);
        int time = 1;

        long start = System.currentTimeMillis();

        while (OutputAudio.isRunning()) {                                     // while true the program sends audio

            if (System.currentTimeMillis() < start + (20 * time))
                continue;

            if (time > 8000) {
                time = 1;
                start = System.currentTimeMillis();
            } else
                time += 1;                                             // time incrementation

            for (int index = 0; index < 160; index++) {                            // for every byte in the RTP body
                double x = (2 * Math.PI * time * frequency / 8000.0);
                double sinValue = Math.sin(x);                                     // create the sinusoidal wave
                int value = (int) (amplitude * sinValue);
                int sinusoid = G711.linear2ulaw(value);                              // compress the byte with PCM algorithm

                rtpBody[index] = (byte) sinusoid;                                    // replace it in every index of RTP body
            }

            System.arraycopy(rtpPacket.getHeader(), 0, rtpMessage, 0, 12);  // Array Copy set the RTP Header
            System.arraycopy(rtpBody, 0, rtpMessage, 12, rtpBody.length);      // and the RTP Body in RTP Message.
            rtpPacket.incrementSequence();
            rtpPacket.incrementTimeStamp();

            OutputAudio.sendAudio(rtpMessage);
        }
    }
}