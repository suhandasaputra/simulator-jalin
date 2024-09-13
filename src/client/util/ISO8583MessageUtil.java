package client.util;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;

public class ISO8583MessageUtil {
    private final ISOPackager packager;

    public ISO8583MessageUtil(ISOPackager packager) {
        this.packager = packager;
    }

    public ISOMsg unpackISO8583Message(byte[] messageBytes) throws ISOException {
        try {
            ISOMsg isoMessage = new ISOMsg();
            isoMessage.setPackager(packager);
            isoMessage.unpack(messageBytes);
            return isoMessage;
        } catch (ISOException e) {
            throw new ISOException("Failed to unpack ISO 8583 message", e);
        }
    }
}
