/*
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.resources.konakart.checkout;

import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.resources.konakart.shared.SharedResource;
import org.wyona.yanel.servlet.IdentityMap;
import org.wyona.yanel.servlet.YanelServlet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.konakart.app.Basket;
import com.konakart.app.EngineConfig;
import com.konakart.app.KKEng;
import com.konakart.app.ProductSearch;

import com.konakart.appif.BasketIf;
import com.konakart.appif.KKEngIf;
import com.konakart.appif.CategoryIf;
import com.konakart.appif.LanguageIf;
import com.konakart.appif.ManufacturerIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.ProductsIf;
import com.konakart.appif.ProductSearchIf;
import com.konakart.appif.ProductsIf;
import com.konakart.util.KKConstants;

import org.w3c.dom.Element;

import org.wyona.yanel.core.attributes.viewable.View;

import javax.servlet.http.HttpServletResponse;

/**
 * KonaKart checkout process
 */
public class KonakartCheckoutSOAPInfResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(KonakartCheckoutSOAPInfResource.class);

    private static String KONAKART_NAMESPACE = "http://www.konakart.com/1.0";

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(java.lang.String)
     */
    public View getView(String viewId) throws Exception {
        String username = getEnvironment().getRequest().getParameter("username");
        String password = getEnvironment().getRequest().getParameter("password");

        if (username !=  null && password != null) {
            try {
                javax.servlet.http.HttpSession httpSession = getEnvironment().getRequest().getSession(true);

                // INFO: KonaKart authentication
                SharedResource shared = new SharedResource();
                KKEngIf kkEngine = shared.getKonakartEngineImpl();
                String konakartSessionID = shared.getSessionId(httpSession);
                konakartSessionID = shared.login(username, password, getRealm(), httpSession);
                if(konakartSessionID != null && konakartSessionID.length() > 0) {
                    // Get number of products in shopping cart
                    // This is to figure out if we want to redirect
                    // the customer to the delivery page or not.
                    int languageId = shared.getLanguageId(getContentLanguage());
                    int tmpCustomerId = shared.getTemporaryCustomerId(getEnvironment().getRequest().getSession(true));
                    BasketIf[] items = kkEngine.getBasketItemsPerCustomer(null, tmpCustomerId, languageId);

                    // Redirect
                    if(items.length > 0) {
                        View view = new View();
                        view.setResponse(false);
                        HttpServletResponse response = getEnvironment().getResponse();
                        response.setStatus(302);
                        response.setHeader("Location", getResourceConfigProperty("delivery-url"));
                        return view;
                    } else {
                        return getXMLView(viewId, getContentXML(viewId));
                    }
                } else {
                    log.warn("Login failed for user: " + username);
                }
            } catch(Exception e) {
                log.warn(e, e);
            }
            return getXMLView(viewId, getContentXML(viewId));
        } else {
            return getXMLView(viewId, getContentXML(viewId));
        }
    }
    
    /**
     * Generate XML of the KonaKart checkout process
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        org.w3c.dom.Document doc = null;
        try {
            doc = org.wyona.commons.xml.XMLHelper.createDocument(KONAKART_NAMESPACE, "checkout");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
        Element rootElement = doc.getDocumentElement();
        Element customerElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "customer"));

        SharedResource shared = new SharedResource();
        KKEngIf kkEngine = shared.getKonakartEngineImpl();

        javax.servlet.http.HttpSession httpSession = getEnvironment().getRequest().getSession(true);
        String konakartSessionID = shared.getSessionId(httpSession);
        int temporaryCustomerID = shared.getTemporaryCustomerId(httpSession);

        String username = getEnvironment().getRequest().getParameter("username");
        String password = getEnvironment().getRequest().getParameter("password");
        if (username !=  null && password != null) {
            // INFO: The actual login was already done within getView(String)
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "login-failed"));
        } else if (getEnvironment().getRequest().getParameter("logout") != null) {
            Element logoutElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "logout"));

            // INFO: Yanel logout
            org.wyona.yanel.core.map.Realm realm = getRealm();
            IdentityMap identityMap = (IdentityMap)httpSession.getAttribute(YanelServlet.IDENTITY_MAP_KEY);
            if (identityMap != null && identityMap.containsKey(realm.getID())) {
                log.info("Logout from realm: " + realm.getID());
                identityMap.remove(realm.getID());
            }

            // INFO: KonaKart logout
            kkEngine.logout(konakartSessionID);
            konakartSessionID = shared.getSessionId(httpSession);
        } else {
            Element defaultElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "default"));
            defaultElement.appendChild(doc.createTextNode("No action matched!"));
        }

        customerElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-session-id", konakartSessionID);
        customerElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-temp-customer-id", "" + shared.getTemporaryCustomerId(httpSession));
        int customerID = shared.getCustomerId(httpSession);
        if (customerID > 0) {
            String emailAddr = kkEngine.getCustomer(konakartSessionID).getEmailAddr();
            if (emailAddr.equals("customer." + temporaryCustomerID + shared.TMP_EMAIL_SUFFIX)) {
                log.debug("Not signed-in yet (just as temporary user)!");
            } else {
                customerElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-customer-id", "" + customerID);
                customerElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-customer-email", emailAddr);
            }
        } else {
            log.debug("Not signed-in yet!");
        }

        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
        org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
        return new java.io.ByteArrayInputStream(baout.toByteArray());
    }

    /**
     * Get language ID
     */
    private int getLanguageID(KKEngIf eng) throws Exception {
        LanguageIf kkLang = eng.getLanguagePerCode(getContentLanguage());
        int kkLangInt = -1;
        if (kkLang != null) {
            kkLangInt = kkLang.getId();
        } else {
            log.warn("No such language: " + getContentLanguage());
        }
        return kkLangInt;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        SharedResource shared = new SharedResource();
        KKEngIf kkEngine = shared.getKonakartEngineImpl();

        int languageID = getLanguageID(kkEngine);
        if (languageID == -1) {
            log.error("No such language: " + getContentLanguage());
            return false;
        }
        return true;
    }
}
