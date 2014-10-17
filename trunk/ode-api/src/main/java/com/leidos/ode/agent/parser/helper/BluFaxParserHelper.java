package com.leidos.ode.agent.parser.helper;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/17/14
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class BluFaxParserHelper extends ODEParserHelper {

    private static final String TAG = "VDOTParserHelper";
    private static Logger logger = Logger.getLogger(TAG);
    private static VDOTParserHelper instance;

    public static VDOTParserHelper getInstance() {
        if (instance == null) {
            instance = new VDOTParserHelper();
        }
        return instance;
    }

    @Override
    public ODEHelperResponse parseData(byte[] bytes) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
