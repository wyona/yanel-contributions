/*
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.resources.konakart.product;

import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.resources.konakart.shared.KonakartOfflineException;
import org.wyona.yanel.resources.konakart.shared.SharedResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.net.URLEncoder;

import org.wyona.yanel.core.attributes.viewable.View;

import javax.servlet.http.HttpServletResponse;

/**
 * A product
 */
public class KonakartProductSOAPInfResource extends BasicXMLResource implements org.wyona.yanel.core.api.attributes.AnnotatableV1 {
    
    private static Logger log = Logger.getLogger(KonakartProductSOAPInfResource.class);

    private static String KONAKART_NAMESPACE = "http://www.konakart.com/1.0";
    
    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(java.lang.String)
     */
    public View getView(String viewId) throws Exception {
        SharedResource shared = new SharedResource();
        if(!shared.isKKOnline(getRealm())) {
            // Konakart is offline
            throw new KonakartOfflineException("Konakart is offline.");
        }
        if (!exists()) {
            throw new org.wyona.yanel.core.ResourceNotFoundException("No such product!");
        }

        // All is well
        return getXMLView(viewId, getContentXML(viewId));
    }

    /**
     * Generate XML of a KonaKart product
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        SharedResource shared = new SharedResource();
        KKEngIf kkEngine = shared.getKonakartEngineImpl();
        int customerId = shared.getTemporaryCustomerId(getEnvironment().getRequest().getSession(true));

        org.w3c.dom.Document doc = null;
        try {
            doc = org.wyona.commons.xml.XMLHelper.createDocument(KONAKART_NAMESPACE, "product");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
        Element rootElement = doc.getDocumentElement();

        int productID = 0;
        try { 
            productID = getProductID(kkEngine);
        } catch(Exception e) {
            // This should have been caught be exists()...
            String message = "No such product: " + productID + ", " + getContentLanguage();
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "exception"));
            exceptionElement.appendChild(doc.createTextNode(message));
            java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
            org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
            return new java.io.ByteArrayInputStream(baout.toByteArray());
        }

        int languageId = shared.getLanguageId(getContentLanguage());
        if (languageId == -1) {
            String message = "No such language: " + getContentLanguage();
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "exception"));
            exceptionElement.appendChild(doc.createTextNode(message));
        } else {
            rootElement.setAttributeNS(KONAKART_NAMESPACE, "language-code", getContentLanguage());
            rootElement.setAttributeNS(KONAKART_NAMESPACE, "language-id", "" + languageId);
            log.debug("Content language: " + getContentLanguage());
        }

        ProductIf product = kkEngine.getProduct(null, productID, languageId);
        if (product != null) {
            Element productElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "product"));
            productElement.setAttributeNS(KONAKART_NAMESPACE, "id", "" + productID);
            productElement.setAttributeNS(KONAKART_NAMESPACE, "language", getContentLanguage());
            productElement.setAttributeNS(KONAKART_NAMESPACE, "language-id", "" + languageId);
            productElement.setAttributeNS(KONAKART_NAMESPACE, "category-id", "" + product.getCategoryId());

            Element imageElem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "image-name"));
            imageElem.appendChild(doc.createTextNode("" + product.getImage()));

            // Units
            Element unitElement = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "units"));
            unitElement.appendChild(doc.createTextNode(shared.getItemUnits(product, getContentLanguage(), true)));

            // Try to parse the tags in the description field
            String sdetail1, sdetail2;
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            try {
                Pattern pdetail1 = Pattern.compile("<detail1>(.*)</detail1>", Pattern.DOTALL);
                Matcher mdetail1 = pdetail1.matcher(product.getDescription());
                mdetail1.find();
                sdetail1 = mdetail1.group(0);
                // Here we parse and import the content of the field. Xalan doesn't let us
                // import a whole document, so we need to import the child nodes individually.
                // So we just loop over all the child nodes and import them...
                Node ndetail1 = (Node) builder.parse(new ByteArrayInputStream(sdetail1.getBytes()));
                NodeList children = ndetail1.getChildNodes();
                for(int i = 0; i < children.getLength(); i++) {
                    productElement.appendChild(doc.importNode(children.item(i), true));
                }
            } catch(Exception e) {
                Element desc1Elem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "detail1"));
                desc1Elem.appendChild(doc.createTextNode("No detail1 field."));
                log.warn(e,e);
            }

            try {
                Pattern pdetail2 = Pattern.compile("<detail2>(.*)</detail2>", Pattern.DOTALL);
                Matcher mdetail2 = pdetail2.matcher(product.getDescription());
                mdetail2.find();
                sdetail2 = mdetail2.group(0);
                // Here we parse and import the content of the field. Xalan doesn't let us
                // import a whole document, so we need to import the child nodes individually.
                // So we just loop over all the child nodes and import them...
                Node ndetail2 = (Node) builder.parse(new ByteArrayInputStream(sdetail2.getBytes()));
                NodeList children = ndetail2.getChildNodes();
                for(int i = 0; i < children.getLength(); i++) {
                    productElement.appendChild(doc.importNode(children.item(i), true));
                }
            } catch(Exception e) {
                Element desc2Elem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "detail2"));
                desc2Elem.appendChild(doc.createTextNode("No detail2 field."));
                log.warn(e,e);
            }

            Element nameElem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "name"));
            nameElem.appendChild(doc.createTextNode("" + product.getName()));
            Element snameElem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "stripped-name"));
            String stripped_name = product.getName().replaceAll("[ /\\\\]+","-");
            snameElem.appendChild(doc.createTextNode(URLEncoder.encode(stripped_name, "UTF-8")));

            BigDecimal price1, price2;

            try {
                price1 = product.getPriceIncTax().setScale(2);
            } catch(Exception e) {
                price1 = product.getPriceIncTax();
            }

            try {
                price2 = product.getSpecialPriceIncTax().setScale(2);
            } catch(Exception e) {
                price2 = product.getSpecialPriceIncTax();
            }

            Element priceIncTaxElem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "price-inc-tax"));
            priceIncTaxElem.appendChild(doc.createTextNode("" + price1));

            Element specialPriceIncTaxElem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "special-price-inc-tax"));
            specialPriceIncTaxElem.appendChild(doc.createTextNode("" + price2));

            try {
                price1 = product.getPriceExTax().setScale(2);
            } catch(Exception e) {
                price1 = product.getPriceExTax();
            }

            try {
                price2 = product.getSpecialPriceExTax().setScale(2);
            } catch(Exception e) {
                price2 = product.getSpecialPriceExTax();
            }

            Element priceExTaxElem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "price-ex-tax"));
            priceExTaxElem.appendChild(doc.createTextNode("" + price1));

            Element specialPriceExTaxElem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "special-price-ex-tax"));
            specialPriceExTaxElem.appendChild(doc.createTextNode("" + price2));

            // Expiry date is only supported by KonaKart 5
            // Element expiryElem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "expiry-date"));
            // expiryElem.appendChild(doc.createTextNode("" + product.getExpiryDate()));

            if (product.getImage4() != null && product.getImage4().length() > 0) {
                Element image4Elem = (Element) productElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "blowup-image-name"));
                image4Elem.appendChild(doc.createTextNode("" + product.getImage4()));
            }
        } else {
            String message = "No such product: " + productID + ", " + getContentLanguage();
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "exception"));
            exceptionElement.appendChild(doc.createTextNode(message));
        }

        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
        org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
        return new java.io.ByteArrayInputStream(baout.toByteArray());
    }

    /**
     * Get product ID
     */
    private int getProductID(KKEngIf eng) throws Exception {
        int productID;
        String productIDStr;
        try {
            // Get product ID in res config
            productIDStr = getResourceConfigProperty("product-id");
            productID = Integer.parseInt(productIDStr);
        } catch(Exception e1) {
            String path = getPath();
            try { 
                // Get product id from basename of path,
                // e.g. /xyz/33.html yields 33 as product id.
                String name = org.wyona.commons.io.PathUtil.getName(path);
                productIDStr = name.substring(0, name.indexOf(".html"));
                productID = Integer.parseInt(productIDStr);
            } catch(Exception e2) {
                // Well if all else failed, try to get product id from 
                // dirname, e.g. /xyz/33/test.html yields 33 as id.
                String parent = org.wyona.commons.io.PathUtil.getParent(path);
                productIDStr = org.wyona.commons.io.PathUtil.getName(parent);
                productID = Integer.parseInt(productIDStr);
            }
        }
        return productID;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        SharedResource shared = new SharedResource();
        if(!shared.isKKOnline(getRealm())) return true;
        int languageId = shared.getLanguageId(getContentLanguage());
        if (languageId == -1) {
            log.error("No such language: " + getContentLanguage());
            return false;
        }

        int productID = 0;
        KKEngIf kkEngine = shared.getKonakartEngineImpl();
        try {
            productID = getProductID(kkEngine);
        } catch(Exception e) {
            return false;
        }

        ProductIf product = kkEngine.getProduct(null, productID, languageId);
        // getStatus is 1 if the product is in stock
        // getInvisible is 1 if the product is invisible
        if (product != null && product.getInvisible() != 1 && product.getStatus() == 1) {
            return true;
        } else {
            log.error("No such product: " + getPath());
            if(product != null) log.error("Product is not in stock or invisible...");
            return false;
        }
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.AnnotatableV1#getAnnotations()
     */
    public String[] getAnnotations() throws Exception {
        SharedResource shared = new SharedResource();
        KKEngIf kkEngine = shared.getKonakartEngineImpl();
        int languageId = shared.getLanguageId(getContentLanguage());
        int productID = getProductID(kkEngine);
        ProductIf product = kkEngine.getProduct(null, productID, languageId);
        String[] annotations = {"shop-product", "" + productID, product.getName()};
        return annotations;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.AnnotatableV1#clearAllAnnotations()
     */
    public void clearAllAnnotations() throws Exception {
        log.warn("No implemented yet!");
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.AnnotatableV1#removeAnnotation(String)
     */
    public void removeAnnotation(String name) throws Exception {
        log.warn("No implemented yet!");
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.AnnotatableV1#setAnnotation(String)
     */
    public void setAnnotation(String name) throws Exception {
        log.warn("No implemented yet!");
    }
}
