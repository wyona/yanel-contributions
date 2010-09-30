/**
 *
 */
package org.wyona.konakart.model.impl;

import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.impl.repo.orm.ORMRepositoryImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;

import org.apache.log4j.Logger;

/**
 * ORM based KonaKart repository implementation
 */
public class KonakartJDBCRepository extends ORMRepositoryImpl {

    private static Logger log = Logger.getLogger(KonakartJDBCRepository.class);

    /**
     *
     */
    public Node getNode(String path) throws NoSuchNodeException, RepositoryException {
        if (path.equals("/products") || path.equals("/products/")) {
            return new KonakartProductOverviewJDBCNode(this, path, null);
        } else if (path.startsWith("/products/")) {
            return new KonakartProductOverviewJDBCNode(this, path, null).getNode(org.wyona.commons.io.PathUtil.getName(path));
        } else {
            throw new NoSuchNodeException(path);
        }
    }

    /**
     * Get JDBC connection
     */
    public Connection getConnection() throws SQLException {
        return getBasicDataSource().getConnection();
    }

    /**
     * Get language ID (e.g. 'en' is 1 or 'de' is 2, etc.)
     * @param languageCode Language code, e.g. 'en' for english or 'de' for german
     */
    int getLanguageId(String languageCode) {
        String query = "SELECT languages_id FROM languages WHERE code = '" + languageCode + "'";
        log.warn("TODO: Implementation not finished yet!");
        return 1; // en
        //return 2; // de
        //return 4; // fr
    }
}
