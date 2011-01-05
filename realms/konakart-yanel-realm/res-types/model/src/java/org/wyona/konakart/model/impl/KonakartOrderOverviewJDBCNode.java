package org.wyona.konakart.model.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import org.wyona.yarep.core.Map;
import org.wyona.yarep.core.NoSuchRevisionException;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.NodeStateException;
import org.wyona.yarep.core.NodeType;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Path;
import org.wyona.yarep.core.Property;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Revision;
import org.wyona.yarep.core.Storage;
import org.wyona.yarep.core.UID;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Yarep node representing a KonaKart order overview using JDBC to retrieve data
 */
public class KonakartOrderOverviewJDBCNode extends org.wyona.yarep.impl.AbstractNode {
    private static Logger log = Logger.getLogger(KonakartOrderOverviewJDBCNode.class);
    
    public KonakartOrderOverviewJDBCNode(Repository repo, String path, String uuid) throws RepositoryException {
        super(repo, path, uuid);
        properties = new java.util.HashMap();
    }

    /**
     * @see org.wyona.yarep.core.Node#getNode(String)
     */
    public Node getNode(String idLang) throws NoSuchNodeException, RepositoryException {
        String[] idLanguage = idLang.split("_");
        String orderID = idLanguage[0];
        if (idLanguage.length < 2) {
            throw new RepositoryException("No language within node name: " + idLang);
        }
        String languageCode = idLanguage[1];
        String orderPath = path + "/" + orderID;
        String query = "SELECT * FROM orders WHERE orders_id = '" + orderID + "'";
        try {
            Connection con = ((KonakartJDBCRepository)repository).getConnection();
            Statement stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stm.executeQuery(query);
            if(resultSet.last() && resultSet.getRow() == 1) {
                String id = resultSet.getString("orders_id");
                if (log.isDebugEnabled()) {
                    log.debug("Order ID: " + id);
                    log.debug("Column count: " + resultSet.getMetaData().getColumnCount());
                }
                return new KonakartOrderJDBCNode(repository, orderPath, null);
            } else {
                log.warn("No such order: " + orderID + " (" + orderPath + ")");
            }
            resultSet.close();
            stm.close();
            con.close();
            throw new NoSuchNodeException(path);
        } catch(SQLException e) {
            log.error(e, e);
            throw new RepositoryException(e.getMessage());
        }
    }

    /**
     * @see org.wyona.yarep.core.Node#getIteratorOfNodes()
     */
    public Iterator getIteratorOfNodes() throws RepositoryException {
        log.error("Not implemented yet!"); // TODO
        return null;
    }

    /**
     * @see org.wyona.yarep.core.Node#getNodes()
     */
    public Node[] getNodes() throws RepositoryException {
        String query = "SELECT orders_id FROM orders";
        try {
            Connection con = ((KonakartJDBCRepository)repository).getConnection();
            Statement stm = con.createStatement();
            ResultSet resultSet = stm.executeQuery(query);
            java.util.List<KonakartOrderJDBCNode> nodes = new java.util.ArrayList<KonakartOrderJDBCNode>();
            while(resultSet.next()) {
                String id = resultSet.getString("orders_id");
                if (log.isDebugEnabled()) {
                    log.debug("Order ID: " + id);
                    log.debug("Column count: " + resultSet.getMetaData().getColumnCount());
                }
                nodes.add(new KonakartOrderJDBCNode(repository, path + "/" + id, null));
            }
            resultSet.close();
            stm.close();
            con.close();
            return nodes.toArray(new KonakartOrderJDBCNode[nodes.size()]);
        } catch(SQLException e) {
            log.error(e, e);
            throw new RepositoryException(e.getMessage());
        }
    }

    /**
     * @see org.wyona.yarep.core.Node#hasNode(String)
     */
    public boolean hasNode(String idLang) throws RepositoryException {
        String[] idLanguage = idLang.split("_");
        String orderID = idLanguage[0];
        String languageCode = idLanguage[1];
        log.warn("DEBUG: ID = " + orderID + ", Language = " + languageCode);
        int languageID = ((KonakartJDBCRepository)repository).getLanguageId(languageCode);
        String query = "SELECT * FROM orders WHERE orders_id = '" + orderID + "'";
        log.warn("DEBUG: Query: " + query);
        try {
            Connection con = ((KonakartJDBCRepository)repository).getConnection();
            Statement stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stm.executeQuery(query);
            boolean hasNode = false;
            if(resultSet.last() && resultSet.getRow() == 1) {
                String id = resultSet.getString("orders_id");
                if (log.isDebugEnabled()) {
                    log.debug("Order ID: " + id);
                    log.debug("Column count: " + resultSet.getMetaData().getColumnCount());
                }
                hasNode = true;
            } else {
                log.warn("No such order: ID = " + orderID + ", Language = " + languageCode);
                hasNode = false;
            }
            resultSet.close();
            stm.close();
            con.close();
            return hasNode;
        } catch(SQLException e) {
            log.error(e, e);
            throw new RepositoryException(e.getMessage());
        }
    }
    
    public Node addNode(String name, int type) throws RepositoryException {
        log.warn("TODO: Not implemented yet!");
        return null;
    }
    
    public void removeProperty(String name) throws RepositoryException {
        log.warn("TODO: Not implemented yet!");
    }
    
    /**
     * @see org.wyona.yarep.core.Node#setProperty(Property)
     */
    public void setProperty(Property property) throws RepositoryException {
        //log.debug("Property: " + property.getName() + ", " + property.getValueAsString());
        properties.put(property.getName(), property);
    }

    public InputStream getInputStream() throws RepositoryException {
        log.warn("TODO: Not implemented yet!");
        return null;
    }
    
    public OutputStream getOutputStream() throws RepositoryException {
        log.warn("TODO: Not implemented yet!");
        return null;
    }
    
    public Revision checkin() throws NodeStateException, RepositoryException {
        log.warn("TODO: Not implemented yet!");
        return null;
    }
    
    public Revision checkin(String comment) throws NodeStateException, RepositoryException {
        log.warn("TODO: Not implemented yet!");
        return null;
    }

    public void cancelCheckout() throws NodeStateException, RepositoryException {
        log.warn("TODO: Not implemented yet!");
    }

    public void checkout(String userID) throws NodeStateException, RepositoryException {
        log.warn("TODO: Not implemented yet!");
    }
    
    public void restore(String revisionName) throws NoSuchRevisionException, RepositoryException {
        log.warn("TODO: Not implemented yet!");
    }

    public void delete() throws RepositoryException {
        log.warn("TODO: Not implemented yet!");
    }
}
