/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.core.util.PathUtil;

import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

/**
 *
 */
public class TinyMCEResource extends Resource implements ViewableV2{
    private static Category log = Category.getInstance(TinyMCEResource.class);
    private boolean ajaxBrowser = false;
    
    /**
     *
     */
    public TinyMCEResource() {
    }
    
    /**
    *
    */
    public boolean exists() {
        return true;
    }

    /**
    *
    */
    public long getSize() {
        return -1;
    }

    /**
    *
    */
   public String getMimeType(String viewId) {
       if (viewId != null && viewId.equals("source")) return "application/xml";
       return "application/xhtml+xml";
   }
   
   /**
   *
   */
  public ViewDescriptor[] getViewDescriptors() {
      ViewDescriptor[] vd = new ViewDescriptor[2];
      vd[0] = new ViewDescriptor("default");
      vd[0].setMimeType(getMimeType(null));
      vd[1] = new ViewDescriptor("source");
      vd[1].setMimeType(getMimeType("source"));
      return vd;
  }
    
    /**
    *
    */
    public View getView(String viewId) {
        if(request.getHeader("User-Agent").indexOf("rv:1.7") < 0) {
            ajaxBrowser = true;
        }
       
        View view = new View();
        String mimeType = getMimeType(viewId);
        view.setMimeType(mimeType);
        
        if (viewId != null && viewId.equals("source")) {
            view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
            view.setMimeType(getMimeType(viewId));
            return view;
        }
        
        view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
        return view;
    }
    
