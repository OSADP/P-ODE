package com.leidos.ode.agent.parser.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ODEJsoup {

    protected static Document getMessageDocument(byte[] bytes) {
        if (bytes != null) {
            String messageString = new String(bytes);
            return Jsoup.parse(messageString);
        }
        return null;
    }
}
