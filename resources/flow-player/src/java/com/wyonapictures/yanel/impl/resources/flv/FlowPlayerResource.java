/*
 * Copyright 2007 Wyona
 */

package com.wyonapictures.yanel.impl.resources.flv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.WildcardReplacerHelper;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yarep.core.Repository;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Category;

/**
 *
 */
public class FlowPlayerResource extends BasicXMLResource {

    private static Category log = Category.getInstance(FlowPlayerResource.class);

    /**
     *
     */
    public FlowPlayerResource() {
    }

    public View getView(String viewId) throws Exception {
        View view = new View();
        try {
            if (viewId != null && viewId.equals("flashplayer")) {
                view.setInputStream(getFlashPlayerFile());
                view.setMimeType("application/x-shockwave-flash");
            } else {
                return getXMLView(viewId, getContentXML());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return view;
    }

    /**
     *
     */
    public boolean exists() {
        log.warn("Not really implemented yet! Needs to check if events XML exists.");
        return true;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[3];
        try {
            vd[0] = new ViewDescriptor("default");
            vd[0].setMimeType(getMimeType(null));
            vd[1] = new ViewDescriptor("xml");
            vd[1].setMimeType(getMimeType("xml"));
            vd[2] = new ViewDescriptor("flashplayer");
            vd[2].setMimeType("application/x-shockwave-flash");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return vd;
    }

    /**
     *
     */
    private InputStream getContentXML() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", baos);
        IOUtils.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">", baos);
        IOUtils.write("<head>", baos);
        IOUtils.write("<title>" + getResourceConfigProperty("title") + "</title>", baos);
        IOUtils.write("<script type=\"text/javascript\" src=\"" + PathUtil.getResourcesHtdocsPath(this) + "swfobject.js\"></script>", baos);
        IOUtils.write("</head>", baos);
        IOUtils.write("<body>", baos);
        IOUtils.write("<h3>" + getResourceConfigProperty("title") + "</h3>", baos);
        IOUtils.write("<div id=\"flowplayerholder\">", baos);
        IOUtils.write("<a href=\"http://www.macromedia.com/go/getflashplayer\">Get the Flash Player</a> to see this movie.", baos);
        IOUtils.write("</div>", baos);
        IOUtils.write("<script type=\"text/javascript\">", baos);
        //IOUtils.write("// <![CDATA[", baos);
        String width = "468";
        if (getResourceConfigProperty("width") != null) {
            width = getResourceConfigProperty("width");
        }
        String height = "350";
        if (getResourceConfigProperty("height") != null) {
            height = getResourceConfigProperty("height");
        }
        IOUtils.write("var fo = new SWFObject(\"?yanel.resource.viewid=flashplayer\", \"FlowPlayer\", \"" + width + "\", \"" + height +"\", \"7\", \"#000\", true);", baos);

        //IOUtils.write(" // need this next line for local testing, it's optional if your swf is on the same domain as your html page", baos);
        //IOUtils.write("fo.addParam(\"allowScriptAccess\", \"always\");", baos);

        if (getResourceConfigProperty("configFileName") != null) {
            IOUtils.write("fo.addVariable(\"config\", \"{ configFileName: '" + PathUtil.backToRealm(getPath())  + getResourceConfigProperty("configFileName") +"' }\");", baos);
        } else {
            String splashImg = "";
            if (getResourceConfigProperty("splashImg") == null) {
                splashImg = "{ url: '" + PathUtil.getResourcesHtdocsPath(this) + "wyona-pictures.jpg' },";
            }
            if (getResourceConfigProperty("splashImg") != null && !getResourceConfigProperty("splashImg").equals("none")) {
                splashImg = "{ url: '" + PathUtil.backToRealm(getPath()) + getResourceConfigProperty("splashImg") + "' },";
            }

            String flvPath;
            if (getResourceConfigProperty("flvPath") == null) {
                WildcardReplacerHelper dataPath = new WildcardReplacerHelper(getResourceConfigProperty("path-replace-tokens"), getResourceConfigProperty("path-matcher"));
                flvPath = dataPath.getReplacedString(getPath().substring(getPath().lastIndexOf("/") + 1, getPath().length()));
            } else {
                flvPath = getResourceConfigProperty("flvPath");
            }

            String showPlayListButtons = "showPlayListButtons: false, ";
            if (getResourceConfigProperty("showPlayListButtons") != null && getResourceConfigProperty("showPlayListButtons").equals("true")) {
                showPlayListButtons = "showPlayListButtons: true, ";
            }
            String showFullScreenButton = ", showFullScreenButton: false ";
            if (getResourceConfigProperty("showFullScreenButton") != null && getResourceConfigProperty("showFullScreenButton").equals("true")) {
                showFullScreenButton = ", showFullScreenButton: true ";
            }
            String showMenu = ", showMenu: false ";
            if (getResourceConfigProperty("showMenu") != null && getResourceConfigProperty("showMenu").equals("true")) {
                showMenu = ", showMenu: true ";
            }
            String initialScale;
            if (getResourceConfigProperty("initialScale") == null) {
                initialScale = ", initialScale: 'fit' ";
            } else {
                initialScale = ", initialScale: '" + getResourceConfigProperty("initialScale") + "' ";
            }

            IOUtils.write("fo.addVariable(\"config\", \"{ " + showPlayListButtons + "  playList: [  " + splashImg + "  { url: '" + flvPath + "' } ]" + showMenu + showFullScreenButton + initialScale + " }\");", baos);
        }
        IOUtils.write("fo.write(\"flowplayerholder\");", baos);
        //IOUtils.write("// ]]>", baos);
        IOUtils.write("</script>", baos);
        IOUtils.write("</body>", baos);
        IOUtils.write("</html>", baos);
        return new java.io.ByteArrayInputStream(baos.toByteArray());
    }

    private InputStream getFlashPlayerFile() {
        File flowPlayerFile = org.wyona.commons.io.FileUtil.file(this.getRTD().getConfigFile().getParentFile().getAbsolutePath(),  "htdocs" + File.separator + "FlowPlayer.swf");
        try {
            return new FileInputStream(flowPlayerFile);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
