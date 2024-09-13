package server.util;

/*
 * Created on Dec 15, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Random;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

/**
 * @author PT. MATAJARI MITRA SOLUSI
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class StringFunction {

    private static Logger log = Logger.getLogger(StringFunction.class);
    private static Random rand = new Random();

    public static String pad(String s, int pad, String padString, String position) {
        StringBuffer a = new StringBuffer(pad);
        for (int i = 0; i < pad; i++) {
            a = a.append(padString);
        }
        if (position.equals("Right")) {
            return a.substring(s.length()) + s;
        }
        return s + a.substring(s.length());
    }

    public static String isNull(String text) {
        String result;
        if (text.equals(null)) {
            result = "";
        } else {
            result = text;
        }

        return result;
    }

    public static String getCurrentDateMMDDYYYY() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
        String s = formatter.format(calendar.getTime());
        return s;
    }

    public static String getCurrentDateMMDD() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("MMdd");
        String s = formatter.format(calendar.getTime());
        return s;
    }

    public static String getCurrentDateMMDD(GregorianCalendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMdd");
        String s = formatter.format(calendar.getTime());
        return s;
    }

    public static String getCurrentDateYYYYMMDD() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String s = formatter.format(calendar.getTime());
        return s;
    }

    public static String getCurrentDateYYYYMMDDHHMMSS() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = formatter.format(calendar.getTime());
        return s;
    }
    
    public static String getCurrentYear() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        String s = formatter.format(calendar.getTime());
        calendar = null;
        return s;
    }
    
    public static String getCurrentDateYYYYMMDDHHMMSS(GregorianCalendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = formatter.format(calendar.getTime());
        return s;
    }

    public static String getGMTCurrentDateMMDDHHMMSS() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(GregorianCalendar.HOUR, -7);
        SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss");
        String s = formatter.format(calendar.getTime());
        return s;
    }

    public static String getGMTCurrentDateMMDDHHMMSS(GregorianCalendar calendar) {
        calendar.add(GregorianCalendar.HOUR, -7);
        SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss");
        String s = formatter.format(calendar.getTime());
        return s;
    }

    public static String getCurrentDateForLog() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        String s = formatter.format(calendar.getTime());
        return s;
    }
    
    public static String getCurrentDateYYYYMMDDHHMMSS2() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = formatter.format(calendar.getTime());
        return s;
    }
    
    public static String getCurrentDateYYYYMMDDHHMMSS2(GregorianCalendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = formatter.format(calendar.getTime());
        return s;
    }

    public static String getCurrentDateForLog2() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        String s = formatter.format(calendar.getTime());
        return s;
    }

    public static String getYearMonthForLog() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM");
        String s = formatter.format(calendar.getTime());
        return s;
    }

    public static String getCurrentTimeHHmmss() {
        GregorianCalendar kalender = new GregorianCalendar();
        SimpleDateFormat sFormat = new SimpleDateFormat("HHmmss");
        String jam = sFormat.format(kalender.getTime());
        return jam;
    }

    public static String getCurrentTimeHHmmsss(GregorianCalendar calendar) {
        SimpleDateFormat sFormat = new SimpleDateFormat("HHmmss");
        String jam = sFormat.format(calendar.getTime());
        return jam;
    }

    public static GregorianCalendar convertDateTime(String source) {
        GregorianCalendar cal = new GregorianCalendar();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cal.setTime(formatter.parse(source));
            return cal;
        } catch (ParseException ex) {
            log.error("failed convertDateTime : " + source);
        }
        return cal;
    }

    public static String ZeroFormatter6(int input) {
        DecimalFormat formatter = new DecimalFormat("##000000");
        String output = formatter.format(input);
        return output;
    }

    public static String ZeroFormatter15(int input) {
        DecimalFormat formatter = new DecimalFormat("##000000000000000");
        String output = formatter.format(input);
        return output;
    }

    public static String ZeroFormatter16(int input) {
        DecimalFormat formatter = new DecimalFormat("##0000000000000000");
        String output = formatter.format(input);
        return output;
    }

    public static String ZeroFormatter16(long input) {
        DecimalFormat formatter = new DecimalFormat("##0000000000000000");
        String output = formatter.format(input);
        return output;
    }

    public static String ZeroFormatter12(long input) {
        DecimalFormat formatter = new DecimalFormat("##000000000000");
        String output = formatter.format(input);
        return output;
    }

    public static String ZeroFormatter11(int input) {
        DecimalFormat formatter = new DecimalFormat("##00000000000");
        String output = formatter.format(input);
        return output;
    }

    public static String ZeroFormatter9(int input) {
        DecimalFormat formatter = new DecimalFormat("##000000000");
        String output = formatter.format(input);
        return output;
    }

    public static String ZeroFormatter2(int input) {
        DecimalFormat formatter = new DecimalFormat("##00");
        String output = formatter.format(input);
        return output;
    }

    public static String createDecimal2FromString(String input) {
        return input.substring(0, input.length() - 2) + "." + input.substring(input.length() - 2);
    }

    public static String ZeroFormatter17Dec2(double input) {
        DecimalFormat formatter = new DecimalFormat("##000000000000000.00");
        String output = formatter.format(input);
        return output;
    }

    public static String ZeroFormatterDec2(double input) {
        DecimalFormat formatter = new DecimalFormat("##0.00");
        String output = formatter.format(input);
        return output;
    }

    public static String RemoveDecimalPoint(String input) {
        String hasil = "";
        for (int i = 0; i < input.length(); i++) {
            if (!input.substring(i, i + 1).equals(".")) {
                hasil += input.substring(i, i + 1);
            }
        }
        return hasil;
    }

    public static String encodeHexString(String sourceText) {

        byte[] rawData = sourceText.getBytes();
        StringBuffer hexText = new StringBuffer();
        String initialHex = null;
        int initHexLength = 0;

        for (int i = 0; i < rawData.length; i++) {
            int positiveValue = rawData[i] & 0x000000FF;
            initialHex = Integer.toHexString(positiveValue);
            initHexLength = initialHex.length();
            while (initHexLength++ < 2) {
                hexText.append("");
            }
            hexText.append(initialHex);
        }
        return hexText.toString();
    }

    public static String decodeHexString(String hexText) {

        String decodedText = null;
        String chunk = null;

        if (hexText != null && hexText.length() > 0) {
            int numBytes = hexText.length() / 2;

            byte[] rawToByte = new byte[numBytes];
            int offset = 0;
            for (int i = 0; i < numBytes; i++) {
                chunk = hexText.substring(offset, offset + 2);
                offset += 2;
                rawToByte[i] = (byte) (Integer.parseInt(chunk, 16) & 0x000000FF);
            }
            decodedText = new String(rawToByte);
        }
        return decodedText;
    }

    public static String generateHexFromMsgLength(String msg) {
//        System.out.println("msg.length : " + Integer.toHexString(msg.length()));
        return decodeHexString(pad(Integer.toHexString(msg.length()), 4, "0", "Right"));
    }

    public static String rupiahFormat(long input) {
        DecimalFormat formatter = new DecimalFormat("###,###.##");
        String output = formatter.format(input);
        return output;
    }

    public static String getExtensionFileName(String name) {
        return name.substring(name.length() - 3, name.length()).toUpperCase();
    }

    public static String removeSpecialChar(String txt) {
        return txt.replaceAll("1&", ">");
    }

    public static String randomChar(String alphabet) {
        return String.valueOf(alphabet.charAt(rand.nextInt(alphabet.length())));
    }

    public static int randomNumber(int number) {
        return rand.nextInt(number);
    }

    public static String convertToHexString(String text) {
        StringBuffer shexa = new StringBuffer();
        StringBuffer stext = new StringBuffer();
        StringBuffer sresult = new StringBuffer();
        sresult.append("\n");
        for (int i = 0; i < text.length(); i++) {
            if (i % 20 == 0) {
                sresult.append(shexa.toString());
                sresult.append(pad("", 70 - (shexa.length()), " ", "Right"));
                sresult.append(stext);
                stext.delete(0, stext.toString().length());
                shexa.delete(0, shexa.toString().length());
                sresult.append("\n");
            }
            shexa.append(Hex.encodeHexString(text.substring(i, i + 1).getBytes()));
            shexa.append(" ");
            stext.append(text.substring(i, i + 1));
        }
        sresult.append(shexa.toString());
        sresult.append(pad("", 70 - (shexa.length()), " ", "Right"));
        sresult.append(stext);
        stext.delete(0, stext.toString().length());
        shexa.delete(0, shexa.toString().length());
        sresult.append("\n");
        shexa = null;
        stext = null;
        return sresult.toString();
    }
}
