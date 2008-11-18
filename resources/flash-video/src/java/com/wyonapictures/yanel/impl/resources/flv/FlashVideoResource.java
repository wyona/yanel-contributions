/*
 * Copyright 2007 Wyona
 */

package com.wyonapictures.yanel.impl.resources.flv;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.util.PathUtil;

import org.apache.log4j.Category;

/**
 *
 */
public class FlashVideoResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(FlashVideoResource.class);

    /**
     *
     */
    public FlashVideoResource() {
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
    public String getMimeType(String viewId) {
        if (viewId != null) {
            if (viewId.equals("xml")) return "application/xml";
        }
        return "application/xhtml+xml";
    }

    /**
     *
     */
    public long getSize() {
        log.warn("Not implemented yet!");
        return -1;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[2];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType(getMimeType(null));
        vd[1] = new ViewDescriptor("xml");
        vd[1].setMimeType(getMimeType("xml"));
        return vd;
    }

    /**
     *
     */
    public View getView(String viewId) {
        View view = new View();
        try {
            view.setInputStream(new java.io.StringBufferInputStream(getXHTML().toString()));
            //view.setInputStream(getRealm().getRepository().getNode("/timeline.xhtml").getInputStream());
            view.setMimeType(getMimeType(null));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return view;
    }

    /**
     *
     */
    private StringBuffer getXHTML() throws Exception {
        //java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd yyyy hh:mm:ss z");
        //String todaysDate = sdf.format(new java.util.Date());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd yyyy");
        String todaysDate = sdf.format(new java.util.Date()) + " 00:00:00 GMT";

        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");

        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head>");
        sb.append("<title>" + getResourceConfigProperty("title") + "</title>");
        sb.append("<script type=\"text/javascript\" src=\"" + PathUtil.getResourcesHtdocsPath(this) + "ufo.js\"></script>");
        sb.append("</head>");
        sb.append("<body>");

        sb.append("<h3>" + getResourceConfigProperty("title") + "</h3>");

        sb.append("<p id=\"player1\"><a href=\"http://www.macromedia.com/go/getflashplayer\">Get the Flash Player</a> to see this player.</p>");

        sb.append("<script type=\"text/javascript\">");

        // TODO: Resolve href relative to swf file and html page URL!
        sb.append("var FO = {movie:\"" + PathUtil.getResourcesHtdocsPath(this) + "flvplayer.swf\",width:\"425\",height:\"350\",majorversion:\"7\",build:\"0\",bgcolor:\"#FFFFFF\",allowfullscreen:\"true\", flashvars:\"file=" + getResourceConfigProperty("href") + "&amp;image=" + PathUtil.getResourcesHtdocsPath(this) + "wyona-pictures.jpg\" };");

        sb.append("UFO.create(FO, \"player1\");");

        sb.append("</script>");
        sb.append("</body>");
        sb.append("</html>");
        return sb;
    }
}
