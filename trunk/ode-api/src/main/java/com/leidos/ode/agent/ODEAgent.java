package com.leidos.ode.agent;

import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/7/14
 * Time: 5:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ODEAgent {

    public void startUp() throws DataTargetException;

    public void processMessage(byte[] messageBytes);
}
