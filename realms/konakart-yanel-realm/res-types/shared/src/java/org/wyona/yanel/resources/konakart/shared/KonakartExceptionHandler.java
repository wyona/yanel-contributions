/*-
 * Copyright 2011 Wyona
 */

package org.wyona.yanel.resources.konakart.shared;

import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.impl.resources.BasicGenericExceptionHandlerResource;

import javax.servlet.http.HttpServletResponse;

public class KonakartExceptionHandler extends BasicGenericExceptionHandlerResource {

    /**
     * Get view.
     */
    @Override
    public View getView(String viewId) throws Exception {
        View view = new View();
        view.setResponse(false);
        HttpServletResponse response = getEnvironment().getResponse();
        response.sendError(503, "The shop is currently unavailable.");
        return view;
    }
}
