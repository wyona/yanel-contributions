/*
 * Copyright 2008 Wyona
 */

package org.wyona.foaf.yanel.impl.resources.impfoaf;

import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;

import java.io.ByteArrayInputStream;
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
        return false;
    }

    /**
     * Get import directory which needs to be set within resource configuration
     */
    public String getImportDirectory() {
        try {
            String importDir = getResourceConfigProperty("import-directory");
            if (importDir != null) {
                if (new java.io.File(importDir).isDirectory()) {
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
}
