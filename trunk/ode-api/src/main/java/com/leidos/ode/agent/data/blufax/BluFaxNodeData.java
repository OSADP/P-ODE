package com.leidos.ode.agent.data.blufax;

import org.tmdd._3.messages.NodeInventoryMsg;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/13/14
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class BluFaxNodeData implements Serializable {

    private NodeInventoryMsg nodeInventoryMsg;

    public NodeInventoryMsg getNodeInventoryMsg() {
        return nodeInventoryMsg;
    }

    public void setNodeInventoryMsg(NodeInventoryMsg nodeInventoryMsg) {
        this.nodeInventoryMsg = nodeInventoryMsg;
    }
}
