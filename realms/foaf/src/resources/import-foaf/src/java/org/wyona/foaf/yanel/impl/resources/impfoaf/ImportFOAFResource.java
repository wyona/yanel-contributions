/*
 * Copyright 2008 Wyona
 */

package org.wyona.foaf.yanel.impl.resources.impfoaf;

import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;

import org.wyona.yarep.core.Node;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;


/**
 * A simple usecase which is based on ExecutableUsecaseResource
 */
public class ImportFOAFResource extends ExecutableUsecaseResource {
    
    private static Logger log = Logger.getLogger(ImportFOAFResource.class);
    
    
    /*
     * This method is executed when submitting the form provided in the default view (probably implemented as a jelly template).
     */
    public void execute() throws UsecaseException {
        doImportFOAFFiles();
    }

    /*
     * This method is executed when canceling the form provided in the default view (probably implemented as a jelly template).
     */
    public void cancel() throws UsecaseException {

    }
    
    /*
     * Implement some test which are tested before the usecase will e executed
     */
    public boolean checkPreconditions() throws UsecaseException {
        return true;
    }

    /**
     * Get import directory which needs to be set within resource configuration
     */
    public String getImportDirectory() {
        try {
            String importDir = getResourceConfigProperty("import-directory");
            if (importDir != null) {
                if (new File(importDir).isDirectory()) {
                    return importDir;
                } else {
                    return "No such directory: " + importDir;
                }
            } else {
                return "No import directory has been set!";
            }
        } catch(Exception e) {
            log.error(e, e);
            return "Exception: " + e.getMessage();
        }
    }

    /**
     *
     */
    private void doImportFOAFFiles() {
        File importDir = new File(getImportDirectory());
        File[] filesAndDirs = importDir.listFiles();
        for (int i = 0; i < filesAndDirs.length; i++) {
            if (filesAndDirs[i].isFile()) {
                log.error("DEBUG: Import File: " + filesAndDirs[i].getAbsolutePath());
                try {
                    Node newFOAFNode = null;
                    org.wyona.yarep.core.Repository repo = ((org.wyona.yanel.impl.map.FOAFRealm) getRealm()).getProfilesRepository();
                    if (repo.existsNode("/profiles/" + filesAndDirs[i].getName())) {
                        newFOAFNode = repo.getNode("/profiles/" + filesAndDirs[i].getName());
                        log.warn("New node created: " + newFOAFNode.getPath());
                    } else {
                        newFOAFNode = repo.getNode("/profiles").addNode(filesAndDirs[i].getName(), org.wyona.yarep.core.NodeType.RESOURCE);
                    }
                    newFOAFNode.setMimeType("application/xml");

                    byte buffer[] = new byte[8192];
                    int bytesRead;
                    java.io.FileInputStream in = new java.io.FileInputStream(filesAndDirs[i].getAbsolutePath());
                    java.io.OutputStream out = newFOAFNode.getOutputStream();
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.close();
                    in.close(); 
                } catch(Exception e) {
                    log.error(e, e);
                }
            }
        }
    }
}
