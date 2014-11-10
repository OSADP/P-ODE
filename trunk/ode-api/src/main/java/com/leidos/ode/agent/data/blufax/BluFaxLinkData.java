package com.leidos.ode.agent.data.blufax;

import org.tmdd._3.messages.LinkStatusMsg;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/10/14
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class BluFaxLinkData {

    private LinkStatusMsg linkStatusMsg;

    public LinkStatusMsg getLinkStatusMsg() {
        return linkStatusMsg;
    }

    public void setLinkStatusMsg(LinkStatusMsg linkStatusMsg) {
        this.linkStatusMsg = linkStatusMsg;
    }
}