    /**
     * Flow
     */
    private String getScreen() {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head><title>tinyMCE</title>");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + PathUtil.getResourcesHtdocsPath(this) + "css/tinymce-resource.css\"/>");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-css/progressBar.css\"/>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-js/prototype.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-js/progressBar.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-js/sorttable.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getResourcesHtdocsPath(this)+ "js/ajaxlookup.js\" type=\"text/javascript\"></script>");
        
        
        sb.append("<script language=\"javascript\" type=\"text/javascript\" src=\"" + PathUtil.getResourcesHtdocsPath(this)+ "tinymce/jscripts/tiny_mce/tiny_mce.js\"></script>");
        sb.append("<script language=\"javascript\" type=\"text/javascript\">");
        sb.append("tinyMCE.init({");
        sb.append("    theme : \"advanced\",");
        sb.append("    mode : \"textareas\"");
        sb.append("});");
        sb.append("</script>");
        
        sb.append("</head>");
        sb.append("<body>");

        Enumeration parameters = request.getParameterNames();
        if (!parameters.hasMoreElements()) {
            sb.append(getLookUp());
        } else {
            if (request.getParameter("lookup") != null) {
                return getLookUp().toString();
            }
            if (request.getParameter("edit-path") != null) {
                sb.append(getEditorForm(request.getParameter("edit-path")));
            } else {
                sb.append(getLookUp());
            }
        }

        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
    
    private StringBuffer getEditorForm(String path) {
        StringBuffer editableDoc = new StringBuffer();
        StringBuffer sb = new StringBuffer();
        
        
        try {
            InputStream is = getRealm().getRepository().getNode(path).getInputStream();
            BufferedReader ib = new BufferedReader(new InputStreamReader(is));
            String temp = ib.readLine();
            while (temp !=null){
                editableDoc.append (temp);
                editableDoc.append ("\n");
                temp = ib.readLine();
            }
        } catch (Exception e) {
            sb.append("Exception: " + e);
        }
        String html;
        try {
            String beforeHtml = editableDoc.toString().split("<html.*>")[0];
            String tailedHtml = editableDoc.toString().split("<html.*>")[1];
            html = tailedHtml.toString().split("</html>")[0];
            String afterHtml = tailedHtml.toString().split("</html>")[1];
        } catch (Exception e) {
            sb.append("The document: " + path + "seems not to be an html document. please use another editor.");
            return sb;
        }
        
        
        
        sb.append("<form method=\"post\" action=\"\">");
        sb.append("<textarea id=\"elm1\" name=\"elm1\" rows=\"15\" cols=\"80\" style=\"width: 100%\">");
        sb.append(html);
        sb.append("</textarea>");
        sb.append("<br />");
        sb.append("<input type=\"submit\" name=\"save\" value=\"Submit\" />");
        sb.append("<input type=\"reset\" name=\"reset\" value=\"Reset\" />");
        sb.append("</form>");
        return sb;
    }
    
    private StringBuffer getLookUp() {
        StringBuffer sb = new StringBuffer("");
        Sitetree sitetree = (Sitetree) getYanel().getBeanFactory().getBean("repo-navigation");
        Node node = null;
        String lookinPath = getRequest().getParameter("lookin");
        if (lookinPath != null) {
            node = sitetree.getNode(getRealm(), lookinPath);
        } else {
            node = sitetree.getNode(getRealm(), getPath());
        }
        if (node.isCollection()) {
            if(log.isDebugEnabled()) log.debug("Is Collection: " + node.getName());
        } else if (node.isResource()) {
            if (log.isDebugEnabled()) log.debug("Is Resource: " + node.getName());
            node = node.getParent();
        } else {
            log.error("Neither collection nor resource: " + getPath());
        }
        
        sb.append("<div id=\"lookup\">");
        sb.append("<table id=\"resourceCreatorSaveAsTable\"><tr><td colspan=\"2\">Select a file for editing with tinyMCE</td></tr>");
        sb.append("<tr><td>Look in: " + node.getPath() + "&#160;&#160;&#160;</td><td>");
        
        String parent = "/";
        if (!node.getPath().equals("/")) {
            parent = new org.wyona.commons.io.Path(node.getPath()).getParent().toString();
        }
        if (log.isDebugEnabled()) log.debug("Parent: " + parent);

        if (ajaxBrowser) {
            sb.append("<a href='JavaScript:ajaxlookup(\"" + parent + "\")'><img src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/go-up.png\" alt=\"go up\" border=\"0\"/></a>");
        } else {
            sb.append("<a href=\"?lookin=" + parent + "\"><img src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/go-up.png\" alt=\"go up\" border=\"0\"/></a>");
        }
        sb.append("</td></tr>");

        sb.append("<tr><td colspan=\"2\">");

        sb.append("<div id=\"lookupfiles\">");
        sb.append("<table id=\"lookupfilesTable\" class=\"sortable\">");
        sb.append("<thead>");
        sb.append("<tr><th>Type</th><th>Name</th><th>Resource Type</th></tr>");
        sb.append("</thead>");
        sb.append("<tbody>");
                Node[] children = node.getChildren();
                for (int i = 0; i < children.length; i++) {
                    String resourceTypeName;
                    try {
                        resourceTypeName = yanel.getResourceManager().getResource(getEnvironment(), realm, children[i].getPath()).getResourceTypeLocalName();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        resourceTypeName = "?";
                    }
                    if (children[i].isCollection()) {
                        // TODO: Also append resource specific parameters (AJAX ...)
                        if (ajaxBrowser) {
                            sb.append("<tr><td sorttable_customkey=\"Collection\"><img src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/folder.png\" alt=\"Collection:\"/></td><td><a href='JavaScript:ajaxlookup(\"" + node.getPath() + children[i].getName() + "/\")'>" + children[i].getName() + "</a></td><td>" + resourceTypeName  + "</td></tr>");
                        } else {
                            sb.append("<tr><td sorttable_customkey=\"Collection\"><img src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/folder.png\" alt=\"Collection:\"/></td><td><a href=\"?lookin=" + node.getPath() + children[i].getName() + "/\">" + children[i].getName() + "</a></td><td>" + resourceTypeName  + "</td></tr>");
                        }
                    } else if (children[i].isResource()) {
                sb.append("<tr><td sorttable_customkey=\"Resource\"><img src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/text-x-generic.png\" alt=\"Resource:\"/></td><td><a href=\"?edit-path=" + children[i].getPath() + "\">"+children[i].getName() + "</a></td><td>" + resourceTypeName  + "</td></tr>");
                    } else {
                sb.append("<tr><td>?</td><td>"+children[i].getName()+"</td><td>-</td></tr>");
                    }
                }
        
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</div>");
        sb.append("</td></tr>");
        sb.append("</table>");
        sb.append("</div>");
        
        return sb;
    }    
}
