package com.leidos.ode.core.dao;

import com.leidos.ode.core.rde.data.RDEArchiveInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class to handle the RDE archive information access to the database.
 *
 * @author lamde
 */
@Component
public class RDEArchiveDAOImpl implements RDEArchiveDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    public RDEArchiveDAOImpl() {

    }

    @Override
    public void storeRDEArchiveInfo(RDEArchiveInfo archiveInfo) {
        getMongoTemplate().save(archiveInfo);
    }

    @Override
    public List<RDEArchiveInfo> getArchiveInfoForMsgType(String msgType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
