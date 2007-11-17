/*
 * Copyright 2007 Wyona
 */

package org.wyona.yanel.impl.resources.cortado;

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
public class OggTheoraResource extends BasicXMLResource {

    private static Category log = Category.getInstance(OggTheoraResource.class);

    /**
     *
     */
    public OggTheoraResource() {
    }

    public View getView(String viewId) throws Exception {
        View view = new View();
        try {
/*            if (viewId != null && viewId.equals("cortadojar")) {
                view.setInputStream(getCortadoJar());
                view.setMimeType("application/java-archive");
            } else {*/
                return getXMLView(viewId, getContentXML());
            //}
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return view;
    }

    /**
     *
     */
    public boolean exists() {
        log.warn("Not really implemented yet!");
        return true;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[2];
        try {
            vd[0] = new ViewDescriptor("default");
            vd[0].setMimeType(getMimeType(null));
            vd[1] = new ViewDescriptor("xml");
            vd[1].setMimeType(getMimeType("xml"));
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
        IOUtils.write("</head>", baos);
        IOUtils.write("<body>", baos);
        IOUtils.write("<h3>" + getResourceConfigProperty("title") + "</h3>", baos);

        String width = "368";
        if (getResourceConfigProperty("width") != null) {
            width = getResourceConfigProperty("width");
        }
        String height = "288";
        if (getResourceConfigProperty("height") != null) {
            height = getResourceConfigProperty("height");
        }

        WildcardReplacerHelper dataPath = new WildcardReplacerHelper(getResourceConfigProperty("oggPath"), getResourceConfigProperty("path-matcher"));
        String oggPath = dataPath.getReplacedString(getPath());


        IOUtils.write("<object id=\"cortado\" classid=\"clsid:08B0E5C0-4FCB-11CF-AAA5-00401C608501\" width=\"" + width + "\" height=\"" + height + "\" align=\"baseline\" onerror=\"objectLoadError ();\">", baos);
        IOUtils.write("<param name=\"code\" value=\"com.fluendo.player.Cortado.class\"/>", baos);
        //IOUtils.write("<param name=\"codebase\" value=\".\"/>", baos);
        IOUtils.write("<param name=\"archive\" value=\"" + PathUtil.getResourcesHtdocsPath(this) + "cortado-ovt-stripped-0.2.2.jar\"/>", baos);
        IOUtils.write("<param name=\"bufferSize\" value=\"500\"/>", baos);
        IOUtils.write("<param name=\"statusHeight\" value=\"16\"/>", baos);
        IOUtils.write("<param name=\"url\" value=\"" + oggPath + "\"/>", baos);
        IOUtils.write("<param name=\"duration\" value=\"255.454\"/>", baos);
        IOUtils.write("<param name=\"video\" value=\"true\"/>", baos);
        IOUtils.write("<param name=\"seekable\" value=\"true\"/>", baos);
        IOUtils.write("<param name=\"audio\" value=\"true\"/>", baos);
        IOUtils.write("<comment>", baos);
        //IOUtils.write("<embed type=\"application/x-java-applet\" width=\"" + width + "\" height=\"" + height + "\" align=\"baseline\" code=\"com.fluendo.player.Cortado.class\" archive=\"?yanel.resource.viewid=cortadojar\" codebase=\".\" bufferSize=\"500\" statusHeight=\"16\" url=\"" + oggPath + "\" duration=\"255.454\" video=\"true\" seekable=\"true\" audio=\"true\">", baos);
        IOUtils.write("<embed type=\"application/x-java-applet\" width=\"" + width + "\" height=\"" + height + "\" align=\"baseline\" code=\"com.fluendo.player.Cortado.class\" archive=\"" + PathUtil.getResourcesHtdocsPath(this) + "cortado-ovt-stripped-0.2.2.jar\" codebase=\".\" bufferSize=\"500\" statusHeight=\"16\" url=\"" + oggPath + "\" duration=\"255.454\" video=\"true\" seekable=\"true\" audio=\"true\">", baos);
        IOUtils.write("<noembed>You need Java to view this media file.</noembed>", baos);
        IOUtils.write("</embed>", baos);
        IOUtils.write("</comment>", baos);
        IOUtils.write("</object>", baos);

        IOUtils.write("</body>", baos);
        IOUtils.write("</html>", baos);
        return new java.io.ByteArrayInputStream(baos.toByteArray());
    }

/*    private InputStream getCortadoJar() {
        File flowPlayerFile = org.wyona.commons.io.FileUtil.file(this.getRTD().getConfigFile().getParentFile().getAbsolutePath(),  "htdocs" + File.separator + "cortado-ovt-stripped-0.2.2.jar");
        try {
            return new FileInputStream(flowPlayerFile);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }*/
}
