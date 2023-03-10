package Audio;

import VoIP.UserAgent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Output Audio
 *
 * This is the base-audio class. It stores all that is necessary to set up the RTP Connection.
 *  It has the sourcePort attribute, which is the port used by the VoIP.UserAgent to send
 *  RTP Packets to the mjUA "Bob", and DatagramSocket socketOutgoing, previously initialized
 *  in the VoIP.UserAgent Class, used to send Requests to the other mjUA. Then it has boolean attribute
 *  sendingAudio, which is initially set as false, and turns to true once the VoIP.UserAgent starts to send audio.
 */
public abstract class OutputAudio {
    private static int sourcePort;
    private static DatagramSocket socketOutgoing = UserAgent.getSocketOutgoing();
    private static volatile boolean running = false;

    /**
     * Set the Source Port
     *
     * @param newValue the port's number
     */
    public static void setSourcePort(int newValue){
        sourcePort = newValue;
    }

    /**
     * Set sendingAudio
     *
     * @param value, true if the program is sending audio, false otherwise
     */
    public static void setRunning(boolean value) {
        running = value;
    }

    /**
     * Get method to sendingAudio boolean value
     *
     * @return the value of sendingAudio
     */
    public static boolean isRunning(){
        return running;
    }

    /**
     * Send audio (in a RTP packet, in bytes[]) to the mjUA "Bob"
     *
     * @param toSend the byte to send
     */
    public static void sendAudio(byte[] toSend) {
        try {
            DatagramPacket sendPacket = new DatagramPacket(toSend, toSend.length, UserAgent.getAddress(), sourcePort);
            socketOutgoing.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
