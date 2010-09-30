/*
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.resources.konakart.payment;

import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.resources.konakart.shared.SharedResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.konakart.appif.KKEngIf;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.AddressIf;
import com.konakart.appif.BasketIf;
import com.konakart.appif.OrderIf;
import com.konakart.appif.ShippingQuoteIf;

import org.w3c.dom.Element;

import org.wyona.yanel.core.attributes.viewable.View;
import javax.servlet.http.HttpServletResponse;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.Character;

/**
 * Ask for payment information
 */
public class KonakartPaymentSOAPInfResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(KonakartPaymentSOAPInfResource.class);
    private static String KONAKART_NAMESPACE = "http://www.konakart.com/1.0";
    private boolean redirect;

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(java.lang.String)
     */
    public View getView(String viewId) throws Exception {
        View v = super.getView(viewId);
        if(redirect) {
            View view = new View();
            view.setResponse(false);
            HttpServletResponse response = getEnvironment().getResponse();
            response.setStatus(302);
            response.setHeader("Location", getResourceConfigProperty("overview-url"));
            return view;
        }
        return v;
    }

    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        redirect = false;
        SharedResource shared = new SharedResource();
        KKEngIf kkEngine = shared.getKonakartEngineImpl();
        String sessionId = shared.getSessionId(getEnvironment().getRequest().getSession(true));
        int customerId = shared.getCustomerId(getEnvironment().getRequest().getSession(true));
        int languageId = shared.getLanguageId(getContentLanguage());

        // Build document
        org.w3c.dom.Document doc = null;
        try {
            doc = org.wyona.commons.xml.XMLHelper.createDocument(KONAKART_NAMESPACE, "payment");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }

        // Root element
        Element rootElement = doc.getDocumentElement();

        // Get customer
        CustomerIf customer = shared.getCustomer(sessionId, customerId); 

        if(customer == null) {
            Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "not-logged-in-yet"));
            java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
            org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
            return new java.io.ByteArrayInputStream(baout.toByteArray());
        } 

        // Get payment (=default) address
        AddressIf defaddr = kkEngine.getDefaultAddressPerCustomer(sessionId);
    
        if(defaddr == null) {
            // Well, there's no default address - just make the first address
            // we have on the database the default address from now on (if there's
            // no default address this is most likely a newly registered user).
            AddressIf[] addrs = customer.getAddresses();
            // There has to be at least one address, Konakart doesn't let you register otherwise.
            defaddr = addrs[0];
            kkEngine.setDefaultAddressPerCustomer(sessionId, defaddr.getId());
        }
    
        // Now print that address information
        Element defAddrElem = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "payment-address"));
        Element defAddrNameElem = (Element) defAddrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "last-name"));
        defAddrNameElem.appendChild(doc.createTextNode("" + defaddr.getLastName())); 
        Element defAddrfNameElem = (Element) defAddrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "first-name"));
        defAddrfNameElem.appendChild(doc.createTextNode("" + defaddr.getFirstName())); 
        Element defAddrCompanyElem = (Element) defAddrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "company"));
        defAddrCompanyElem.appendChild(doc.createTextNode("" + defaddr.getCompany())); 
        Element defAddrCityElem = (Element) defAddrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "city"));
        defAddrCityElem.appendChild(doc.createTextNode("" + defaddr.getCity())); 
        Element defAddrCountryElem = (Element) defAddrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "country"));
        defAddrCountryElem.appendChild(doc.createTextNode("" + defaddr.getCountryName())); 
        Element defAddrStreetElem = (Element) defAddrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "street"));
        defAddrStreetElem.appendChild(doc.createTextNode("" + defaddr.getStreetAddress())); 
        Element defAddrGenderElem = (Element) defAddrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "gender"));
        defAddrGenderElem.appendChild(doc.createTextNode("" + defaddr.getGender())); 
        Element defAddrPostcodeElem = (Element) defAddrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "postcode"));
        defAddrPostcodeElem.appendChild(doc.createTextNode("" + defaddr.getPostcode())); 
        
        // If the process parameter is set,
        // we process the form and proceed/redirect
        String process = getEnvironment().getRequest().getParameter("process");
        if(process != null) {
            // Should we redirect?
            redirect = true;

            javax.servlet.http.HttpServletRequest req = getEnvironment().getRequest();
            String payment_method = req.getParameter("payment_method");
            
            if("creditcard".equalsIgnoreCase(payment_method)) {
                String val;
                String ccname = null, ccnumber = null, cccode = null, ccvalid = null, cctype = null;
                boolean valid = true;

                val = req.getParameter("ccname");
                if(val != null && val.length() > 0) {
                    ccname = val; 
                    appendField("ccname", val, rootElement, doc);
                } else {
                    valid = false;
                    appendErr("ccname", rootElement, doc);
                }

                val = req.getParameter("cctype");
                if(val != null && val.length() > 0) {
                    cctype = val;
                    appendField("cctype", val, rootElement, doc);
                } else {
                    valid = false;
                    appendErr("cctype", rootElement, doc);
                }

                val = req.getParameter("ccnumber");
                if(val != null && val.length() > 0) {
                    appendField("ccnumber", val, rootElement, doc);
                    if(checkCreditCardNumber(val)) {
                        ccnumber = val.replaceAll("[ -]+", "");
                    } else {
                        valid = false;
                        appendErr("ccnumber.invalid", rootElement, doc);
                    }
                } else {
                    valid = false;
                    appendErr("ccnumber", rootElement, doc);
                }

                val = req.getParameter("cccode");
                if(val != null && val.length() > 0) {
                    // We don't echo the CVC code for security reasons
                    if(val.matches("[0-9]{3,4}")) {
                        cccode = val;
                    } else {
                        appendErr("cccode.invalid", rootElement, doc);
                    }
                } else {
                    valid = false;
                    appendErr("cccode", rootElement, doc);
                }

                val = req.getParameter("ccvalid_month");
                if(val != null && val.length() > 0) {
                    if(val.matches("[0-9]{1,2}")) {
                        ccvalid = val;
                    } else {
                        appendErr("ccvalid_month.invalid", rootElement, doc);
                    }
                    appendField("ccvalid_month", val, rootElement, doc);
                } else {
                    ccvalid = "";
                    valid = false;
                    appendErr("ccvalid_month", rootElement, doc);
                }

                val = req.getParameter("ccvalid_year");
                if(val != null && val.length() > 0) {
                    if(val.matches("[0-9]{2,4}")) {
                        ccvalid = ccvalid + "-" + val;
                    } else {
                        appendErr("ccvalid_year.invalid", rootElement, doc);
                    }
                    appendField("ccvalid_year", val, rootElement, doc);
                } else {
                    valid = false;
                    appendErr("ccvalid_year", rootElement, doc);
                }

                req.getSession(true).removeAttribute("checkout-data-remarks");
                val = req.getParameter("remarks");
                if(val != null && val.length() > 0) {
                    req.getSession(true).setAttribute("checkout-data-remarks", val);
                    appendField("remarks", val, rootElement, doc);
                } else {
                   String remarks = (String) getEnvironment().getRequest().getSession(true).getAttribute("checkout-data-remarks");
                   if(remarks != null) appendField("remarks", remarks, rootElement, doc);
                }

                if(valid) {
                    // We store the credit card data only
                    // in the session, not within Konakart,
                    // to make sure it doesn't get stored.
                    javax.servlet.http.HttpSession session = getEnvironment().getRequest().getSession(true);
                    session.setAttribute("checkout-card-data-name", ccname);
                    session.setAttribute("checkout-card-data-type", cctype);
                    session.setAttribute("checkout-card-data-number", ccnumber);
                    session.setAttribute("checkout-card-data-cvc", cccode);
                    session.setAttribute("checkout-card-data-valid", ccvalid);
                } else {
                    redirect = false;
                    appendErr("cc", rootElement, doc);
                }
            } else {
                String val;
                String pcname = null, pcnumber = null, pcvalid = "";
                boolean valid = true;

                val = req.getParameter("pcname");
                if(val != null && val.length() > 0) {
                    pcname = val; 
                    appendField("pcname", val, rootElement, doc);
                } else {
                    valid = false;
                    appendErr("pcname", rootElement, doc);
                }

                val = req.getParameter("pcnumber");
                if(val != null && val.length() > 0) {
                    String stripped = val.replaceAll("[ -]+", "");
                    if(stripped.matches("[0-9]{10,11}")) {
                        pcnumber = stripped;
                    } else {
                        valid = false;
                        appendErr("pcnumber.invalid", rootElement, doc);
                    }
                    appendField("pcnumber", val, rootElement, doc);
                } else {
                    valid = false;
                    appendErr("pcnumber", rootElement, doc);
                }

                val = req.getParameter("pcvalid_month");
                if(val != null && val.length() > 0) {
                    if(val.matches("[0-9]{1,2}")) {
                        pcvalid = val;
                    } else {
                        valid = false;
                        appendErr("pcvalid_month.invalid", rootElement, doc);
                    }
                    appendField("pcvalid_month", val, rootElement, doc);
                } else {
                    valid = false;
                    appendErr("pcvalid", rootElement, doc);
                }

                val = req.getParameter("pcvalid_year");
                if(val != null && val.length() > 0) {
                    if(val.matches("[0-9]{2,4}")) {
                        pcvalid = pcvalid + "-" + val;
                    } else {
                        valid = false;
                        appendErr("pcvalid_year.invalid", rootElement, doc);
                    }
                    appendField("pcvalid_year", val, rootElement, doc);
                } else {
                    valid = false;
                    appendErr("pcvalid", rootElement, doc);
                }

                req.getSession(true).removeAttribute("checkout-data-remarks");
                val = req.getParameter("remarks");
                if(val != null && val.length() > 0) {
                    req.getSession(true).setAttribute("checkout-data-remarks", val);
                    appendField("remarks", val, rootElement, doc);
                } else {
                   String remarks = (String) getEnvironment().getRequest().getSession(true).getAttribute("checkout-data-remarks");
                   if(remarks != null) appendField("remarks", remarks, rootElement, doc);
                }

                if(valid) { 
                    javax.servlet.http.HttpSession session = getEnvironment().getRequest().getSession(true);
                    session.setAttribute("checkout-card-data-name", pcname);
                    session.setAttribute("checkout-card-data-type", "Pluscard");
                    session.setAttribute("checkout-card-data-number", pcnumber);
                    session.setAttribute("checkout-card-data-valid", pcvalid);
                } else {
                    redirect = false;
                    appendErr("pc", rootElement, doc);
                }
            }
        } else {
            String remarks = (String) getEnvironment().getRequest().getSession(true).getAttribute("checkout-data-remarks");
            if(remarks != null) appendField("remarks", remarks, rootElement, doc);
        }

        // Output
        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
        org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
        return new java.io.ByteArrayInputStream(baout.toByteArray());
    }
    
    /**
     * Append error element for given field.
     */
    public void appendErr(String field, Element elem, org.w3c.dom.Document doc) {
        Element err = (Element) elem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "error"));
        err.setAttribute("id", field);
    }

    /**
     * Append field to XML output.
     */
    public void appendField(String field, String val, Element elem, org.w3c.dom.Document doc) {
        Element err = (Element) elem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "field"));
        err.setAttribute("id", field);
        err.appendChild(doc.createTextNode(val));
    }

    /**
     * Verify credit card number.
     * This function verifies if a string passed to it is a valid
     * credit card number using regular expressions and the Luhn algorithm.
     * See https://www.paypalobjects.com/en_US/vhelp/paypalmanager_help/credit_card_numbers.htm
     * for a couple of test numbers that can be used for this.
     */
    public boolean checkCreditCardNumber(String number) {
        // Strip all spaces and dashes, make sure
        // the credit card number is 13-19 digits long.
        log.debug("Performing Luhn test");
        try {
            String num = number.replaceAll("[ -]+", "");
            if(num.matches("[0-9]{13,19}")) {
                int total = 0;
                boolean alt = false;
                
                // Look at every digit in the number
                for(int i = num.length()-1; i >= 0; --i) {
                    int d = Character.digit(num.charAt(i), 10);
    
                    // Double every alternating digit
                    if(alt) {
                        d = d * 2;
                        // Make sure it's only a single digit
                        if(d > 9) {
                            d = (d % 10) + 1;
                        }
                    }
    
                    // Sum up the resulting digits
                    total = total + d;
                    alt = !alt;
                }

    
                // If it's a multiple of 10, it's valid
                return (total%10 == 0);
            }
        } catch(Exception e) { }        

        // If we end up here, the number isn't a number
        // or it's too long or too short or whatnot
        return false;
    }

    /**
     * Exists?
     */
    public boolean exists() {
        try {
            SharedResource shared = new SharedResource();
            KKEngIf kkEngine = shared.getKonakartEngineImpl();
            String sessionId = shared.getSessionId(getEnvironment().getRequest().getSession(true));
            int customerId = shared.getCustomerId(getEnvironment().getRequest().getSession(true));
            int languageId = shared.getLanguageId(getContentLanguage());

            // Get customer
            CustomerIf customer = shared.getCustomer(sessionId, customerId); 

            if(customer == null) {
                return false;
            } 
        } catch(Exception e) {
            return false;
        }

        return true;
    }
}
