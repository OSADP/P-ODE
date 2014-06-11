/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.collector.datasource;

/**
 *
 * @author cassadyja
 */
public class DataSourceException extends Exception{

    DataSourceException(String string) {
        super(string);
    }
    
    DataSourceException(String string, Throwable e) {
        super(string,e);
    }
    
}
