///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package client.util;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import org.apache.commons.codec.DecoderException;
//import org.apache.commons.codec.binary.Hex;
//import org.apache.log4j.Logger;
//
///**
// *
// * @author PT. MATAJARI MITRA SOLUSI
// */
//public class HeaderMessage {
//
//    private static Logger log = Logger.getLogger(HeaderMessage.class);
//
//    public static byte[] stxGtx(String msg) {
//        return ("\02" + msg + "\03").getBytes();
//    }
//
//    public static byte[] digitHeader(boolean lengthIncl, String msg) {
////        log.info("HeaderMessage : " + StringFunction.pad(String.valueOf(msg.length()), 4, "0", "Right") + "  " + msg);
//        if (lengthIncl) {
//            return (StringFunction.pad(String.valueOf(msg.length() + 4), 4, "0", "Right") + msg).getBytes();
//        } else {
//            return (StringFunction.pad(String.valueOf(msg.length()), 4, "0", "Right") + msg).getBytes();
//        }
//    }
//
//    public static String hexaDigitHeader(boolean lengthIncl, String msg) {
//        try {
//            String hexLength;
//            long length=msg.length();
//            if (lengthIncl) {
//                length = msg.length() + 2;
//            }
////            hexLength = StringFunction.pad(Integer.toHexString(length), 4, "0", "Right");
//            hexLength = StringFunction.pad(Long.toHexString(length), 4, "0", "Right");
////            hexLength = Long.toHexString(length);
//            log.info("sent message with length : " + msg.length());
//            log.info("sent message with hex length : " + hexLength);
//            byte[] bytes = Hex.decodeHex(hexLength.toCharArray());
//            String msgtosend = new String(bytes) + msg;
////            log.info("msg : " + new String(bytes) + msg);
//            log.info("msg : " + msgtosend);
//            log.info("msgtosend : " + msgtosend.length());
////            if (msgtosend.length()>length){
////                msgtosend = msgtosend.substring(2);
////            }
////            log.info("msg 2 : " + msgtosend);
////            return new String(bytes) + msg;
//            return msgtosend;
//
//        } catch (DecoderException ex) {
////            log.error(ex.getMessage());
//            ex.printStackTrace();
//        }
//        return msg;
//    }
//
//    public static byte[] getFinallyMessage(boolean lengthIncl, int headerType, String msg) {
//        if (headerType == 1) {
//            return digitHeader(lengthIncl,msg);
//        } else if (headerType == 2) {
//            return stxGtx(msg);
//        } else if (headerType == 3) {
//            return setHeaderHexaLength(lengthIncl,msg);
//        } else if (headerType == 4) {
//            return setHeaderHexaLength2(lengthIncl,msg);
//        }
//        return null;
//    }
//    
//    public static byte[] setHeaderHexaLength(boolean lengthIncl, String msg){
//        try {
//            long length=msg.length();
//            if (lengthIncl) {
//                length = msg.length() + 2;
//            }
//            String hexLength = StringFunction.pad(Long.toHexString(length), 4, "0", "Right");
//            log.info("sent message with length : " + msg.length());
//            log.info("sent message with hex length : " + hexLength);
//            byte[] bytesLength = Hex.decodeHex(hexLength.toCharArray());
//            byte[] bytesMsg = msg.getBytes();
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            outputStream.write(bytesLength);
//            outputStream.write(bytesMsg);
//            byte[] c = outputStream.toByteArray();
//            return c;
//        } catch (DecoderException ex) {
//            log.error(ex.getMessage());
//        } catch (IOException ex) {
//            log.error(ex.getMessage());
//        }
//        return null;
//    }
//    
//    public static byte[] setHeaderHexaLength2(boolean lengthIncl, String msg){
//        try {
//            long length=msg.length();
//            if (lengthIncl) {
//                length = msg.length() + 2;
//            }
//            String hexLength = StringFunction.pad(Long.toHexString(length), 4, "0", "Right");
//            log.info("sent message with length : " + msg.length());
//            log.info("sent message with hex length : " + hexLength);
//            byte[] bytesLength = Hex.decodeHex(hexLength.toCharArray());
//            byte[] bytesMsg = msg.getBytes();
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            outputStream.write(bytesLength[1]); //mod
//            outputStream.write(bytesLength[0]); //div
//            outputStream.write(bytesMsg);
//            byte[] c = outputStream.toByteArray();
//            return c;
//        } catch (DecoderException ex) {
//            log.error(ex.getMessage());
//        } catch (IOException ex) {
//            log.error(ex.getMessage());
//        }
//        return null;
//    }
//}
