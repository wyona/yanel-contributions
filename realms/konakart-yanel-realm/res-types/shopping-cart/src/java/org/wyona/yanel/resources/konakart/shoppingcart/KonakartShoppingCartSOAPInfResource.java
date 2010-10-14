/*-
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.resources.konakart.shoppingcart;

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
import com.konakart.appif.OrderIf;
import com.konakart.appif.ShippingQuoteIf;
import com.konakart.appif.CustomerRegistrationIf;
import com.konakart.util.KKConstants;
import com.konakart.app.CreateOrderOptions;
import com.konakart.appif.CreateOrderOptionsIf;

import java.lang.Integer;
import java.math.BigDecimal;
import javax.xml.transform.Transformer;
import java.net.URLEncoder;

import org.w3c.dom.Element;

/**
 * KonaKart shopping cart
 */
public class KonakartShoppingCartSOAPInfResource extends BasicXMLResource {
    private static Logger log = Logger.getLogger(KonakartShoppingCartSOAPInfResource.class);
    private static String KONAKART_NAMESPACE = "http://www.konakart.com/1.0";
    
    /**
     * Generate XML of the KonaKart shopping cart
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        // Get shared resource
        SharedResource shared = new SharedResource();
        KKEngIf kkEngine = shared.getKonakartEngineImpl();

        // Build document
        org.w3c.dom.Document doc = null;

        try {
            doc = org.wyona.commons.xml.XMLHelper.createDocument(KONAKART_NAMESPACE, "shopping-cart");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }

        // Is KK down?
        if(!shared.isKKOnline(getRealm())) {
            // Konakart is offline...
            // Simply return an empty shopping cart.
            Element rootElement = doc.getDocumentElement();
            rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "no-items-inside-shopping-cart-yet"));
            java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
            org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
            return new java.io.ByteArrayInputStream(baout.toByteArray());
        }

        // Get language id, customer, basket, etc.
        int languageId = shared.getLanguageId(getContentLanguage());
        int tmpCustomerId = shared.getTemporaryCustomerId(getEnvironment().getRequest().getSession(true));
        String sessionId = shared.getSessionId(getEnvironment().getRequest().getSession(true));
        BasketIf[] items = kkEngine.getBasketItemsPerCustomer(null, tmpCustomerId, languageId);

        // Should we clear the basket?
        if(getParameters().get("clear") != null) {
            kkEngine.removeBasketItemsPerCustomer(null, tmpCustomerId);
            Element rootElement = doc.getDocumentElement();
            rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "no-items-inside-shopping-cart-yet"));
            java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
            org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
            return new java.io.ByteArrayInputStream(baout.toByteArray());
        }

        // Handle parameters
        // We only handle parameters if explicitly requested... This is because the shopping
        // cart can be included on a single page multiple times, e.g. in the header or in
        // the sidebar, but we don't want both instances to handle the parameters or we'll 
        // end up with incorrect amounts of products in the cart end we don't like that :-)
        // There are two ways to make this resource handle parameters: Set the property
        // 'handle_params' in the yanel resource config, or pass the parameter 'handle_params'
        // to the resource through the request.
        String handle_params = getResourceConfigProperty("handle_params");
        if(handle_params == null) handle_params = getEnvironment().getRequest().getParameter("handle_params");

        if(handle_params != null) {
            // Add product to basket if requested
            String productId = getEnvironment().getRequest().getParameter("product");
    
            if (productId != null) {
                int quantity = 1;
                String amountPara = getEnvironment().getRequest().getParameter("amount");
    
                if (amountPara != null) {
                    try {
                        quantity = new Integer(amountPara).intValue();
                    } catch(Exception e) {
                        // Ignore invalid values
                        quantity = 0;
                    }
                }
    
                if(quantity > 0) {
                    // Don't add negative quantities to cart
                    addToShoppingCart(new Integer(productId).intValue(), tmpCustomerId, quantity, kkEngine);
                }
            }
    
            // Remove product from basket if requested
            String deleteProduct = getEnvironment().getRequest().getParameter("delete");
    
            if(deleteProduct != null) {
                try {
                    int prod_id = new Integer(deleteProduct).intValue();
                    removeIdFromShoppingCart(prod_id, null, tmpCustomerId, languageId, kkEngine);
                } catch(Exception e) {
                    // Could not remove product from basket...
                    log.error(e, e);
                }
            }
    
            // Adjust amounts if needed
            int number = 0, new_amount;
            String stepper = getEnvironment().getRequest().getParameter("stepper");
            String value = getEnvironment().getRequest().getParameter("value");
    
            if(stepper != null && value != null) {
                try {
                    int num = new Integer(stepper.substring(7)).intValue();
                    int val = new Integer(value).intValue();
    
                    if(val <= 0) {
                        removeNumFromShoppingCart(num, null, tmpCustomerId, languageId, kkEngine);
                    } else if(val > 0) {
                        addToShoppingCart(items[num].getProductId(), tmpCustomerId, val - items[num].getQuantity(), kkEngine);
                    }
    
                    // Update basket
                    items = kkEngine.getBasketItemsPerCustomer(null, tmpCustomerId, languageId);
                } catch(Exception e) {
                    // Most likely the parameters are missing or the input
                    // is invalid/malformed or otherwise incorrect. Ignore.
                    log.warn(e,e);
                }
            }
        }



        // <shopping-cart> (root)
        Element rootElement = doc.getDocumentElement();

        if (languageId == -1) {
            String message = "No such language: " + getContentLanguage();
            log.error(message);

            // <exception>
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "exception"));
            exceptionElement.appendChild(doc.createTextNode(message));
        } else {
            // <language-code>, <language-id>
            rootElement.setAttributeNS(KONAKART_NAMESPACE, "language-code", getContentLanguage());
            rootElement.setAttributeNS(KONAKART_NAMESPACE, "language-id", "" + languageId);
            log.debug("Content language: " + getContentLanguage());
        }

        javax.servlet.http.HttpSession httpSession = getEnvironment().getRequest().getSession(true);
        Element customerElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "customer"));

        // If user is logged in, get customer id, email, etc.
        if(sessionId != null) {
            customerElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-session-id", sessionId);

            int customerId = shared.getCustomerId(httpSession);
            String emailAddr = kkEngine.getCustomer(sessionId).getEmailAddr();

            if (customerId > 0) {
                // Customer ids > 0 are persistent, customer ids < 0 are temporary
                customerElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-customer-id", "" + customerId);
                customerElement.setAttributeNS(KONAKART_NAMESPACE, "konakart-customer-email", emailAddr);
            }
        }

        BigDecimal all_price, single_price;
        items = kkEngine.getBasketItemsPerCustomer(null, tmpCustomerId, languageId);

        // Generate XML output
        if (items != null && items.length > 0) {
            // <shopping-cart>
            Element shoppingCartElement = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "shopping-cart"));
            Element itemsElement = (Element) shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "items"));

            // <items>
            for (int i = 0; i < items.length; i++) {
                // <item>
                Element itemElement = (Element) itemsElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "item"));

                // Attributes
                itemElement.setAttributeNS(KONAKART_NAMESPACE, "id", "" + items[i].getProductId());
                itemElement.setAttributeNS(KONAKART_NAMESPACE, "quantity", "" + items[i].getQuantity());
                itemElement.setAttributeNS(KONAKART_NAMESPACE, "number", "" + i);

                // <name>
                Element nameElement = (Element) itemElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "name"));
                nameElement.appendChild(doc.createTextNode(items[i].getProduct().getName()));
                Element snameElement = (Element) itemElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "stripped-name"));
                String stripped_name = items[i].getProduct().getName().replaceAll("[ /\\\\]+","-");
                snameElement.appendChild(doc.createTextNode(URLEncoder.encode(stripped_name, "UTF-8")));

                // <image-name>
                Element imgElement = (Element) itemElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "image-name"));
                imgElement.appendChild(doc.createTextNode(items[i].getProduct().getImage3()));

                // <price-ex-tax>, <price-ex-tax-all>
                try {
                    // We try to set the scale in order to avoid outputting
                    // values like 5.00000000 (because 5.00 looks nicer)
                    all_price = items[i].getFinalPriceExTax().setScale(2);
                    single_price = items[i].getProduct().getPriceExTax().setScale(2);
                } catch(Exception e) {
                    // If if fails (e.g. if BigDecimal is 5.001) then we 
                    // output the exact value (rather than rounding)
                    all_price = items[i].getFinalPriceExTax();
                    single_price = items[i].getProduct().getPriceExTax();
                }

                Element priceExTaxElement = (Element) itemElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "price-ex-tax"));
                priceExTaxElement.appendChild(doc.createTextNode("" + single_price));
                Element priceExTaxAllElement = (Element) itemElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "price-ex-tax-all"));
                priceExTaxAllElement.appendChild(doc.createTextNode("" + all_price));

                // <price-inc-tax>, <price-inc-tax-all>
                try {
                    // We try to set the scale in order to avoid outputting
                    // values like 5.00000000 (because 5.00 looks nicer)
                    all_price = items[i].getFinalPriceIncTax().setScale(2);
                    single_price = items[i].getProduct().getPriceIncTax().setScale(2);
                } catch(Exception e) {
                    // If if fails (e.g. if BigDecimal is 5.001) then we 
                    // output the exact value (rather than rounding)
                    all_price = items[i].getFinalPriceIncTax();
                    single_price = items[i].getProduct().getPriceIncTax();
                }

                Element priceIncTaxElement = (Element) itemElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "price-inc-tax"));
                priceIncTaxElement.appendChild(doc.createTextNode("" + single_price)); 
                Element priceIncTaxAllElement = (Element) itemElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "price-inc-tax-all"));
                priceIncTaxAllElement.appendChild(doc.createTextNode("" + all_price));

                // <special-price-ex-tax>
                try {
                    // We try to set the scale in order to avoid outputting
                    // values like 5.00000000 (because 5.00 looks nicer)
                    single_price = items[i].getProduct().getSpecialPriceExTax().setScale(2);
                } catch(Exception e) {
                    // If if fails (e.g. if BigDecimal is 5.001) then we 
                    // output the exact value (rather than rounding)
                    single_price = items[i].getProduct().getSpecialPriceExTax();
                }

                Element specialPriceExTaxElement = (Element) itemElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "special-price-ex-tax"));
                specialPriceExTaxElement.appendChild(doc.createTextNode("" + single_price));

                // <special-price-inc-tax>
                try {
                    // We try to set the scale in order to avoid outputting
                    // values like 5.00000000 (because 5.00 looks nicer)
                    single_price = items[i].getProduct().getSpecialPriceIncTax().setScale(2);
                } catch(Exception e) {
                    // If if fails (e.g. if BigDecimal is 5.001) then we 
                    // output the exact value (rather than rounding)
                    single_price = items[i].getProduct().getSpecialPriceIncTax();
                }

                Element specialPriceIncTaxElement = (Element) itemElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "special-price-inc-tax"));
            }

            // Get order totals
            CreateOrderOptionsIf orderOptions = new CreateOrderOptions();
            orderOptions.setUseDefaultCustomer(sessionId == null);

            OrderIf order = kkEngine.createOrderWithOptions(sessionId, items, orderOptions, languageId);
            order = kkEngine.getOrderTotals(order, languageId);

            // <total-price-ex-tax>
            Element totalPriceExTaxElement = (Element) shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "total-price-ex-tax"));
            try {
                totalPriceExTaxElement.appendChild(doc.createTextNode("" + order.getSubTotalExTax().setScale(2)));
            } catch(Exception e) {
                totalPriceExTaxElement.appendChild(doc.createTextNode("" + order.getSubTotalExTax()));
            }

            // <total-price-inc-tax>
            Element totalPriceIncTaxElement = (Element) shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "total-price-inc-tax"));
            try {
                totalPriceIncTaxElement.appendChild(doc.createTextNode("" + order.getSubTotalIncTax().setScale(2)));
            } catch(Exception e) {
                totalPriceIncTaxElement.appendChild(doc.createTextNode("" + order.getSubTotalIncTax()));
            }

            // <shipping-price-ex-rebate>
            ShippingQuoteIf shipping;

            try {
                shipping = shared.getShippingCost(items, sessionId, languageId);
            } catch(Exception e) {
                // Order is empty or module is broken
                shipping = null;
            }

            if(shipping != null) {
                // A return value of null means: This module
                // is not available (enable in konakartadmin)
                order.setShippingQuote(shipping);
                order = kkEngine.getOrderTotals(order, languageId);

                try {
                    single_price = shipping.getTotalExTax().setScale(2);
                    all_price = order.getTotalExTax().setScale(2);
                } catch(Exception e) {
                    single_price = shipping.getTotalExTax();
                    all_price = order.getTotalExTax();
                }
    
                Element shippingCost = (Element) shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "shipping-price-ex-rebate"));
                shippingCost.appendChild(doc.createTextNode("" + single_price));

                // Custom shipping cost
                Element shippingCustom1 = (Element) shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "shipping-custom1"));
                shippingCustom1.appendChild(doc.createTextNode("" + shipping.getCustom1()));

                Element shippingCustom2 = (Element) shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "shipping-custom2"));
                shippingCustom2.appendChild(doc.createTextNode("" + shipping.getCustom2()));
                
                Element shippingCustom3 = (Element) shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "shipping-custom3"));
                shippingCustom3.appendChild(doc.createTextNode("" + shipping.getCustom3()));

                // <total-shipping-price-ex-tax>
                Element totalPlusShippingIncTax = (Element) shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "total-shipping-price-ex-tax"));
                totalPlusShippingIncTax.appendChild(doc.createTextNode("" + all_price));
    
                // <shipping-price-inc-rebate>
                try {
                    single_price = shipping.getTotalIncTax().setScale(2);
                    all_price = order.getTotalIncTax().setScale(2);
                } catch(Exception e) {
                    single_price = shipping.getTotalIncTax();
                    all_price = order.getTotalIncTax();
                }
                all_price = all_price.add(single_price).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    
                Element shippingCostIncTax = (Element) shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "shipping-price-inc-rebate"));
                shippingCostIncTax.appendChild(doc.createTextNode("" + single_price));
    
                // <total-shipping-price-inc-rebate>
                Element totalPlusShipping = (Element) shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "total-shipping-price-inc-rebate"));
                totalPlusShipping.appendChild(doc.createTextNode("" + all_price));
            } else {
                // <shipping-module-not-available>
                // TODO: This is not very good, do something more useful...
                shoppingCartElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "shipping-module-not-available"));
                log.error("Fatal: Shipping module is broken or not available!");
            }
        } else {
            // <no-items-inside-shopping-cart-yet>
            rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "no-items-inside-shopping-cart-yet"));
        }

        // Send output stream
        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
        org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
        return new java.io.ByteArrayInputStream(baout.toByteArray());
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

        return true;
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
        String hide_checkout = (String) getParameters().get("hide-checkout");
        if(hide_checkout != null && "true".equalsIgnoreCase(hide_checkout)) {
            transformer.setParameter("hide-checkout", hide_checkout);
        }
    }

    /**
     * Add product to shopping cart
     * @param productId Product ID
     * @param tmpCustomerId Temporary customer ID
     * @param quantity Quantity of product
     * @param kkEngine Konakart engine
     */
    private void addToShoppingCart(int productId, int tmpCustomerId, int quantity, KKEngIf kkEngine) throws Exception {
        BasketIf item = new Basket();
        item.setProductId(productId);
        item.setQuantity(quantity);

        // INFO: Do not use session Id, but only temporary customer Id, because as soon as 
        // customer signs in the session Id will change, but the temporary customer Id remains
        kkEngine.addToBasket(null, tmpCustomerId, item); 
    }

    /**
     * Remove from shopping cart given position.
     * @param number Product no. in basket
     * @param sessionId Session ID
     * @param customerId Customer ID
     * @param languageId Language ID
     * @param kkEngine Konakart engine
     */
    private void removeNumFromShoppingCart(int number, String sessionId, int customerId, int languageId, KKEngIf kkEngine) throws Exception {
        BasketIf[] items = kkEngine.getBasketItemsPerCustomer(sessionId, customerId, languageId);
        kkEngine.removeFromBasket(sessionId, customerId, items[number]);
    }

    /**
     * Remove from shopping cart given product id.
     * @param id Product id in basket
     * @param sessionId Session ID
     * @param customerId Customer ID
     * @param languageId Language ID
     * @param kkEngine Konakart engine
     */
    private void removeIdFromShoppingCart(int id, String sessionId, int customerId, int languageId, KKEngIf kkEngine) throws Exception {
        BasketIf[] items = kkEngine.getBasketItemsPerCustomer(sessionId, customerId, languageId);

        for(BasketIf item : items) {
            if(item.getProductId() == id) {
                kkEngine.removeFromBasket(sessionId, customerId, item);
                return;
            }
        }
    }

}
