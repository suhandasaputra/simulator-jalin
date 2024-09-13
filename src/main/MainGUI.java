package main;

import java.awt.Color;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import client.util.StringFunction;
import java.net.ServerSocket;
import server.ClientHandler;
import client.ResponseListenerThread;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.JList;

/**
 *
 * @author suhan
 */
public class MainGUI extends javax.swing.JFrame {

    private final GenericPackager packager;
    private String status;
    private String header;
    private boolean header_status;
    private boolean calc_length;
    private String trf_indokator;
    private String bit_48;
    private int length_message;
    private String path;
    private JList<String> dataList;

    Socket clientsocket;
    ServerSocket serversocket;

    public MainGUI() throws ISOException {
        initComponents();
        setVisible(true);

        if (jComboBoxChannel.getSelectedItem().equals("Core Banking")) {
            path = "C:\\opt\\asacgateway\\core_banking.xml";
        } else if (jComboBoxChannel.getSelectedItem().equals("Jalin")) {
            path = "C:\\opt\\asacgateway\\jalin.xml";
        } else if (jComboBoxChannel.getSelectedItem().equals("Artajasa")) {
            path = "C:\\opt\\asacgateway\\artajasa.xml";
        } else if (jComboBoxChannel.getSelectedItem().equals("Rintis")) {
            path = "C:\\opt\\asacgateway\\rintis.xml";
        }
        this.packager = new GenericPackager(path);
    }

    private void connecttoserver() {
        while (true) {
            try {
                String serverIP = getTextFieldIP();
                int serverPort = Integer.parseInt(getTextFieldPort());
                clientsocket = new Socket(serverIP, serverPort);

                // Establish connection
                ResponseListenerThread responseListenerThread = new ResponseListenerThread(clientsocket, packager, this);
                responseListenerThread.start();
                setconnected(); // Set the connection status once

                break; // Exit the loop after successful connection
            } catch (SocketException e) {
                // Handle connection closed by server
                System.out.println("Connection unavailable , please check server " + e.getMessage());
                appendToTextArea("Connection unavailable , please check server " + e.getMessage() + "\n");
                JOptionPane.showMessageDialog(null, "Connection unavailable , please check server " + e.getMessage());
                break; // Exit the loop
            } catch (IOException | NumberFormatException e) {
                // Handle other IO or number format exceptions
                System.out.println(e.getMessage());
                appendToTextArea(e.getMessage() + "\n");
                JOptionPane.showMessageDialog(null, e.getMessage());
                break; // Exit the loop
            }

        }
    }

    public void setjTextFieldAs(String a) {
        jTextFieldAs.setText(a);
    }

