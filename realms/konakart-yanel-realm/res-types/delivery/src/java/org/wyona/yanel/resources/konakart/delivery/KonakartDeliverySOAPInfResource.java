/*
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.resources.konakart.delivery;

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

/**
 * Ask for delivery information
 */
public class KonakartDeliverySOAPInfResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(KonakartDeliverySOAPInfResource.class);
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
            response.setHeader("Location", getResourceConfigProperty("next-url"));
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
            doc = org.wyona.commons.xml.XMLHelper.createDocument(KONAKART_NAMESPACE, "delivery");
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

        // Get default address
        AddressIf defaddr = kkEngine.getDefaultAddressPerCustomer(sessionId);
    
        if(defaddr == null) {
            // Well, there's no default address - just make the first address
            // we have on the database the default address from now on (if there's
            // no default address this is most likely a newly registered user).
            AddressIf[] addrs = customer.getAddresses();
            // There has to be at least one address, Konakart doesn't let you register otherwise.
            defaddr = addrs[0];
            kkEngine.setDefaultAddressPerCustomer(sessionId, defaddr.getId());
            // Use billing addr as default shipping addr
            kkEngine.editCustomer(sessionId, customer);
        }
    
        // Now print that address information
        Element defAddrElem = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "default-address"));
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
        Element defAddrPhoneElem = (Element) defAddrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "telephone"));
        defAddrPhoneElem.appendChild(doc.createTextNode("" + customer.getTelephoneNumber())); 

        // If the process parameter is set,
        // we process the form and proceed/redirect
        String process = getEnvironment().getRequest().getParameter("process");
        if(process != null) {
            // Should we redirect?
            redirect = true;

            javax.servlet.http.HttpServletRequest req = getEnvironment().getRequest();
            String delivery_addr = req.getParameter("delivery_address");

            if("same_address".equalsIgnoreCase(delivery_addr)) {
                // Use billing addr as shipping addr
                customer.setCustom1(Integer.toString(defaddr.getId()));
                kkEngine.editCustomer(sessionId, customer);

                req.getSession(true).removeAttribute("checkout-data-remarks");
                String val = req.getParameter("remarks");
                if(val != null && val.length() > 0) {
                    req.getSession(true).setAttribute("checkout-data-remarks", val);
                    appendField("remarks", val, rootElement, doc);
                }
            } else {
                // Process form input
                // Note: We re-use the defaddr object since we don't need it anymore.
                String val;
                boolean address_valid = true;

                // Parsing all those parameters sure is a lot of fun ;-) 
                val = req.getParameter("gender");
                if(val != null && val.length() > 0) {
                    defaddr.setGender(val);
                    appendField("gender", val, rootElement, doc);
                } else {
                    appendErr("gender", rootElement, doc);
                    // See bug #7776
                    // Konakart doesn't have 'gender' field in
                    // order object, so we don't really need it.
                    // address_valid = false;
                }

                val = req.getParameter("firstname");
                if(val != null && val.length() > 0) {
                    defaddr.setFirstName(val);
                    appendField("firstname", val, rootElement, doc);
                } else {
                    appendErr("firstname", rootElement, doc);
                    address_valid = false;
                }

                val = req.getParameter("lastname");
                if(val != null && val.length() > 0) {
                    defaddr.setLastName(val);
                    appendField("lastname", val, rootElement, doc);
                } else {
                    appendErr("lastname", rootElement, doc);
                    address_valid = false;
                }

                val = req.getParameter("street");
                if(val != null && val.length() > 0) {
                    defaddr.setStreetAddress(val);
                    appendField("street", val, rootElement, doc);
                } else {
                    appendErr("street", rootElement, doc);
                    address_valid = false;
                }

                val = req.getParameter("location");
                if(val != null && val.length() > 0) {
                    defaddr.setCity(val);
                    appendField("location", val, rootElement, doc);
                } else {
                    appendErr("location", rootElement, doc);
                    address_valid = false;
                }

                val = req.getParameter("company");
                if(val != null && val.length() > 0) {
                    defaddr.setCompany(val);
                    appendField("company", val, rootElement, doc);
                } else {
                    defaddr.setCompany("");
                }
                
                val = req.getParameter("zip");
                if(val != null && val.length() > 0) {
                    Pattern pzip = Pattern.compile("[1-9][0-9]{3}");
                    Matcher mzip = pzip.matcher(val);
                    appendField("zip", val, rootElement, doc);
                    if(mzip.find()) {
                        defaddr.setPostcode(mzip.group(0));
                    } else {
                        appendErr("zip.invalid", rootElement, doc);
                        address_valid = false;
                    }
                } else {
                    appendErr("zip", rootElement, doc);
                    address_valid = false;
                }
                
                // Country, Zone
                com.konakart.appif.CountryIf cn = kkEngine.getCountryPerName("Switzerland");
                defaddr.setState(getResourceConfigProperty("default-zone"));
                if(cn == null) {
                    com.konakart.appif.CountryIf[] cns = kkEngine.getAllCountries(); // We use the first country in the database.
                    defaddr.setCountryId(cns[0].getId());
                } else {
                    defaddr.setCountryId(cn.getId());
                }

                req.getSession(true).removeAttribute("checkout-data-remarks");
                val = req.getParameter("remarks");
                if(val != null && val.length() > 0) {
                    req.getSession(true).setAttribute("checkout-data-remarks", val);
                    appendField("remarks", val, rootElement, doc);
                }

                if(address_valid) {
                    int old_id = defaddr.getId();
                    int new_id = kkEngine.addAddressToCustomer(sessionId, defaddr);
                    customer.setCustom1(Integer.toString(new_id));
                    kkEngine.setDefaultAddressPerCustomer(sessionId, old_id);
                    kkEngine.editCustomer(sessionId, customer);
                } else {
                    redirect = false;
                }
            }
        } else {
            String edit = getEnvironment().getRequest().getParameter("edit");

            // If 'edit' is set, we read the current values
            // from the database end display them again for editing
            if(edit != null) {
                // Get delivery (=default) address
                AddressIf[] addrs = kkEngine.getAddressesPerCustomer(sessionId);
                int devaddrid = Integer.parseInt(customer.getCustom1());
                AddressIf devaddr = null;
            
                for(AddressIf a : addrs) {
                    if(a.getId() == devaddrid) {
                        devaddr = a;
                    }
                }
        
                if(devaddr == null) devaddr = defaddr;
        
                // Now print that address information
                appendField("lastname", devaddr.getLastName(), rootElement, doc);
                appendField("firstname", devaddr.getFirstName(), rootElement, doc);
                if(devaddr.getCompany() != null) {
                    appendField("company", devaddr.getCompany(), rootElement, doc);
                }
                appendField("zip", devaddr.getPostcode(), rootElement, doc);
                appendField("location", devaddr.getCity(), rootElement, doc);
                appendField("street", devaddr.getStreetAddress(), rootElement, doc);
                appendField("gender", devaddr.getGender(), rootElement, doc);

                String remarks = (String) getEnvironment().getRequest().getSession(true).getAttribute("checkout-data-remarks");
                if(remarks != null) appendField("remarks", remarks, rootElement, doc);
            }
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
        err.setAttribute("id", "" + field);
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

    /**
     * Append field to XML output.
     */
    public void appendField(String field, String val, Element elem, org.w3c.dom.Document doc) {
        Element err = (Element) elem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "field"));
        err.setAttribute("id", field);
        err.appendChild(doc.createTextNode("" + val));
    }
}
