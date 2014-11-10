package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.parser.ODEDataParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/10/14
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class WXDEParser extends ODEDataParser {

    private final String WXDE_TAG = "wxde";

    @Override
    protected ODEDataParserResponse parse(byte[] bytes) {
        Document document = getMessageDocument(bytes);
        if (document != null) {
            Element body = document.body();
            if (body != null) {
                Elements bodyElements = body.getElementsByTag(WXDE_TAG);

            }
        }
        return null;
    }
}
