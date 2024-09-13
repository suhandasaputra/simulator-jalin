package client;

import client.util.ISO8583MessageHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import javax.swing.SwingUtilities;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import client.util.ISO8583MessageUtil;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import main.MainGUI;

public class ResponseListenerThread extends Thread {

    private final Socket socket;
    private final ISO8583MessageUtil messageUtil;
    private final MainGUI mainGUI;
    private final ISO8583MessageHandler messageHandler;

    public ResponseListenerThread(Socket socket, ISOPackager packager, MainGUI mainGUI) {
        this.socket = socket;
        this.messageUtil = new ISO8583MessageUtil(packager);
        this.mainGUI = mainGUI;
        this.messageHandler = new ISO8583MessageHandler(packager, mainGUI);

    }

    @Override
    public void run() {
        try {
            boolean header_status = mainGUI.getheaderstatus();
            boolean calc_status = mainGUI.getcalcstatus();
            int length_message;
            InputStream inputStream = socket.getInputStream();

            byte[] buffer = new byte[4096];
            byte[] messageBytes;

            while (true) {
                int bytesRead = inputStream.read(buffer);
                length_message = bytesRead;

                if (bytesRead == -1) {
                    System.out.println("Server closed the connection.");
                    // No more data to read, exit the loop
                    break;
                }

                if (header_status) {
                    messageBytes = extractISO8583Message(buffer, bytesRead);

                    length_message = messageBytes.length;

                    System.out.println();
                    appendToTextArea("\n");
                    System.out.println("Client Received ISO 8583 message: " + new String(buffer, 0, bytesRead));
                    appendToTextArea("Client Received ISO 8583 message: " + new String(buffer, 0, bytesRead) + "\n");
                    if (calc_status) {
                        length_message = bytesRead;
                    }
                } else {
                    messageBytes = new byte[bytesRead];
                    System.arraycopy(buffer, 0, messageBytes, 0, bytesRead);

                    length_message = messageBytes.length;
                    System.out.println();
                    appendToTextArea("\n");
                    System.out.println("Client Received ISO 8583 message: " + new String(messageBytes));
                    appendToTextArea("Client Received ISO 8583 message: " + new String(messageBytes) + "\n");
                }
                System.out.println("Length ISO 8583 message: " + length_message);
                appendToTextArea("Length ISO 8583 message: " + length_message + "\n");

                ISOMsg isoMessage = messageUtil.unpackISO8583Message(messageBytes);

                String tipe = isoMessage.getMTI().substring(2, 4);

                System.out.println("ini tipe nya : " + tipe);
                if ("10".equals(tipe)) {
                    tipe = "response";
                } else {
                    tipe = "request";
                }

                System.out.println("Received Message Fields:");
                appendToTextArea("Received Message Fields:" + "\n");
                System.out.println("MTI: " + isoMessage.getMTI());
                appendToTextArea("MTI: " + isoMessage.getMTI() + "\n");

                for (int i = 1; i <= isoMessage.getMaxField(); i++) {
                    if (isoMessage.hasField(i)) {
                        System.out.println("Field " + i + ": " + isoMessage.getString(i));
                        appendToTextArea("Field " + i + ": " + isoMessage.getString(i) + "\n");
                    }
                }

                if ("request".equals(tipe)) {
                    messageHandler.processISO8583Message(isoMessage, messageBytes, socket);
                    // Send response
                    byte[] responseBytes = isoMessage.pack();
                    length_message = responseBytes.length;

                    if (header_status) {
                        // Set the header
                        byte[] headerBytes = mainGUI.getHeader().getBytes();

                        // Concatenate the header and the ISO message
                        byte[] completeMessage = new byte[headerBytes.length + responseBytes.length];
                        System.arraycopy(headerBytes, 0, completeMessage, 0, headerBytes.length);
                        System.arraycopy(responseBytes, 0, completeMessage, headerBytes.length, responseBytes.length);

                        responseBytes = completeMessage;

                        if (calc_status) {
                            length_message = responseBytes.length;
                        }
                    }

                    sendResponse(responseBytes, socket);

                    String outIsoMessageString = new String(responseBytes, StandardCharsets.ISO_8859_1); // Using ISO-8859-1 encoding for ISO 8583 messages

                    System.out.println();
                    appendToTextArea("\n");
                    System.out.println("Client sent ISO 8583 response message: " + outIsoMessageString);
                    appendToTextArea("Client sent ISO 8583 response message: " + outIsoMessageString + "\n");
                    System.out.println("Length ISO 8583 message: " + length_message);
                    appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                    System.out.println("Outgoing Message Fields:");
                    appendToTextArea("Outgoing Message Fields:\n");
                    System.out.println("MTI: " + isoMessage.getMTI());
                    appendToTextArea("MTI: " + isoMessage.getMTI() + "\n");

                    for (int i = 1; i <= isoMessage.getMaxField(); i++) {
                        if (isoMessage.hasField(i)) {
                            System.out.println("Field " + i + ": " + isoMessage.getString(i));
                            appendToTextArea("Field " + i + ": " + isoMessage.getString(i) + "\n");
                        }
                    }
                }

                System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                appendToTextArea("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=" + "\n");
            }

            // Close the socket after processing all messages
            socket.close();
        } catch (SocketException e) {
            try {
                // Handle connection closed by server
                System.out.println("Connection Close, " + e.getMessage());
                appendToTextArea("Connection Close, " + e.getMessage() + "\n");
                JOptionPane.showMessageDialog(null, "Connection Close, " + e.getMessage());
                socket.close();
                mainGUI.setdisconnectedclient();
            } catch (IOException ex) {
                Logger.getLogger(ResponseListenerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException | ISOException e) {
            // Handle other IO errors
            System.out.println("Server closed the connection3. " + e.getMessage());
            appendToTextArea("Server closed the connection3. " + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(null, "Server closed the connection3. " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Handle error closing the socket
                System.out.println("Server closed the connection. " + e.getMessage());
                appendToTextArea("Server closed the connection. " + e.getMessage() + "\n");
                JOptionPane.showMessageDialog(null, "Server closed the connection. " + e.getMessage());
            }
        }
    }

    private byte[] extractISO8583Message(byte[] receivedBytes, int bytesRead) {
        // Assuming your header length is 12 (modify as needed)
        int headerLength = 12;
        int isoMessageLength = bytesRead - headerLength;

        byte[] isoMessageBytes = new byte[isoMessageLength];
        System.arraycopy(receivedBytes, headerLength, isoMessageBytes, 0, isoMessageLength);
        return isoMessageBytes;
    }

    private void sendResponse(byte[] responseBytes, Socket socket) throws IOException {
        if (socket.isConnected()) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(responseBytes);
            outputStream.flush();
        } else {
            System.out.println("Socket is not connected. Response not sent.");
            appendToTextArea("Socket is not connected. Response not sent.\n");
            JOptionPane.showMessageDialog(null, "Socket is not connected. Response not sent");
        }
    }

    // Helper method to update the GUI in the EDT
    private void appendToTextArea(String text) {
        SwingUtilities.invokeLater(() -> {
            mainGUI.appendToTextArea(text);
        });
    }
}
