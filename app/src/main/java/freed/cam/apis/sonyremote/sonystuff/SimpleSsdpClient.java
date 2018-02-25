/*
 * Copyright 2014 Sony Corporation
 */

package freed.cam.apis.sonyremote.sonystuff;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import freed.utils.FreeDPool;
import freed.utils.Log;

/**
 * A SSDP client class for this sample application. This implementation keeps
 * simple so that many developers understand quickly.
 */
public class SimpleSsdpClient {

    private static final String TAG = SimpleSsdpClient.class.getSimpleName();

    private static final int SSDP_RECEIVE_TIMEOUT = 4000; // msec

    private static final int PACKET_BUFFER_SIZE = 1024;

    private static final int SSDP_PORT = 1900;

    private static final int SSDP_MX = 1;

    private static final String SSDP_ADDR = "239.255.255.250";

    private static final String SSDP_ST = "urn:schemas-sony-com:service:ScalarWebAPI:1";

    /** Handler interface for SSDP search result. */
    public interface SearchResultHandler {

        /**
         * Called when API server device is found. Note that it's performed by
         * non-UI thread.
         *
         * @param device API server device that is found by searching
         */
        void onDeviceFound(ServerDevice device);

        /**
         * Called when searching completes successfully. Note that it's
         * performed by non-UI thread.
         */
        void onFinished();

        /**
         * Called when searching completes with some errors. Note that it's
         * performed by non-UI thread.
         */
        void onErrorFinished();
    }

    private boolean mSearching;

    /**
     * Search API server device.
     *
     * @param handler result handler
     * @return true: start successfully, false: already searching now
     */
    public synchronized boolean search(final SearchResultHandler handler) {
        if (mSearching) {
            Log.d(TAG, "search() already searching.");
            return false;
        }
        if (handler == null) {
            throw new NullPointerException("handler is null.");
        }
        Log.d(TAG, "search() Start.");

        String ssdpRequest =
                "M-SEARCH * HTTP/1.1\r\n" + String.format("HOST: %s:%d\r\n", SSDP_ADDR, SSDP_PORT)
                        + String.format("MAN: \"ssdp:discover\"\r\n")
                        + String.format("MX: %d\r\n", SSDP_MX)
                        + String.format("ST: %s\r\n", SSDP_ST) + "\r\n";
        final byte[] sendData = ssdpRequest.getBytes();

        FreeDPool.Execute(() -> {
            DatagramSocket socket = null;
            DatagramPacket receivePacket = null;
            DatagramPacket packet = null;
            try {
                socket = new DatagramSocket();
                InetSocketAddress iAddress = new InetSocketAddress(SSDP_ADDR, SSDP_PORT);
                packet = new DatagramPacket(sendData, sendData.length, iAddress);
                // send 3 times
                Log.d(TAG, "search() Send Datagram packet 3 times.");
                socket.send(packet);
                Thread.sleep(100);
                socket.send(packet);
                Thread.sleep(100);
                socket.send(packet);
            } catch (SocketException e) {
                Log.e(TAG, "search() DatagramSocket error:");
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                handler.onErrorFinished();
                return;
            } catch (IOException e) {
                Log.e(TAG, "search() IOException :");
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                handler.onErrorFinished();
                return;
            } catch (InterruptedException e) {
                // do nothing.
                Log.d(TAG, "search() InterruptedException :");
            }

            // Receive reply packets
            mSearching = true;
            long startTime = System.currentTimeMillis();
            List<String> foundDevices = new ArrayList<>();
            byte[] array = new byte[PACKET_BUFFER_SIZE];
            ServerDevice device = null;
            try {

                while (mSearching) {
                    receivePacket = new DatagramPacket(array, array.length);
                    socket.setSoTimeout(SSDP_RECEIVE_TIMEOUT);
                    socket.receive(receivePacket);
                    String ssdpReplyMessage = new String(receivePacket.getData(), 0, //
                            receivePacket.getLength(), "UTF-8");
                    String ddUsn = findParameterValue(ssdpReplyMessage, "USN");

                    /*
                     * There is possibility to receive multiple packets from
                     * a individual server.
                     */
                    if (!foundDevices.contains(ddUsn)) {
                        String ddLocation = findParameterValue(ssdpReplyMessage, "LOCATION");
                        foundDevices.add(ddUsn);

                        // Fetch Device Description XML and parse it.
                        device = ServerDevice.fetch(ddLocation);
                        // Note that it's a irresponsible rule
                        // for the sample application.
                        if (device != null && device.hasApiService()) {
                            handler.onDeviceFound(device);
                        }
                    }
                    if (SSDP_RECEIVE_TIMEOUT < System.currentTimeMillis() - startTime) {
                        break;
                    }
                }

            } catch (InterruptedIOException e) {
                Log.d(TAG, "search() Timeout.");

                if (device == null) {
                    mSearching = false;
                    handler.onErrorFinished();
                    return;
                }
            } catch (IOException e) {
                Log.d(TAG, "search() IOException2. : " + e);
                mSearching = false;
                handler.onErrorFinished();
                return;
            } finally {
                Log.d(TAG, "search() Finish ");
                mSearching = false;
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
            handler.onFinished();
        });
        return true;
    }

    /**
     * Checks whether searching is in progress or not.
     *
     * @return true: now searching, false: otherwise
     */
    public boolean isSearching() {
        return mSearching;
    }

    /**
     * Cancels searching. Note that it cannot stop the operation immediately.
     */
    public void cancelSearching() {
        mSearching = false;
    }

    /**
     * Find a value string from message line as below. (ex.)
     * "ST: XXXXX-YYYYY-ZZZZZ" -> "XXXXX-YYYYY-ZZZZZ"
     */
    private static String findParameterValue(String ssdpMessage, String paramName) {
        String name = paramName;
        if (!name.endsWith(":")) {
            name = name + ":";
        }
        int start = ssdpMessage.indexOf(name);
        int end = ssdpMessage.indexOf("\r\n", start);
        if (start != -1 && end != -1) {
            start += name.length();
            String val = ssdpMessage.substring(start, end);
            if (val != null) {
                return val.trim();
            }
        }
        return null;
    }
}
