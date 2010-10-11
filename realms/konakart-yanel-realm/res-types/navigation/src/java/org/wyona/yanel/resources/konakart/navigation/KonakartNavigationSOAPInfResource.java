/*-
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.resources.konakart.navigation;

import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.resources.konakart.shared.SharedResource;

import org.apache.log4j.Logger;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import com.konakart.appif.BasketIf;
import com.konakart.appif.KKEngIf;
import com.konakart.appif.CategoryIf;
import com.konakart.appif.LanguageIf;
import com.konakart.appif.ManufacturerIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.ProductsIf;
import com.konakart.appif.ProductSearchIf;
import com.konakart.appif.ProductsIf;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.transform.Transformer;

/**
 * KonaKart navigation
 */
public class KonakartNavigationSOAPInfResource extends BasicXMLResource {
    private static Logger log = Logger.getLogger(KonakartNavigationSOAPInfResource.class);
    private static String KONAKART_NAMESPACE = "http://www.konakart.com/1.0";
    
    /**
     * Generate XML of a KonaKart navigation
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        SharedResource shared = new SharedResource();
        KKEngIf kkEngine = shared.getKonakartEngineImpl();

        // Create W3C DOM document
        Document doc = null;
        try {
            doc = org.wyona.commons.xml.XMLHelper.createDocument(KONAKART_NAMESPACE, "navigation");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
        Element rootElement = doc.getDocumentElement();

        if(!shared.isKKOnline()) {
            // Konakart is offline...
            rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "offline"));
            java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
            org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
            return new java.io.ByteArrayInputStream(baout.toByteArray());
        }

        int languageId = shared.getLanguageId(getContentLanguage());
        int customerId = shared.getTemporaryCustomerId(getEnvironment().getRequest().getSession(true));
        String sessionId = shared.getSessionId(getEnvironment().getRequest().getSession(true));

        // Get category
        int categoryId = getCategoryId(kkEngine);
        log.debug("Category ID: " + categoryId);

        if (languageId == -1) {
            // No such language...
            String message = "No such language: " + getContentLanguage();
            log.error(message);
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "exception"));
            exceptionElement.appendChild(doc.createTextNode(message));
        } else {
            // Show language code, id number
            rootElement.setAttributeNS(KONAKART_NAMESPACE, "language-code", getContentLanguage());
            rootElement.setAttributeNS(KONAKART_NAMESPACE, "language-id", "" + languageId);
            log.debug("Content language: " + getContentLanguage());
        }

        // Print category tree
        CategoryIf[] categories = kkEngine.getCategoryTree(languageId, true);
        if (categories != null) {
            appendCategories(categories, rootElement, categoryId);
        } else {
            // No categories found...
            String message = "No categories for language: " + getContentLanguage();
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "exception"));
            exceptionElement.appendChild(doc.createTextNode(message));
        }

        // Return stream
        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
        org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
        return new java.io.ByteArrayInputStream(baout.toByteArray());
    }

    /**
     * Get category ID of active category.
     * Return -1 if no category is currently active.
     */
    private int getCategoryId(KKEngIf eng) throws Exception {
        String catid = (String) getParameters().get("category-id");
        if(catid == null) catid = getEnvironment().getRequest().getParameter("category-id");

        try {
            return Integer.parseInt(catid);
        } catch(Exception e) {
            return -1;
        }
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
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        SharedResource shared = new SharedResource();

        if(!shared.isKKOnline()) return false;

        int languageId = shared.getLanguageId(getContentLanguage());

        if (languageId == -1) {
            log.error("No such language: " + getContentLanguage());
            return false;
        }

        return true;
    }

    /**
     * Append categories
     * @param categories Categories to append
     * @param element Element to which categories shall be appended
     * @param selectedCategoryID Selected category ID
     */
    private boolean appendCategories(CategoryIf[] categories, Element element, int selectedCategoryID) throws Exception {
        boolean selected = false;
        Document doc = element.getOwnerDocument();

        // Check if there's anything to append
        if (categories != null && categories.length > 0) {
            Element categoriesElement = (Element) element.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "categories"));

            // Loop over all categories to append
            for (int i = 0; i < categories.length; i++) {
                Element categoryElement = (Element) categoriesElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "category"));
                categoryElement.setAttributeNS(KONAKART_NAMESPACE, "id", "" + categories[i].getId());

                // Is this category selected/active?
                if (categories[i].getId() == selectedCategoryID) {
                    selected = true;
                    log.debug("The category " + selectedCategoryID + " is selected.");
                    categoryElement.setAttributeNS(KONAKART_NAMESPACE, "selected", "true");
                }
                Element nameElement = (Element) categoryElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "name"));
                nameElement.appendChild(doc.createTextNode(categories[i].getName()));

                // Apend child categories
                CategoryIf[] childCategories = categories[i].getChildren();
                boolean child_selected = appendCategories(childCategories, categoryElement, selectedCategoryID);
                if(child_selected) {
                    // If child is selected, then we are...
                    selected = true;
                    categoryElement.setAttributeNS(KONAKART_NAMESPACE, "selected", "true");
                    log.debug("The category " + categories[i].getId() + " has selected child categories.");
                }
            }
        } else {
            log.debug("No (sub-)categories.");
        }

        return selected;
    }
}
