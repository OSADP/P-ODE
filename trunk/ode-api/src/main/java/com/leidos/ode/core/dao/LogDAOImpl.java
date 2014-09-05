package com.leidos.ode.core.dao;

import com.leidos.ode.logging.LogBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Class to handle the log access to the database.
 *
 * @author lamde
 */
@Component
public class LogDAOImpl implements LogDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void storeLogBean(LogBean logBean) {
        getMongoTemplate().save(logBean);
    }

    private MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
