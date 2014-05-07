package com.leidos.ode.core.dao;

import com.leidos.ode.core.rde.model.RDEArchiveInfo;
import com.mongodb.Mongo;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 5/7/14
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class RDEArchiveDAOImpl extends BasicDAO<RDEArchiveInfo, ObjectId> implements RDEArchiveDAO {

    public RDEArchiveDAOImpl(Mongo mongo, Morphia morphia) {
        super(mongo, morphia, "RDEArchive");
    }

    @Override
    public void storeRDEArchiveInfo(RDEArchiveInfo archiveInfo) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RDEArchiveInfo> getArchiveInfoForMsgType(String msgType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
