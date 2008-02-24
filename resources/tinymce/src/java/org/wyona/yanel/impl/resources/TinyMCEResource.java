/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;
import org.wyona.yanel.impl.resources.xml.ConfigurableViewDescriptor;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.io.IOUtils;


import sun.util.logging.resources.logging;

/**
 *
 */
public class TinyMCEResource extends ExecutableUsecaseResource {
    
    private static Logger log = Logger.getLogger(TinyMCEResource.class);
    
    private static String PARAMETER_EDIT_PATH = "edit-path";
    private static String PARAMETER_CONTINUE_PATH = "continue-path";

    
    public void execute() throws UsecaseException {
        String editPath = getEditPath();
        String content = request.getParameter(editPath);
        log.error(content);
        try {
            Resource resToEdit = getYanel().getResourceManager().getResource(getEnvironment(), getRealm(), editPath);
            if (ResourceAttributeHelper.hasAttributeImplemented(resToEdit, "Modifiable", "2")) {
                OutputStream os = ((ModifiableV2) resToEdit).getOutputStream();
                IOUtils.write(content, os);
                addInfoMessage("Successfully saved.");
                setParameter(PARAMETER_CONTINUE_PATH, PathUtil.backToRealm(getPath()) + editPath.substring(1)); // allow jelly template to show link to new event
            } else {
                addError("The resource you wanted to edit does not implement VieableV2 and is therefor not editable with this editor.");
                log.error("The resource you wanted to edit does not implement VieableV2 and is therefor not editable with this editor.");
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
            throw new UsecaseException(e.getMessage(), e);
        }
    }
    
    public void cancel() throws UsecaseException {
        addInfoMessage("Cancled.");
        setParameter(PARAMETER_CONTINUE_PATH, PathUtil.backToRealm(getPath()) + getEditPath().substring(1)); // allow jelly template to show link to new event
    }

    public StringBuffer getEditorForm() throws UsecaseException {
        StringBuffer editableDoc = new StringBuffer();
        StringBuffer sb = new StringBuffer();
        String path;
        
        Enumeration parameters = request.getParameterNames();
        if (!parameters.hasMoreElements()) {
            sb.append("no parameter edit-path found in the request. don't know what to edit");
            return sb;
        } else {
            if (request.getParameter(PARAMETER_EDIT_PATH) == null) {
                sb.append("no parameter edit-path found in the request. don't know what to edit");
                return sb;
            } else {
                path = request.getParameter(PARAMETER_EDIT_PATH);
            }
        }
        
        try {
            Resource resToEdit = getYanel().getResourceManager().getResource(getEnvironment(), getRealm(), path);
            if (ResourceAttributeHelper.hasAttributeImplemented(resToEdit, "Modifiable", "2")) {
                
                InputStream is = ((ModifiableV2) resToEdit).getInputStream();
                BufferedReader ib = new BufferedReader(new InputStreamReader(is));
                String temp = ib.readLine();
                while (temp !=null){
                    editableDoc.append (temp);
                    editableDoc.append ("\n");
                    temp = ib.readLine();
                }
            } else {
                log.error("The resource you wanted to edit does not implement VieableV2 and is therefor not editable with this editor.");
                sb.append("The resource you wanted to edit does not implement VieableV2 and is therefor not editable with this editor.");
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
            throw new UsecaseException(e.getMessage(), e);
        }
        
        sb.append("<form method=\"post\" action=\"\">");
        sb.append("<input type=\"hidden\" name=\"" + PARAMETER_EDIT_PATH + "\" value=\"" + request.getParameter(PARAMETER_EDIT_PATH) + "\"/>");
        sb.append("<textarea id=\"" + request.getParameter(PARAMETER_EDIT_PATH) + "\" name=\"" + request.getParameter(PARAMETER_EDIT_PATH) +  "\" rows=\"15\" cols=\"80\" style=\"width: 100%\">");
        sb.append(StringEscapeUtils.escapeXml(editableDoc.toString()));
        sb.append("</textarea>");
        sb.append("<br />");
        sb.append("<input type=\"submit\" name=\"submit\" value=\"Save\" />");
        sb.append("<input type=\"submit\" name=\"cancel\" value=\"Cancel\" />");
        sb.append("</form>");
        return sb;
    }
    
    private String getEditPath() {
        return request.getParameter(PARAMETER_EDIT_PATH);
    }
}
