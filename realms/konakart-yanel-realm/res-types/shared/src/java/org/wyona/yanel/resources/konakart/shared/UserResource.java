/*
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.resources.konakart.shared;

import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.resources.konakart.shared.SharedResource;

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
import javax.xml.transform.Transformer;

/**
 * KonaKart user information
 */
public class UserResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(UserResource.class);

    private static String KONAKART_NAMESPACE = "http://www.konakart.com/1.0";
    
    /**
     * Generate XML of the KonaKart checkout process
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        org.w3c.dom.Document doc = null;
        try {
            doc = org.wyona.commons.xml.XMLHelper.createDocument(KONAKART_NAMESPACE, "user");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
        Element rootElement = doc.getDocumentElement();

        SharedResource shared = new SharedResource();

        if(!shared.isKKOnline()) {
            // Konakart is offline.
            Element firstnameElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "offline"));
            java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
            org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
            return new java.io.ByteArrayInputStream(baout.toByteArray());
        }

        KKEngIf kkEngine = shared.getKonakartEngineImpl();
        javax.servlet.http.HttpSession httpSession = getEnvironment().getRequest().getSession(true);
        String konakartSessionID = shared.getSessionId(httpSession);
        int temporaryCustomerID = shared.getTemporaryCustomerId(httpSession);

        rootElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-session-id", konakartSessionID);
        rootElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-temp-customer-id", "" + shared.getTemporaryCustomerId(httpSession));
        int customerID = shared.getCustomerId(httpSession);
        if (customerID > 0) {
            String emailAddr = kkEngine.getCustomer(konakartSessionID).getEmailAddr();
            rootElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-customer-id", "" + customerID);
            rootElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-customer-email", emailAddr);
            com.konakart.appif.CustomerIf customer = kkEngine.getCustomer(konakartSessionID);
            Element firstnameElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "firstname"));
            firstnameElement.appendChild(doc.createTextNode(customer.getFirstName()));
            Element lastnameElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "lastname"));
            lastnameElement.appendChild(doc.createTextNode(customer.getLastName()));
            Element genderElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "gender"));
            genderElement.appendChild(doc.createTextNode(customer.getGender()));
        } else {
            log.debug("Not signed-in yet!");
        }

        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
        org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
        return new java.io.ByteArrayInputStream(baout.toByteArray());
    }

    /**
     * Pass transformer parameters.
     * @see BasicXMLResource
     */
    protected void passTransformerParameters(Transformer transformer) throws Exception {
        super.passTransformerParameters(transformer);
        String orig2realm = (String) getParameters().get("original-back2realm");
        if(orig2realm != null) {
            transformer.setParameter("original-back2realm", orig2realm);
            log.debug("Back to realm of referer: " + orig2realm);
        } else {
            log.warn("Parameter 'original-back2realm' is null (Path: " + getPath() + ")");
        }
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
        if(!shared.isKKOnline()) return true;
        KKEngIf kkEngine = shared.getKonakartEngineImpl();

        int languageID = getLanguageID(kkEngine);
        if (languageID == -1) {
            log.error("No such language: " + getContentLanguage());
            return false;
        }
        return true;
    }
}
