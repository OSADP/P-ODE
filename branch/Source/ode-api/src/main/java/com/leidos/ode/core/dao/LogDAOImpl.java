package com.leidos.ode.core.dao;

import com.leidos.ode.logging.LogBean;
import com.mongodb.Mongo;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 5/7/14
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogDAOImpl extends BasicDAO<LogBean, ObjectId> implements LogDAO {

    public LogDAOImpl(Mongo mongo, Morphia morphia) {
        super(mongo, morphia, "LogDB");
    }

}
