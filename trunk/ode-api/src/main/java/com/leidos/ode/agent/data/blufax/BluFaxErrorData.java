package com.leidos.ode.agent.data.blufax;

import org.tmdd._3.messages.ErrorReport;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/13/14
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class BluFaxErrorData implements Serializable {

    private ErrorReport errorReportMsg;

    public ErrorReport getErrorReportMsg() {
        return errorReportMsg;
    }

    public void setErrorReportMsg(ErrorReport errorReportMsg) {
        this.errorReportMsg = errorReportMsg;
    }
}
