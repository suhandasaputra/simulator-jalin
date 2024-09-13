package server;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import main.MainGUI;
import server.util.ISO8583MessageHandler;
import server.util.ISO8583MessageUtil;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final ISO8583MessageUtil messageUtil;
    private final ISO8583MessageHandler messageHandler;
    private final MainGUI mainGUI;

    public ClientHandler(Socket socket, ISOPackager packager, MainGUI mainGUI) {
        this.socket = socket;
        this.messageUtil = new ISO8583MessageUtil(packager);
        this.messageHandler = new ISO8583MessageHandler(packager, mainGUI);
        this.mainGUI = mainGUI; // Assign mainGUI passed as parameter
    }

    @Override
    public void run() {
        try {
            boolean header_status = mainGUI.getheaderstatus();
            boolean calc_status = mainGUI.getcalcstatus();
            int length_message;
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[4096];
            ISOMsg isoMessage;
            byte[] messageBytes;

            while (true) {
                int bytesRead = inputStream.read(buffer);

                if (bytesRead == -1) {
                    // No more data to read, exit the loop
                    System.out.println("Client disconnected gracefully.");
                    appendToTextArea("Client disconnected gracefully." + "\n");
                    JOptionPane.showMessageDialog(null, "Client disconnected gracefully.");
                    System.out.println("ISO 8583 server listening on port " + socket.getPort());
                    appendToTextArea("ISO 8583 server listening on port " + socket.getPort() + "\n");
                    JOptionPane.showMessageDialog(null, "Client disconnected gracefully");
                    mainGUI.setopen();
                    break;
                }

                if (header_status) {
                    messageBytes = extractISO8583Message(buffer, bytesRead);

                    length_message = messageBytes.length;
                    System.out.println();
                    appendToTextArea("\n");
                    System.out.println("Server Received ISO 8583 message: " + new String(buffer, 0, bytesRead));
                    appendToTextArea("Server Received ISO 8583 message: " + new String(buffer, 0, bytesRead) + "\n");
                    if (calc_status) {
                        length_message = bytesRead;
                    }

                } else {
                    messageBytes = new byte[bytesRead];
                    System.arraycopy(buffer, 0, messageBytes, 0, bytesRead);

                    length_message = messageBytes.length;
                    System.out.println();
                    appendToTextArea("\n");
                    System.out.println("Server Received ISO 8583 message: " + new String(messageBytes));
                    appendToTextArea("Server Received ISO 8583 message: " + new String(messageBytes) + "\n");

                }
                System.out.println("Length ISO 8583 message: " + length_message);
                appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                isoMessage = messageUtil.unpackISO8583Message(messageBytes);

                String tipe = isoMessage.getMTI().substring(2, 4);
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
                    // Print the modified outgoing message fields
                    String outIsoMessageString = new String(responseBytes, StandardCharsets.ISO_8859_1); // Using ISO-8859-1 encoding for ISO 8583 messages
                    appendToTextArea("\n");
                    System.out.println("Server Sent ISO 8583 response message: " + outIsoMessageString);
                    appendToTextArea("Server Sent ISO 8583 response message: " + outIsoMessageString + "\n");
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
            socket.close();
        } catch (IOException | ISOException e) {
            try {
                mainGUI.setopen();
                System.out.println("Connection reset, client down................ " + e.getMessage());
                System.out.println("ISO 8583 server listening on port " + socket.getPort());
                JOptionPane.showMessageDialog(null, "Connection reset, client down................ " + e.getMessage());
                JOptionPane.showMessageDialog(null, "ISO 8583 server listening on port " + socket.getPort());
                appendToTextArea("Connection reset, client down................ " + e.getMessage() + " \n");
                appendToTextArea("ISO 8583 server listening on port " + socket.getPort());
//            e.printStackTrace();
            } catch (ISOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private byte[] extractISO8583Message(byte[] receivedBytes, int bytesRead) {
        // Assuming your header length is 12 (modify as needed)
        int headerLength = mainGUI.getHeader().length();
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
