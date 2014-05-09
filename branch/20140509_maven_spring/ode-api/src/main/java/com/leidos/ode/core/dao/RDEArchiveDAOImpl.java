package com.leidos.ode.core.dao;

import com.leidos.ode.core.rde.model.RDEArchiveInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 5/7/14
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class RDEArchiveDAOImpl implements RDEArchiveDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    public RDEArchiveDAOImpl(){

    }

    @Override
    public void storeRDEArchiveInfo(RDEArchiveInfo archiveInfo) {
        mongoTemplate.save(archiveInfo);
    }

    @Override
    public List<RDEArchiveInfo> getArchiveInfoForMsgType(String msgType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
