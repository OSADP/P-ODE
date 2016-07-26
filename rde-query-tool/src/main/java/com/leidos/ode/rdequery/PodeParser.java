package com.leidos.ode.rdequery;

import com.leidos.ode.data.PodeDataDelivery;
import org.bn.CoderFactory;
import org.bn.IDecoder;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;

/**
 * Parse the PodeDataDelivery out of the string provided by the RDE.
 */
public class PodeParser {

    /**
     * Parse a P-ODE data delivery out of the HEX encoded string. Returns null if formatting is
     * incorrect or if other errors occur.
     * @param data The string containing a HEX encoded PodeDataDelivery
     * @return The {@link PodeDataDelivery} object itself, or null
     */
    public static PodeDataDelivery parsePodeDataDelivery(String data) {
        try {
            byte[] bytes = DatatypeConverter.parseHexBinary(data.replaceAll(" ", ""));
            IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            PodeDataDelivery message = decoder.decode(bis, PodeDataDelivery.class);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