    public void setserver() {
        jButtonConnect.setText("Open");
        jTextFieldIP.setEditable(false);
        jTextFieldPort.setEditable(true);
        jTextFieldAs.setText("Server");
        jTextFieldStatus.setText("Close");

        jButtonBalInq.setEnabled(true);
        jButtonWitdraw.setEnabled(true);
        jButtonTrfInq.setEnabled(true);
        jButtonTrf.setEnabled(true);
        jComboBox1.setEnabled(true);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"iss only", "iss dest", "dest only"}));
    }

    public void setclient() {
        jButtonConnect.setText("Connect");
        jTextFieldIP.setEditable(true);
        jTextFieldPort.setEditable(true);
        jTextFieldAs.setText("Client");
        jTextFieldStatus.setText("Disconnected");

        jButtonBalInq.setEnabled(true);
        jButtonWitdraw.setEnabled(true);
        jButtonTrfInq.setEnabled(true);
        jButtonTrf.setEnabled(true);
        jComboBox1.setEnabled(true);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"acq only", "acq iss", "acq bene"}));

    }

    public void setconnected() {
        jTextFieldIP.setEditable(false);
        jTextFieldPort.setEditable(false);
        jRadioButtonServer.setEnabled(false);
        jRadioButtonClient.setEnabled(false);
        jButtonConnect.setText("Disconnect");
        jTextFieldStatus.setText("Connected");
        jTextFieldStatus.setBackground(Color.green);
        jTextFieldHeader.setEditable(false);
        jCheckBoxHeader.setEnabled(false);
        jCheckBoxCalcLengthWithHeader.setEnabled(false);
        jLabel17.setEnabled(false);
        jComboBoxHeaderType.setEnabled(false);
        jComboBoxChannel.setEnabled(false);

        System.out.println("Connection accepted to " + clientsocket.getInetAddress() + " " + clientsocket.getPort()); // Print the message
        appendToTextArea("Connection accepted to " + clientsocket.getInetAddress() + " " + clientsocket.getPort() + "\n");
        JOptionPane.showMessageDialog(null, "Connection accepted to " + clientsocket.getInetAddress() + " " + clientsocket.getPort());
    }

    public void setdisconnectedclient() {
        jTextFieldIP.setEditable(true);
        jTextFieldPort.setEditable(true);
        jRadioButtonServer.setEnabled(true);
        jRadioButtonClient.setEnabled(true);
        jButtonConnect.setText("Connect");
        jTextFieldStatus.setText("Disconnected");
        jTextFieldStatus.setBackground(Color.red);
        jTextFieldHeader.setEditable(true);
        jCheckBoxHeader.setEnabled(true);
        jCheckBoxCalcLengthWithHeader.setEnabled(true);
        jLabel17.setEnabled(true);
        jComboBoxHeaderType.setEnabled(true);
        jComboBoxChannel.setEnabled(true);
    }

    public void setopen() throws ISOException {
        jTextFieldIP.setEditable(false);
        jTextFieldPort.setEditable(false);
        jRadioButtonServer.setEnabled(false);
        jRadioButtonClient.setEnabled(false);
        jButtonConnect.setText("Close");
        jTextFieldStatus.setText("Waiting");
        jTextFieldStatus.setBackground(Color.yellow);
        jTextFieldHeader.setEditable(false);
        jCheckBoxHeader.setEnabled(false);
        jCheckBoxCalcLengthWithHeader.setEnabled(false);
        jLabel17.setEnabled(false);
        jComboBoxHeaderType.setEnabled(false);
        jComboBoxChannel.setEnabled(false);
    }

    public void setclose() {
        jTextFieldIP.setEditable(false);
        jTextFieldPort.setEditable(true);
        jRadioButtonServer.setEnabled(true);
        jRadioButtonClient.setEnabled(true);
        jButtonConnect.setText("Open");
        jTextFieldStatus.setText("Close");
        jTextFieldStatus.setBackground(Color.red);
        jTextFieldHeader.setEditable(true);
        jCheckBoxHeader.setEnabled(true);
        jCheckBoxCalcLengthWithHeader.setEnabled(true);
        jLabel17.setEnabled(true);
        jComboBoxHeaderType.setEnabled(true);
        jComboBoxChannel.setEnabled(true);

    }

    public void appendToTextArea(String text) {
        jTextArea1.append(text);
    }

    public String getTextFieldIP() {
        return jTextFieldIP.getText();
    }

    public String getTextFieldPort() {
        return jTextFieldPort.getText();
    }

    public String getSelectedMode() {
        if (jRadioButtonServer.isSelected()) {
            return "Server";
        } else if (jRadioButtonClient.isSelected()) {
            return "Client";
        } else {
            return null;
        }
    }

    public boolean getheaderstatus() {
        boolean stat = jCheckBoxHeader.isSelected();
        return stat;
    }

    public boolean getcalcstatus() {
        boolean stst = jCheckBoxCalcLengthWithHeader.isSelected();
        return stst;
    }

    public String getHeader() {
        return jTextFieldHeader.getText();
    }

    private void create_thread_server() {
        Thread serverThread = new Thread(() -> {
            try {

                int port = Integer.parseInt(jTextFieldPort.getText()); // Specify the port number you want to listen on
                serversocket = new ServerSocket(port);
                System.out.println("ISO 8583 server listening on port " + port);
                appendToTextArea("ISO 8583 server listening on port " + port + "\n");
                JOptionPane.showMessageDialog(null, "ISO 8583 server listening on port " + port);
                setopen();
                while (true) {
                    try {
                        clientsocket = serversocket.accept();
                        System.out.println("Connection established with client: " + clientsocket.getInetAddress());
                        appendToTextArea("Connection established with client: " + clientsocket.getInetAddress() + "\n");
                        jTextFieldStatus.setBackground(Color.green);
                        jTextFieldStatus.setText("Connected");
                        new ClientHandler(clientsocket, packager, this).start();
                    } catch (SocketException se) {
                        // Tangkap pengecualian SocketException saat server socket ditutup
                        if (serversocket.isClosed()) {
                            System.out.println("Server socket closed. Exiting server thread.");
                            appendToTextArea("Server socket closed. Exiting server thread.\n");
                            JOptionPane.showMessageDialog(null, "Server socket closed. Exiting server thread.");
                            break; // Keluar dari loop penerimaan koneksi
                        } else {
                            // Lanjutkan penanganan pengecualian jika bukan karena penutupan server socket
                            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, se);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException e) {
                appendToTextArea("error : " + e.getMessage() + "\n");
                System.out.println("error : " + e.getMessage());
                JOptionPane.showMessageDialog(null, e.getMessage());
            } catch (ISOException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        serverThread.start();
    }

    public void stopServerListener() {
        if (serversocket != null && !serversocket.isClosed()) {
            try {
                serversocket.close();
//                System.out.println("Server listener closed");
//                appendToTextArea("Server listener closed\n");
//                JOptionPane.showMessageDialog(null, "Server listener closed");
            } catch (IOException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                appendToTextArea("Error closing server listener: " + ex.getMessage() + "\n");
                System.out.println("Error closing server listener: " + ex.getMessage());
            }
        } else {
            appendToTextArea("Server listener is already closed.\n");
            System.out.println("Server listener is already closed.");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jRadioButtonServer = new javax.swing.JRadioButton();
        jRadioButtonClient = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldIP = new javax.swing.JTextField();
        jTextFieldPort = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jTextFieldHeader = new javax.swing.JTextField();
        jCheckBoxHeader = new javax.swing.JCheckBox();
        jCheckBoxCalcLengthWithHeader = new javax.swing.JCheckBox();
        jComboBoxHeaderType = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jComboBoxChannel = new javax.swing.JComboBox<>();
        jPanel15 = new javax.swing.JPanel();
        jTextField_addcard_name = new javax.swing.JTextField();
        jTextField_addcard_pan = new javax.swing.JTextField();
        jTextField_addcard_expired = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextField_addcard_cvv = new javax.swing.JTextField();
        jTextField_addcard_d_data = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jTextField_addcard_pin = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jButtonSignon = new javax.swing.JButton();
        jButtonSignoff = new javax.swing.JButton();
        jButtonEcho = new javax.swing.JButton();
        jButtonKeyChange = new javax.swing.JButton();
        jButtonNewKey = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jButtonTrf = new javax.swing.JButton();
        jButtonTrfInq = new javax.swing.JButton();
        jButtonBalInq = new javax.swing.JButton();
        jButtonWitdraw = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldStan = new javax.swing.JTextField();
        jButtonClearLog = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldPin = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldNoKartu = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldExpiredDate = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldCvv = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextFieldAcqCode = new javax.swing.JTextField();
        jTextFieldIssCode = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldDestCode = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldDestAccount = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextFieldAmount = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextFieldBit55 = new javax.swing.JTextArea();
        jLabel16 = new javax.swing.JLabel();
        jTextFieldTerminalNumber = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextFieldDisData = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldStatus = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldAs = new javax.swing.JTextField();
        jButtonConnect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Connection"));

        buttonGroup1.add(jRadioButtonServer);
        jRadioButtonServer.setSelected(true);
        jRadioButtonServer.setText("Server");
        jRadioButtonServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonServerActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButtonClient);
        jRadioButtonClient.setText("Client");
        jRadioButtonClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonClientActionPerformed(evt);
            }
        });

        jLabel1.setText("IP");

        jLabel2.setText("Port");

        jTextFieldIP.setEditable(false);
        jTextFieldIP.setText("127.0.0.1");
        jTextFieldIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIPActionPerformed(evt);
            }
        });

        jTextFieldPort.setEditable(false);
        jTextFieldPort.setText("14000");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButtonServer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonClient)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldIP)
                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonServer)
                    .addComponent(jRadioButtonClient))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        jRadioButtonServer.getAccessibleContext().setAccessibleDescription("");

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Header"));

        jTextFieldHeader.setText("ISO015000010");
        jTextFieldHeader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldHeaderActionPerformed(evt);
            }
        });

        jCheckBoxHeader.setText("Header");
        jCheckBoxHeader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxHeaderActionPerformed(evt);
            }
        });

        jCheckBoxCalcLengthWithHeader.setText("Calc Length with header");

        jComboBoxHeaderType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BIN_LENGTH_2", "BIN_INTEL_2", "TXT_LENGTH_2", "TXT_LENGTH_4" }));

        jLabel17.setText("Header type");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxCalcLengthWithHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxHeaderType, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jCheckBoxHeader)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxHeader)
                    .addComponent(jTextFieldHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxCalcLengthWithHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxHeaderType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Channel H2H"));

        jComboBoxChannel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Core Banking", "Jalin", "Artajasa", "Rintis" }));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxChannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxChannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Add Card"));

        jLabel19.setText("pin");

        jLabel20.setText("pan");

        jLabel21.setText("expired");

        jLabel22.setText("cvv");

        jLabel23.setText("d. data");

        jList1.setBorder(javax.swing.BorderFactory.createTitledBorder("Card"));
        jScrollPane3.setViewportView(jList1);

        jButton1.setText("add card");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("delete card");

        jLabel24.setText("name");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(136, 136, 136))
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField_addcard_name, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .addComponent(jTextField_addcard_d_data)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTextField_addcard_cvv)
                        .addComponent(jTextField_addcard_expired, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                    .addComponent(jTextField_addcard_pan)
                    .addComponent(jTextField_addcard_pin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(jTextField_addcard_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_addcard_pin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_addcard_pan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_addcard_expired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_addcard_cvv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_addcard_d_data, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(232, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Configure", jPanel1);

        jPanel2.setPreferredSize(new java.awt.Dimension(930, 544));

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setBorder(javax.swing.BorderFactory.createTitledBorder("log"));
        jScrollPane1.setViewportView(jTextArea1);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("0800"));

        jButtonSignon.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jButtonSignon.setText("signon");
        jButtonSignon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSignonActionPerformed(evt);
            }
        });

        jButtonSignoff.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jButtonSignoff.setText("signoff");
        jButtonSignoff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSignoffActionPerformed(evt);
            }
        });

        jButtonEcho.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jButtonEcho.setText("echo");
        jButtonEcho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEchoActionPerformed(evt);
            }
        });

        jButtonKeyChange.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jButtonKeyChange.setText("key chg");
        jButtonKeyChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonKeyChangeActionPerformed(evt);
            }
        });

        jButtonNewKey.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jButtonNewKey.setText("new key");
        jButtonNewKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewKeyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButtonKeyChange, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(jButtonEcho, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(jButtonSignoff, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(jButtonSignon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonNewKey, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jButtonSignon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSignoff)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEcho)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonKeyChange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNewKey)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("0200"));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Transaction"));

        jButtonTrf.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jButtonTrf.setText("Trf Pay");
        jButtonTrf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTrfActionPerformed(evt);
            }
        });

        jButtonTrfInq.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jButtonTrfInq.setText("Trf Inq");
        jButtonTrfInq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTrfInqActionPerformed(evt);
            }
        });

        jButtonBalInq.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jButtonBalInq.setText("Bal Inq");
        jButtonBalInq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBalInqActionPerformed(evt);
            }
        });

        jButtonWitdraw.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jButtonWitdraw.setText("Witdraw");
        jButtonWitdraw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonWitdrawActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonBalInq, javax.swing.GroupLayout.PREFERRED_SIZE, 82, Short.MAX_VALUE)
                    .addComponent(jButtonTrfInq, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonWitdraw, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTrf, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonBalInq)
                    .addComponent(jButtonWitdraw))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTrfInq)
                    .addComponent(jButtonTrf))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(124, 124, 124))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("0400"));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel5.setText("stan");

        jTextFieldStan.setText("1");

        jButtonClearLog.setText("Clear");
        jButtonClearLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearLogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jButtonClearLog, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldStan, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldStan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jButtonClearLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel6.setText("pin");

        jTextFieldPin.setText("C26ED899905F7BF2");

        jLabel7.setText("pan");

        jTextFieldNoKartu.setText("6032988900160180");

        jLabel8.setText("expired");

        jTextFieldExpiredDate.setText("2512");

        jLabel9.setText("cvv");

        jTextFieldCvv.setText("220");

        jLabel10.setText("bit 55");

        jLabel12.setText("acq code");

        jLabel13.setText("iss code");

        jTextFieldAcqCode.setText("147");

        jLabel14.setText("dest code");

        jLabel15.setText("dest acc");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel11.setText("amount");

        jTextFieldAmount.setText("000000000000");

        jTextFieldBit55.setColumns(20);
        jTextFieldBit55.setRows(5);
        jTextFieldBit55.setText("820274009F360205C29F26082A1F3E8764227FAE9F101C0101A000000000772B97030000000000000000000000000000000000950580000400009F37042C30491F9F02060000000000009F03060000000000005F3401019F1A0203605F2A0203609A032401179C01309F2701808407A00000060210105A086032988900160180");
        jScrollPane2.setViewportView(jTextFieldBit55);

        jLabel16.setText("t. num");
        jLabel16.setPreferredSize(new java.awt.Dimension(50, 21));

        jTextFieldTerminalNumber.setText("0000000000000098");

        jLabel18.setText("d. data");

        jTextFieldDisData.setText("4473665700000");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(15, 15, 15)))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextFieldCvv)
                                    .addComponent(jTextFieldTerminalNumber)
                                    .addComponent(jTextFieldDisData)
                                    .addComponent(jTextFieldNoKartu, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextFieldPin, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)))
                            .addComponent(jTextFieldExpiredDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTextFieldAcqCode)
                                    .addComponent(jTextFieldDestAccount)
                                    .addComponent(jTextFieldAmount, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldIssCode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jTextFieldDestCode, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldNoKartu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldExpiredDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldCvv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jTextFieldAcqCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTextFieldIssCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldDestCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jTextFieldDestAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextFieldTerminalNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextFieldAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11)))))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jTextFieldDisData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(11, 11, 11)))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 196, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        jTabbedPane1.addTab("Message", jPanel2);

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setText("Status");

        jTextFieldStatus.setEditable(false);
        jTextFieldStatus.setBackground(new java.awt.Color(255, 51, 51));
        jTextFieldStatus.setText("Close");

        jLabel4.setText("As ");

        jTextFieldAs.setEditable(false);

        jButtonConnect.setText("Open");
        jButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldAs, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jButtonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonConnect, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextFieldAs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jTextFieldStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonClearLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearLogActionPerformed
        // TODO add your handling code here:
        jTextArea1.setText("");
    }//GEN-LAST:event_jButtonClearLogActionPerformed

    private void jButtonTrfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTrfActionPerformed
        // TODO add your handling code here:
        header_status = jCheckBoxHeader.isSelected();
        status = jTextFieldStatus.getText();
        calc_length = jCheckBoxCalcLengthWithHeader.isSelected();

        if ("Disconnected".equals(status)) {
            JOptionPane.showMessageDialog(null, "Belum Terkoneksi");
        } else if ("Connected".equals(status)) {
            if (jTextFieldAmount.getText().equals("") || jTextFieldPin.getText().equals("") || jTextFieldNoKartu.getText().equals("") || jTextFieldCvv.getText().equals("") || jTextFieldExpiredDate.getText().equals("") || jTextFieldTerminalNumber.getText().equals("") || jTextFieldAcqCode.getText().equals("") || jTextFieldIssCode.getText().equals("") || jTextFieldDestCode.getText().equals("") || jTextFieldDestAccount.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "amount, pin, kartu, cvv, expired, terminal number, acq code, iss code, dest code dan dest acc tidak boleh kosong");
            } else {
                try {

                    // Create a new ISO message for each iteration
                    ISOMsg isoMsg = new ISOMsg();
                    isoMsg.setPackager(packager);

                    if (jComboBox1.getSelectedItem().equals("acq only")) {
                        bit_48 = "NAMA REKENING TUJUAN          REFERENCE NUMBERNAMA PEMILIK KARTU            ";
                        trf_indokator = "3";
                    } else if (jComboBox1.getSelectedItem().equals("acq iss")) {
                        bit_48 = "NAMA REKENING TUJUAN          REFERENCE NUMBERNAMA PEMILIK KARTU            ";
                        trf_indokator = "3";
                    } else if (jComboBox1.getSelectedItem().equals("acq bene")) {
                        bit_48 = "SYAHRIR K BANGSAWAN                           NAMA NASABAH BANK TUJUAN      ";
                        trf_indokator = "3";
                    }

                    // Set the specific fields for each message
                    isoMsg.setMTI("0200");
                    isoMsg.set(3, "401000");
                    isoMsg.set(4, jTextFieldAmount.getText());
                    isoMsg.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                    isoMsg.set(11, StringFunction.pad(jTextFieldStan.getText(), 6, "0", "Right"));
                    isoMsg.set(12, StringFunction.getCurrentTimeHHmmss());
                    isoMsg.set(13, StringFunction.getCurrentDateMMDD());
                    isoMsg.set(17, StringFunction.getCurrentDateMMDD());
                    isoMsg.set(18, "6011");
                    isoMsg.set(22, "051");
                    isoMsg.set(32, "10000000" + jTextFieldAcqCode.getText());
                    isoMsg.set(35, jTextFieldNoKartu.getText() + "=" + jTextFieldExpiredDate.getText() + jTextFieldCvv.getText() + jTextFieldDisData.getText());
                    isoMsg.set(37, StringFunction.pad(jTextFieldStan.getText(), 12, "0", "Right"));
                    isoMsg.set(41, jTextFieldTerminalNumber.getText());
                    isoMsg.set(43, "MUAMALAT                         Jak  ID");
                    isoMsg.set(48, bit_48);
                    isoMsg.set(49, "360");
                    isoMsg.set(52, jTextFieldPin.getText());
                    isoMsg.set(55, jTextFieldBit55.getText());
                    isoMsg.set(60, "THIS IS TERMINAL DATA");
                    isoMsg.set(100, "10000000" + jTextFieldIssCode.getText());
                    isoMsg.set(103, jTextFieldDestAccount.getText());//norek tujuan 098098098098 jalin, 3310004933 bmi
                    isoMsg.set(125, trf_indokator);
                    isoMsg.set(127, "10000000" + jTextFieldDestCode.getText());

                    // Pack the ISO message into a byte array
                    byte[] isoBytes = isoMsg.pack();
                    length_message = isoBytes.length;

                    if (header_status) {
                        // Set the header
                        header = getHeader();
                        byte[] headerBytes = header.getBytes();

                        if (calc_length) {
                            // Concatenate the header and the ISO message
                            byte[] completeMessage = new byte[headerBytes.length + isoBytes.length];
                            System.arraycopy(headerBytes, 0, completeMessage, 0, headerBytes.length);
                            System.arraycopy(isoBytes, 0, completeMessage, headerBytes.length, isoBytes.length);

                            isoBytes = completeMessage;

                            if (calc_length) {
                                length_message = isoBytes.length;
                            }
                        }
                    }

                    // Print the sent ISO 8583 message
                    String sentIsoMessageString = new String(isoBytes);
                    System.out.println();
                    System.out.println("Sent ISO 8583 request message: " + sentIsoMessageString);
                    System.out.println("Length ISO 8583 message: " + length_message);
                    System.out.println("Outgoing Message Fields:");
                    System.out.println("Header: " + header);
                    System.out.println("MTI: " + isoMsg.getMTI());

                    appendToTextArea("\n");
                    appendToTextArea("Sent ISO 8583 request message: " + sentIsoMessageString + "\n");
                    appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                    appendToTextArea("Outgoing Message Fields:" + "\n");
                    appendToTextArea("Header: " + header + "\n");
                    appendToTextArea("MTI: " + isoMsg.getMTI() + "\n");

                    for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                        if (isoMsg.hasField(i)) {
                            System.out.println("Field " + i + ": " + isoMsg.getString(i));
                            appendToTextArea("Field " + i + ": " + isoMsg.getString(i) + "\n");
                        }
                    }
                    // Send the ISO message to the destination system using the client's socket
                    clientsocket.getOutputStream().write(isoBytes);
                } catch (IOException | ISOException e) {
                    e.printStackTrace();
                }
            }
            jTextFieldStan.setText(String.valueOf(1 + Long.parseLong(jTextFieldStan.getText())));
        }
    }//GEN-LAST:event_jButtonTrfActionPerformed

    private void jButtonTrfInqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTrfInqActionPerformed
        // TODO add your handling code here:
        header_status = jCheckBoxHeader.isSelected();
        status = jTextFieldStatus.getText();
        calc_length = jCheckBoxCalcLengthWithHeader.isSelected();

        if ("Disconnected".equals(status)) {
            JOptionPane.showMessageDialog(null, "Belum Terkoneksi");
        } else if ("Connected".equals(status)) {
            if (jTextFieldAmount.getText().equals("") || jTextFieldPin.getText().equals("") || jTextFieldNoKartu.getText().equals("") || jTextFieldCvv.getText().equals("") || jTextFieldExpiredDate.getText().equals("") || jTextFieldTerminalNumber.getText().equals("") || jTextFieldAcqCode.getText().equals("") || jTextFieldIssCode.getText().equals("") || jTextFieldDestCode.getText().equals("") || jTextFieldDestAccount.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "amount, pin, kartu, cvv, expired, terminal number, acq code, iss code, dest code dan dest acc tidak boleh kosong");
            } else {
                try {

                    if (jComboBox1.getSelectedItem().equals("acq only")) {
                        trf_indokator = "3";
                    } else if (jComboBox1.getSelectedItem().equals("acq iss")) {
                        trf_indokator = "2";
                    } else if (jComboBox1.getSelectedItem().equals("acq bene")) {
                        trf_indokator = "1";
                    }

                    // Create a new ISO message for each iteration
                    ISOMsg isoMsg = new ISOMsg();
                    isoMsg.setPackager(packager);

                    // Set the specific fields for each message
                    isoMsg.setMTI("0200");
                    isoMsg.set(3, "391000");
                    isoMsg.set(4, jTextFieldAmount.getText());
                    isoMsg.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                    isoMsg.set(11, StringFunction.pad(jTextFieldStan.getText(), 6, "0", "Right"));
                    isoMsg.set(12, StringFunction.getCurrentTimeHHmmss());
                    isoMsg.set(13, StringFunction.getCurrentDateMMDD());
                    isoMsg.set(17, StringFunction.getCurrentDateMMDD());
                    isoMsg.set(18, "6011");
                    isoMsg.set(22, "051");
                    isoMsg.set(32, "10000000" + jTextFieldAcqCode.getText());//kode bank acquirer
                    isoMsg.set(35, jTextFieldNoKartu.getText() + "=" + jTextFieldExpiredDate.getText() + jTextFieldCvv.getText() + jTextFieldDisData.getText());
                    isoMsg.set(37, StringFunction.pad(jTextFieldStan.getText(), 12, "0", "Right"));
                    isoMsg.set(41, jTextFieldTerminalNumber.getText());
                    isoMsg.set(43, "MUAMALAT                         Jak  ID");
                    isoMsg.set(48, "                                                                            ");
                    isoMsg.set(49, "360");
                    isoMsg.set(52, jTextFieldPin.getText());
                    isoMsg.set(60, "THIS IS TERMINAL DATA");
                    isoMsg.set(100, "10000000" + jTextFieldIssCode.getText());//kode bank penerbit kartu
                    isoMsg.set(103, jTextFieldDestAccount.getText());//norek tujuan 098098098098 jalin, 3310004933 bmi
                    isoMsg.set(125, trf_indokator);
                    isoMsg.set(127, "10000000" + jTextFieldDestCode.getText());//kode bank tujuan

                    // Pack the ISO message into a byte array
                    byte[] isoBytes = isoMsg.pack();
                    length_message = isoBytes.length;

                    if (header_status) {
                        // Set the header
                        header = getHeader();
                        byte[] headerBytes = header.getBytes();

                        if (calc_length) {
                            // Concatenate the header and the ISO message
                            byte[] completeMessage = new byte[headerBytes.length + isoBytes.length];
                            System.arraycopy(headerBytes, 0, completeMessage, 0, headerBytes.length);
                            System.arraycopy(isoBytes, 0, completeMessage, headerBytes.length, isoBytes.length);

                            isoBytes = completeMessage;

                            if (calc_length) {
                                length_message = isoBytes.length;
                            }
                        }
                    }

                    // Print the sent ISO 8583 message
                    String sentIsoMessageString = new String(isoBytes);
                    System.out.println();
                    System.out.println("Sent ISO 8583 request message: " + sentIsoMessageString);
                    System.out.println("Length ISO 8583 message: " + length_message);
                    System.out.println("Outgoing Message Fields:");
                    System.out.println("Header: " + header);
                    System.out.println("MTI: " + isoMsg.getMTI());

                    appendToTextArea("\n");
                    appendToTextArea("Sent ISO 8583 request message: " + sentIsoMessageString + "\n");
                    appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                    appendToTextArea("Outgoing Message Fields:" + "\n");
                    appendToTextArea("Header: " + header + "\n");
                    appendToTextArea("MTI: " + isoMsg.getMTI() + "\n");

                    for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                        if (isoMsg.hasField(i)) {
                            System.out.println("Field " + i + ": " + isoMsg.getString(i));
                            appendToTextArea("Field " + i + ": " + isoMsg.getString(i) + "\n");
                        }
                    }
                    // Send the ISO message to the destination system using the client's socket
                    clientsocket.getOutputStream().write(isoBytes);
                } catch (IOException | ISOException e) {
                    e.printStackTrace();
                }
            }
            jTextFieldStan.setText(String.valueOf(1 + Long.parseLong(jTextFieldStan.getText())));
        }

    }//GEN-LAST:event_jButtonTrfInqActionPerformed

    private void jButtonWitdrawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWitdrawActionPerformed
        // TODO add your handling code here:
        header_status = jCheckBoxHeader.isSelected();
        status = jTextFieldStatus.getText();
        calc_length = jCheckBoxCalcLengthWithHeader.isSelected();

        if ("Disconnected".equals(status)) {
            JOptionPane.showMessageDialog(null, "Belum Terkoneksi");
        } else if ("Connected".equals(status)) {
            if (jTextFieldAmount.getText().equals("") || jTextFieldPin.getText().equals("") || jTextFieldNoKartu.getText().equals("") || jTextFieldExpiredDate.getText().equals("") || jTextFieldCvv.getText().equals("") || jTextFieldAcqCode.getText().equals("") || jTextFieldBit55.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "amount, pin, kartu, cvv, expired, chip dan acq code tidak boleh kosong");
            } else {
                try {
                    // Create a new ISO message for each iteration
                    ISOMsg isoMsg = new ISOMsg();
                    isoMsg.setPackager(packager);

                    // Set the specific fields for each message
                    isoMsg.setMTI("0200");
                    isoMsg.set(3, "011000");
                    isoMsg.set(4, jTextFieldAmount.getText());
                    isoMsg.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                    isoMsg.set(11, StringFunction.pad(jTextFieldStan.getText(), 6, "0", "Right"));
                    isoMsg.set(12, StringFunction.getCurrentTimeHHmmss());
                    isoMsg.set(13, StringFunction.getCurrentDateMMDD());
                    isoMsg.set(17, StringFunction.getCurrentDateMMDD());
                    isoMsg.set(22, "051");
                    isoMsg.set(32, "10000000" + jTextFieldAcqCode.getText());
                    isoMsg.set(35, jTextFieldNoKartu.getText() + "=" + jTextFieldExpiredDate.getText() + jTextFieldCvv.getText() + jTextFieldDisData.getText());
                    isoMsg.set(37, StringFunction.pad(jTextFieldStan.getText(), 12, "0", "Right"));
                    isoMsg.set(41, jTextFieldTerminalNumber.getText());
                    isoMsg.set(43, "MT                               Jak ID ");
                    isoMsg.set(48, "A                       40000036000000000000");
                    isoMsg.set(49, "360");
                    isoMsg.set(52, jTextFieldPin.getText());
                    isoMsg.set(55, jTextFieldBit55.getText());
                    isoMsg.set(60, "THIS IS TERMINAL DATA");

                    // Pack the ISO message into a byte array
                    byte[] isoBytes = isoMsg.pack();
                    length_message = isoBytes.length;

                    if (header_status) {
                        // Set the header
                        header = getHeader();
                        byte[] headerBytes = header.getBytes();

                        if (calc_length) {
                            // Concatenate the header and the ISO message
                            byte[] completeMessage = new byte[headerBytes.length + isoBytes.length];
                            System.arraycopy(headerBytes, 0, completeMessage, 0, headerBytes.length);
                            System.arraycopy(isoBytes, 0, completeMessage, headerBytes.length, isoBytes.length);

                            isoBytes = completeMessage;

                            if (calc_length) {
                                length_message = isoBytes.length;
                            }
                        }
                    }

                    // Print the sent ISO 8583 message
                    String sentIsoMessageString = new String(isoBytes);
                    System.out.println();
                    System.out.println("Sent ISO 8583 request message: " + sentIsoMessageString);
                    System.out.println("Length ISO 8583 message: " + length_message);
                    System.out.println("Outgoing Message Fields:");
                    System.out.println("Header: " + header);
                    System.out.println("MTI: " + isoMsg.getMTI());

                    appendToTextArea("\n");
                    appendToTextArea("Sent ISO 8583 request message: " + sentIsoMessageString + "\n");
                    appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                    appendToTextArea("Outgoing Message Fields:" + "\n");
                    appendToTextArea("Header: " + header + "\n");
                    appendToTextArea("MTI: " + isoMsg.getMTI() + "\n");

                    for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                        if (isoMsg.hasField(i)) {
                            System.out.println("Field " + i + ": " + isoMsg.getString(i));
                            appendToTextArea("Field " + i + ": " + isoMsg.getString(i) + "\n");
                        }
                    }
                    // Send the ISO message to the destination system using the client's socket
                    clientsocket.getOutputStream().write(isoBytes);
                } catch (IOException | ISOException e) {
                    e.printStackTrace();
                }
            }
            jTextFieldStan.setText(String.valueOf(1 + Long.parseLong(jTextFieldStan.getText())));
        }
    }//GEN-LAST:event_jButtonWitdrawActionPerformed

    private void jButtonBalInqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBalInqActionPerformed
        // TODO add your handling code here:
        header_status = jCheckBoxHeader.isSelected();
        status = jTextFieldStatus.getText();
        calc_length = jCheckBoxCalcLengthWithHeader.isSelected();

        if ("Disconnected".equals(status)) {
            JOptionPane.showMessageDialog(null, "Belum Terkoneksi");
        } else if ("Connected".equals(status)) {
            if (jTextFieldAmount.getText().equals("") || jTextFieldPin.getText().equals("") || jTextFieldNoKartu.getText().equals("") || jTextFieldExpiredDate.getText().equals("") || jTextFieldCvv.getText().equals("") || jTextFieldAcqCode.getText().equals("") || jTextFieldBit55.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "amount, pin, kartu, cvv, expired, chip dan acq code tidak boleh kosong");
            } else {
                try {
                    // Create a new ISO message for each iteration
                    ISOMsg isoMsg = new ISOMsg();
                    isoMsg.setPackager(packager);

                    // Set the specific fields for each message
                    isoMsg.setMTI("0200");
                    isoMsg.set(3, "311000");
                    isoMsg.set(4, jTextFieldAmount.getText());
                    isoMsg.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                    isoMsg.set(11, StringFunction.pad(jTextFieldStan.getText(), 6, "0", "Right"));
                    isoMsg.set(12, StringFunction.getCurrentTimeHHmmss());
                    isoMsg.set(13, StringFunction.getCurrentDateMMDD());
                    isoMsg.set(17, StringFunction.getCurrentDateMMDD());
                    isoMsg.set(22, "051");
                    isoMsg.set(32, "10000000" + jTextFieldAcqCode.getText());//99000000147
                    //6032988900160180=25122201246871100000
                    isoMsg.set(35, jTextFieldNoKartu.getText() + "=" + jTextFieldExpiredDate.getText() + jTextFieldCvv.getText() + jTextFieldDisData.getText());
                    isoMsg.set(37, StringFunction.pad(jTextFieldStan.getText(), 12, "0", "Right"));
                    isoMsg.set(41, jTextFieldTerminalNumber.getText());
                    isoMsg.set(43, "MT                               Jak ID ");
                    isoMsg.set(48, "A                       40000036000000000000");
                    isoMsg.set(49, "360");
                    isoMsg.set(52, jTextFieldPin.getText());//C26ED899905F7BF2
                    isoMsg.set(55, jTextFieldBit55.getText());//820274009F360205C29F26082A1F3E8764227FAE9F101C0101A000000000772B97030000000000000000000000000000000000950580000400009F37042C30491F9F02060000000000009F03060000000000005F3401019F1A0203605F2A0203609A032401179C01309F2701808407A00000060210105A086032988900160180
                    isoMsg.set(60, "THIS IS TERMINAL DATA");

                    // Pack the ISO message into a byte array
                    byte[] isoBytes = isoMsg.pack();
                    length_message = isoBytes.length;

                    if (header_status) {
                        // Set the header
                        header = getHeader();
                        byte[] headerBytes = header.getBytes();

                        if (calc_length) {
                            // Concatenate the header and the ISO message
                            byte[] completeMessage = new byte[headerBytes.length + isoBytes.length];
                            System.arraycopy(headerBytes, 0, completeMessage, 0, headerBytes.length);
                            System.arraycopy(isoBytes, 0, completeMessage, headerBytes.length, isoBytes.length);

                            isoBytes = completeMessage;

                            if (calc_length) {
                                length_message = isoBytes.length;
                            }
                        }
                    }

                    // Print the sent ISO 8583 message
                    String sentIsoMessageString = new String(isoBytes);
                    System.out.println();
                    System.out.println("Sent ISO 8583 request message: " + sentIsoMessageString);
                    System.out.println("Length ISO 8583 message: " + length_message);
                    System.out.println("Outgoing Message Fields:");
                    System.out.println("Header: " + header);
                    System.out.println("MTI: " + isoMsg.getMTI());

                    appendToTextArea("\n");
                    appendToTextArea("Sent ISO 8583 request message: " + sentIsoMessageString + "\n");
                    appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                    appendToTextArea("Outgoing Message Fields:" + "\n");
                    appendToTextArea("Header: " + header + "\n");
                    appendToTextArea("MTI: " + isoMsg.getMTI() + "\n");

                    for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                        if (isoMsg.hasField(i)) {
                            System.out.println("Field " + i + ": " + isoMsg.getString(i));
                            appendToTextArea("Field " + i + ": " + isoMsg.getString(i) + "\n");
                        }
                    }
                    // Send the ISO message to the destination system using the client's socket
                    clientsocket.getOutputStream().write(isoBytes);
                } catch (IOException | ISOException e) {
                    e.printStackTrace();
                }
            }
            jTextFieldStan.setText(String.valueOf(1 + Long.parseLong(jTextFieldStan.getText())));
        }
    }//GEN-LAST:event_jButtonBalInqActionPerformed

    private void jButtonNewKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewKeyActionPerformed
        // TODO add your handling code here:
        header_status = jCheckBoxHeader.isSelected();
        status = jTextFieldStatus.getText();
        calc_length = jCheckBoxCalcLengthWithHeader.isSelected();

        if ("Disconnected".equals(status)) {
            JOptionPane.showMessageDialog(null, "Belum Terkoneksi");
        } else if ("Connected".equals(status)) {
            try {
                // Set the common fields for all messages

                // Create a new ISO message for each iteration
                ISOMsg isoMsg = new ISOMsg();
                isoMsg.setPackager(packager);

                // Set the specific fields for each message
                isoMsg.setMTI("0800");
                isoMsg.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                isoMsg.set(11, StringFunction.pad(jTextFieldStan.getText(), 6, "0", "Right"));
                isoMsg.set(53, "1234567890123456");
                isoMsg.set(70, "162");
                isoMsg.set(120, "736700");
                isoMsg.set(123, "CSM(MCL/KSM RCV/00000000147 ORG/90000000212 KD/67C6FB7784608CDE51BE84A454B7CA05 CTP/00000000000081 )");

                // Pack the ISO message into a byte array
                byte[] isoBytes = isoMsg.pack();
                length_message = isoBytes.length;

                if (header_status) {
                    // Set the header
                    header = getHeader();
                    byte[] headerBytes = header.getBytes();

                    if (calc_length) {
                        // Concatenate the header and the ISO message
                        byte[] completeMessage = new byte[headerBytes.length + isoBytes.length];
                        System.arraycopy(headerBytes, 0, completeMessage, 0, headerBytes.length);
                        System.arraycopy(isoBytes, 0, completeMessage, headerBytes.length, isoBytes.length);

                        isoBytes = completeMessage;

                        if (calc_length) {
                            length_message = isoBytes.length;
                        }
                    }
                }

                // Print the sent ISO 8583 message
                String sentIsoMessageString = new String(isoBytes);
                System.out.println();
                System.out.println("Sent ISO 8583 request message: " + sentIsoMessageString);
                System.out.println("Length ISO 8583 message: " + length_message);
                System.out.println("Outgoing Message Fields:");
                System.out.println("Header: " + header);
                System.out.println("MTI: " + isoMsg.getMTI());

                appendToTextArea("\n");
                appendToTextArea("Sent ISO 8583 request message: " + sentIsoMessageString + "\n");
                appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                appendToTextArea("Outgoing Message Fields:" + "\n");
                appendToTextArea("Header: " + header + "\n");
                appendToTextArea("MTI: " + isoMsg.getMTI() + "\n");

                for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                    if (isoMsg.hasField(i)) {
                        System.out.println("Field " + i + ": " + isoMsg.getString(i));
                        appendToTextArea("Field " + i + ": " + isoMsg.getString(i) + "\n");
                    }
                }
                // Send the ISO message to the destination system using the client's socket
                clientsocket.getOutputStream().write(isoBytes);
            } catch (IOException | ISOException e) {
                e.printStackTrace();
            }
            jTextFieldStan.setText(String.valueOf(1 + Long.parseLong(jTextFieldStan.getText())));
        }
    }//GEN-LAST:event_jButtonNewKeyActionPerformed

    private void jButtonKeyChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonKeyChangeActionPerformed
        // TODO add your handling code here:
        header_status = jCheckBoxHeader.isSelected();
        status = jTextFieldStatus.getText();
        calc_length = jCheckBoxCalcLengthWithHeader.isSelected();

        if ("Disconnected".equals(status)) {
            JOptionPane.showMessageDialog(null, "Belum Terkoneksi");
        } else if ("Connected".equals(status)) {
            try {
                // Set the common fields for all messages

                // Create a new ISO message for each iteration
                ISOMsg isoMsg = new ISOMsg();
                isoMsg.setPackager(packager);

                // Set the specific fields for each message
                isoMsg.setMTI("0800");
                isoMsg.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                isoMsg.set(11, StringFunction.pad(jTextFieldStan.getText(), 6, "0", "Right"));
                isoMsg.set(53, "1234567890123456");
                isoMsg.set(70, "161");
                isoMsg.set(123, "CSM(MCL/RSI RCV/90000000212 ORG/00000000147 SVR/ )");

                // Pack the ISO message into a byte array
                byte[] isoBytes = isoMsg.pack();
                length_message = isoBytes.length;

                if (header_status) {
                    // Set the header
                    header = getHeader();
                    byte[] headerBytes = header.getBytes();

                    if (calc_length) {
                        // Concatenate the header and the ISO message
                        byte[] completeMessage = new byte[headerBytes.length + isoBytes.length];
                        System.arraycopy(headerBytes, 0, completeMessage, 0, headerBytes.length);
                        System.arraycopy(isoBytes, 0, completeMessage, headerBytes.length, isoBytes.length);

                        isoBytes = completeMessage;

                        if (calc_length) {
                            length_message = isoBytes.length;
                        }
                    }
                }

                // Print the sent ISO 8583 message
                String sentIsoMessageString = new String(isoBytes);
                System.out.println();
                System.out.println("Sent ISO 8583 request message: " + sentIsoMessageString);
                System.out.println("Length ISO 8583 message: " + length_message);
                System.out.println("Outgoing Message Fields:");
                System.out.println("Header: " + header);
                System.out.println("MTI: " + isoMsg.getMTI());

                appendToTextArea("\n");
                appendToTextArea("Sent ISO 8583 request message: " + sentIsoMessageString + "\n");
                appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                appendToTextArea("Outgoing Message Fields:" + "\n");
                appendToTextArea("Header: " + header + "\n");
                appendToTextArea("MTI: " + isoMsg.getMTI() + "\n");

                for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                    if (isoMsg.hasField(i)) {
                        System.out.println("Field " + i + ": " + isoMsg.getString(i));
                        appendToTextArea("Field " + i + ": " + isoMsg.getString(i) + "\n");
                    }
                }
                // Send the ISO message to the destination system using the client's socket
                clientsocket.getOutputStream().write(isoBytes);
            } catch (IOException | ISOException e) {
                e.printStackTrace();
            }
            jTextFieldStan.setText(String.valueOf(1 + Long.parseLong(jTextFieldStan.getText())));
        }
    }//GEN-LAST:event_jButtonKeyChangeActionPerformed

    private void jButtonEchoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEchoActionPerformed
        // TODO add your handling code here:
        header_status = jCheckBoxHeader.isSelected();
        status = jTextFieldStatus.getText();
        calc_length = jCheckBoxCalcLengthWithHeader.isSelected();

        if ("Disconnected".equals(status)) {
            JOptionPane.showMessageDialog(null, "Belum Terkoneksi");
        } else if ("Connected".equals(status)) {
            try {
                // Set the common fields for all messages

                // Create a new ISO message for each iteration
                ISOMsg isoMsg = new ISOMsg();
                isoMsg.setPackager(packager);

                // Set the specific fields for each message
                isoMsg.setMTI("0800");
                isoMsg.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                isoMsg.set(11, StringFunction.pad(jTextFieldStan.getText(), 6, "0", "Right"));
                isoMsg.set(70, "301");

                // Pack the ISO message into a byte array
                byte[] isoBytes = isoMsg.pack();
                length_message = isoBytes.length;

                if (header_status) {
                    // Set the header
                    header = getHeader();
                    byte[] headerBytes = header.getBytes();

                    if (calc_length) {
                        // Concatenate the header and the ISO message
                        byte[] completeMessage = new byte[headerBytes.length + isoBytes.length];
                        System.arraycopy(headerBytes, 0, completeMessage, 0, headerBytes.length);
                        System.arraycopy(isoBytes, 0, completeMessage, headerBytes.length, isoBytes.length);

                        isoBytes = completeMessage;

                        if (calc_length) {
                            length_message = isoBytes.length;
                        }
                    }
                }

                // Print the sent ISO 8583 message
                String sentIsoMessageString = new String(isoBytes);
                System.out.println();
                System.out.println("Sent ISO 8583 request message: " + sentIsoMessageString);
                System.out.println("Length ISO 8583 message: " + length_message);
                System.out.println("Outgoing Message Fields:");
                System.out.println("Header: " + header);
                System.out.println("MTI: " + isoMsg.getMTI());

                appendToTextArea("\n");
                appendToTextArea("Sent ISO 8583 request message: " + sentIsoMessageString + "\n");
                appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                appendToTextArea("Outgoing Message Fields:" + "\n");
                appendToTextArea("Header: " + header + "\n");
                appendToTextArea("MTI: " + isoMsg.getMTI() + "\n");

                for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                    if (isoMsg.hasField(i)) {
                        System.out.println("Field " + i + ": " + isoMsg.getString(i));
                        appendToTextArea("Field " + i + ": " + isoMsg.getString(i) + "\n");
                    }
                }
                // Send the ISO message to the destination system using the client's socket
                clientsocket.getOutputStream().write(isoBytes);
            } catch (IOException | ISOException e) {
                e.printStackTrace();
            }
            jTextFieldStan.setText(String.valueOf(1 + Long.parseLong(jTextFieldStan.getText())));
        }
    }//GEN-LAST:event_jButtonEchoActionPerformed

    private void jButtonSignoffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSignoffActionPerformed
        // TODO add your handling code here:
        header_status = jCheckBoxHeader.isSelected();
        status = jTextFieldStatus.getText();
        calc_length = jCheckBoxCalcLengthWithHeader.isSelected();

        if ("Disconnected".equals(status)) {
            JOptionPane.showMessageDialog(null, "Belum Terkoneksi");
        } else if ("Connected".equals(status)) {
            try {
                // Set the common fields for all messages

                // Create a new ISO message for each iteration
                ISOMsg isoMsg = new ISOMsg();
                isoMsg.setPackager(packager);

                // Set the specific fields for each message
                isoMsg.setMTI("0800");
                isoMsg.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                isoMsg.set(11, StringFunction.pad(jTextFieldStan.getText(), 6, "0", "Right"));
                isoMsg.set(70, "002");

                // Pack the ISO message into a byte array
                byte[] isoBytes = isoMsg.pack();

                length_message = isoBytes.length;

                if (header_status) {
                    // Set the header
                    header = getHeader();
                    byte[] headerBytes = header.getBytes();

                    if (calc_length) {
                        // Concatenate the header and the ISO message
                        byte[] completeMessage = new byte[headerBytes.length + isoBytes.length];
                        System.arraycopy(headerBytes, 0, completeMessage, 0, headerBytes.length);
                        System.arraycopy(isoBytes, 0, completeMessage, headerBytes.length, isoBytes.length);

                        isoBytes = completeMessage;

                        if (calc_length) {
                            length_message = isoBytes.length;
                        }
                    }
                }

                // Print the sent ISO 8583 message
                String sentIsoMessageString = new String(isoBytes);
                System.out.println();
                System.out.println("Sent ISO 8583 request message: " + sentIsoMessageString);
                System.out.println("Length ISO 8583 message: " + length_message);
                System.out.println("Outgoing Message Fields:");
                System.out.println("Header: " + header);
                System.out.println("MTI: " + isoMsg.getMTI());

                appendToTextArea("\n");
                appendToTextArea("Sent ISO 8583 request message: " + sentIsoMessageString + "\n");
                appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                appendToTextArea("Outgoing Message Fields:" + "\n");
                appendToTextArea("Header: " + header + "\n");
                appendToTextArea("MTI: " + isoMsg.getMTI() + "\n");

                for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                    if (isoMsg.hasField(i)) {
                        System.out.println("Field " + i + ": " + isoMsg.getString(i));
                        appendToTextArea("Field " + i + ": " + isoMsg.getString(i) + "\n");
                    }
                }
                // Send the ISO message to the destination system using the client's socket
                clientsocket.getOutputStream().write(isoBytes);
            } catch (IOException | ISOException e) {
                e.printStackTrace();
            }
            jTextFieldStan.setText(String.valueOf(1 + Long.parseLong(jTextFieldStan.getText())));
        }
    }//GEN-LAST:event_jButtonSignoffActionPerformed

    private void jButtonSignonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSignonActionPerformed
        // TODO add your handling code here:
        header_status = jCheckBoxHeader.isSelected();
        calc_length = jCheckBoxCalcLengthWithHeader.isSelected();
        status = jTextFieldStatus.getText();
        if ("Disconnected".equals(status)) {
            JOptionPane.showMessageDialog(null, "Belum Terkoneksi");
        } else if ("Connected".equals(status)) {
            try {
                // Create a new ISO message for each iteration
                ISOMsg isoMsg = new ISOMsg();
                isoMsg.setPackager(packager);

                // Set the specific fields for each message
                isoMsg.setMTI("0800");
                isoMsg.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                isoMsg.set(11, StringFunction.pad(jTextFieldStan.getText(), 6, "0", "Right"));
                isoMsg.set(48, "ini adalah tes signon");
                isoMsg.set(70, "001");

                // Pack the ISO message into a byte array
                byte[] isoBytes = isoMsg.pack();

                length_message = isoBytes.length;

                if (header_status) {
                    // Set the header
                    header = getHeader();
                    byte[] headerBytes = header.getBytes();
                    // Concatenate the header and the ISO message
                    byte[] completeMessage = new byte[headerBytes.length + isoBytes.length];
                    System.arraycopy(headerBytes, 0, completeMessage, 0, headerBytes.length);
                    System.arraycopy(isoBytes, 0, completeMessage, headerBytes.length, isoBytes.length);

                    isoBytes = completeMessage;

                    if (calc_length) {
                        length_message = isoBytes.length;
                    }
                }

                // Print the sent ISO 8583 message
                String sentIsoMessageString = new String(isoBytes);
                System.out.println();
                System.out.println("Sent ISO 8583 request message: " + sentIsoMessageString);
                System.out.println("Length ISO 8583 message: " + length_message);
                System.out.println("Outgoing Message Fields:");
                System.out.println("Header: " + getHeader());
                System.out.println("MTI: " + isoMsg.getMTI());

                appendToTextArea("\n");
                appendToTextArea("Sent ISO 8583 request message: " + sentIsoMessageString + "\n");
                appendToTextArea("Length ISO 8583 message: " + length_message + "\n");
                appendToTextArea("Outgoing Message Fields:" + "\n");
                appendToTextArea("Header: " + header + "\n");
                appendToTextArea("MTI: " + isoMsg.getMTI() + "\n");

                for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                    if (isoMsg.hasField(i)) {
                        System.out.println("Field " + i + ": " + isoMsg.getString(i));
                        appendToTextArea("Field " + i + ": " + isoMsg.getString(i) + "\n");
                    }
                }
                clientsocket.getOutputStream().write(isoBytes);
            } catch (IOException | ISOException e) {
                e.printStackTrace();
            }
            jTextFieldStan.setText(String.valueOf(1 + Long.parseLong(jTextFieldStan.getText())));
        }
    }//GEN-LAST:event_jButtonSignonActionPerformed

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectActionPerformed
        status = jTextFieldStatus.getText();
        if ("Server".equals(getSelectedMode())) {
            switch (status) {
                case "Waiting":
                    setclose();
                    stopServerListener();
                    break;
                case "Close":
                    if (getTextFieldPort().equals("")) {
                        JOptionPane.showMessageDialog(null, "Port tidak boleh kosong");
                    } else {
                        create_thread_server();
                    }
                    break;
                case "Connected":
                    stopServerListener();
                    setclose();
                    break;
                default:
                    break;
            }
        } else if ("Client".equals(getSelectedMode())) {
            switch (status) {
                case "Connected":
                    try {
                    clientsocket.close();
                    setdisconnectedclient();
                } catch (IOException ex) {
                    System.out.println("Error closing client socket: " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Error closing client socket: " + ex.getMessage());
                    Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
                case "Disconnected":
                    if (getTextFieldIP().equals("")) {
                        JOptionPane.showMessageDialog(null, "IP tidak boleh kosong");
                    } else {
                        if (getTextFieldPort().equals("")) {
                            JOptionPane.showMessageDialog(null, "Port tidak boleh kosong");
                        } else {
                            connecttoserver();
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }//GEN-LAST:event_jButtonConnectActionPerformed

    private void jTextFieldIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldIPActionPerformed

    private void jRadioButtonClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonClientActionPerformed
        // TODO add your handling code here:
        setclient();
    }//GEN-LAST:event_jRadioButtonClientActionPerformed

    private void jRadioButtonServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonServerActionPerformed
        // TODO add your handling code here:
        setserver();
    }//GEN-LAST:event_jRadioButtonServerActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        if (jComboBox1.getSelectedItem().equals("acq only")) {
            jButtonBalInq.setEnabled(true);
            jButtonWitdraw.setEnabled(true);
            jButtonTrfInq.setEnabled(true);
            jButtonTrf.setEnabled(true);
        } else if (jComboBox1.getSelectedItem().equals("acq iss")) {
            jButtonBalInq.setEnabled(false);
            jButtonWitdraw.setEnabled(false);
            jButtonTrfInq.setEnabled(true);
            jButtonTrf.setEnabled(true);
        } else if (jComboBox1.getSelectedItem().equals("acq bene")) {
            jButtonBalInq.setEnabled(false);
            jButtonWitdraw.setEnabled(false);
            jButtonTrfInq.setEnabled(false);
            jButtonTrf.setEnabled(true);
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jTextFieldHeaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldHeaderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldHeaderActionPerformed

    private void jCheckBoxHeaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxHeaderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxHeaderActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        // Menentukan jalur lengkap ke folder tempat Anda ingin membuat file
        String folderPath = "C:\\opt\\asacgateway\\card\\";

        // Nama file yang ingin Anda buat
        String fileName = "data_card.txt";

        // Data yang ingin Anda tambahkan ke file
        String newData = jTextField_addcard_name.getText() + "," + jTextField_addcard_pin.getText() + "," + jTextField_addcard_pan.getText() + "," + jTextField_addcard_expired.getText() + "," + jTextField_addcard_cvv.getText() + "," + jTextField_addcard_d_data.getText();

        // Menjalankan metode untuk menambahkan data ke dalam file
        appendDataToFile(folderPath, fileName, newData);
    }

    public static void appendDataToFile(String folderPath, String fileName, String newData) {
        try {
            // Membuat objek File
            File file = new File(folderPath + fileName);

            // Membuat folder jika belum ada
            file.getParentFile().mkdirs();

            // Membuat objek FileWriter dalam mode append
            FileWriter fw = new FileWriter(file, true);

            // Menulis data baru di bawah data yang sudah ada
            try ( // Membuat objek BufferedWriter
                     BufferedWriter bw = new BufferedWriter(fw)) {
                // Menulis data baru di bawah data yang sudah ada
                bw.write(newData);
                bw.newLine(); // Memastikan data ditulis pada baris baru
                // Menutup BufferedWriter
            }
            System.out.println("Data berhasil ditambahkan ke dalam file: " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private static String createISOHeader(String messageType, int messageLength) {
        // Assuming a simple header format with messageType and messageLength fields
        StringBuilder headerBuilder = new StringBuilder();

        // Append messageType field (4 characters)
        headerBuilder.append(String.format("%-4s", messageType));

        // Append messageLength field (3 characters)
        headerBuilder.append(String.format("%03d", messageLength));

        // You might add more fields as needed based on your specific requirements
        return headerBuilder.toString();
    }      

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonBalInq;
    private javax.swing.JButton jButtonClearLog;
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JButton jButtonEcho;
    private javax.swing.JButton jButtonKeyChange;
    private javax.swing.JButton jButtonNewKey;
    private javax.swing.JButton jButtonSignoff;
    private javax.swing.JButton jButtonSignon;
    private javax.swing.JButton jButtonTrf;
    private javax.swing.JButton jButtonTrfInq;
    private javax.swing.JButton jButtonWitdraw;
    private javax.swing.JCheckBox jCheckBoxCalcLengthWithHeader;
    private javax.swing.JCheckBox jCheckBoxHeader;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBoxChannel;
    private javax.swing.JComboBox<String> jComboBoxHeaderType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButtonClient;
    private javax.swing.JRadioButton jRadioButtonServer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextFieldAcqCode;
    private javax.swing.JTextField jTextFieldAmount;
    private javax.swing.JTextField jTextFieldAs;
    private javax.swing.JTextArea jTextFieldBit55;
    private javax.swing.JTextField jTextFieldCvv;
    private javax.swing.JTextField jTextFieldDestAccount;
    private javax.swing.JTextField jTextFieldDestCode;
    private javax.swing.JTextField jTextFieldDisData;
    private javax.swing.JTextField jTextFieldExpiredDate;
    private javax.swing.JTextField jTextFieldHeader;
    private javax.swing.JTextField jTextFieldIP;
    private javax.swing.JTextField jTextFieldIssCode;
    private javax.swing.JTextField jTextFieldNoKartu;
    private javax.swing.JTextField jTextFieldPin;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTextField jTextFieldStan;
    private javax.swing.JTextField jTextFieldStatus;
    private javax.swing.JTextField jTextFieldTerminalNumber;
    private javax.swing.JTextField jTextField_addcard_cvv;
    private javax.swing.JTextField jTextField_addcard_d_data;
    private javax.swing.JTextField jTextField_addcard_expired;
    private javax.swing.JTextField jTextField_addcard_name;
    private javax.swing.JTextField jTextField_addcard_pan;
    private javax.swing.JTextField jTextField_addcard_pin;
    // End of variables declaration//GEN-END:variables
}
