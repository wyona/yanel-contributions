/*-
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.resources.konakart.settings;

import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.resources.konakart.shared.SharedResource;

import org.wyona.security.core.api.User;
import org.wyona.security.core.api.UserManager;

import com.konakart.appif.KKEngIf;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.AddressIf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Edit settings
 */
public class KonakartSettingsSOAPInfResource extends BasicXMLResource {
    private static Logger log = Logger.getLogger(KonakartSettingsSOAPInfResource.class);
    private static String KONAKART_NAMESPACE = "http://www.konakart.com/1.0";

    protected InputStream getContentXML(String viewId) throws Exception {
        // Konakart data
        SharedResource shared = new SharedResource();
        KKEngIf kkEngine = shared.getKonakartEngineImpl();
        String sessionId = shared.getSessionId(getEnvironment().getRequest().getSession(true));
        int customerId = shared.getCustomerId(getEnvironment().getRequest().getSession(true));
        int languageId = shared.getLanguageId(getContentLanguage());

        // Build document
        Document doc = null;
        doc = org.wyona.commons.xml.XMLHelper.createDocument(KONAKART_NAMESPACE, "settings");
        Element rootElement = doc.getDocumentElement();

        // Get customer
        CustomerIf customer = shared.getCustomer(sessionId, customerId); 

        // What do to?
        HttpServletRequest req = getEnvironment().getRequest();
        String action = req.getParameter("action"); 

        // Go back to checkout after saving?
        // This is used to e.g. integrate this with the checkout process
        String b2c = req.getParameter("back2checkout");
        if(b2c != null) {
            Element b2cE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "back2checkout"));
        }

        if(action == null) {
            // No nothing
            if(customer == null) {
                Element nologin = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "not-logged-in"));
            } else {
                Element actionElem = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "no-action-requested"));
            }
        } else if("login".equals(action) && customer == null) {
            // Perform login
            String newSessionId = null;
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            if(username != null && password != null) {
                // Log into Yanel and Konakart
                newSessionId = shared.login(username, password, getRealm(), req.getSession(true));
            }

            if(newSessionId == null) {
                // Login failed
                Element err = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "login-failed"));
                Element nologin = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "not-logged-in"));
            } else {
                // Login successful
                Element actionElem = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "no-action-requested"));
            } 
        } else if("edit_address".equals(action) && customer != null) {
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
            }

            printData(doc, rootElement, defaddr, customer);
        } else if("save_address".equals(action) && customer != null) {
            // Save new default address, email, etc.
            String val;
            boolean valid = true;
            AddressIf defaddr = kkEngine.getDefaultAddressPerCustomer(sessionId);

            // Parsing all those parameters sure is a lot of fun ;-) 
            val = req.getParameter("gender");
            if(val != null && val.length() > 0 && ("f".equals(val) || "m".equals(val))) {
                defaddr.setGender(val);
                customer.setGender(val);
            } else {
                appendErr("gender", rootElement, doc);
                valid = false;
            }

            // First name
            val = req.getParameter("firstname");
            if(val != null && val.length() > 0) {
                defaddr.setFirstName(val);
                customer.setFirstName(val);
            } else {
                defaddr.setFirstName("");
                appendErr("firstname", rootElement, doc);
                valid = false;
            }

            // Last name
            val = req.getParameter("lastname");
            if(val != null && val.length() > 0) {
                defaddr.setLastName(val);
                customer.setLastName(val);
            } else {
                defaddr.setLastName("");
                appendErr("lastname", rootElement, doc);
                valid = false;
            }

            // Street
            val = req.getParameter("street");
            if(val != null && val.length() > 0) {
                defaddr.setStreetAddress(val);
            } else {
                defaddr.setStreetAddress("");
                appendErr("street", rootElement, doc);
                valid = false;
            }

            // Location
            val = req.getParameter("location");
            if(val != null && val.length() > 0) {
                defaddr.setCity(val);
            } else {
                defaddr.setCity("");
                appendErr("location", rootElement, doc);
                valid = false;
            }

            // Phone
            val = req.getParameter("phone");
            if(val != null && val.length() > 0) {
                customer.setTelephoneNumber(val);
            } else {
                customer.setTelephoneNumber("");
                appendErr("phone", rootElement, doc);
                valid = false;
            }

            // Fax
            // Note: fax is not a required field
            val = req.getParameter("fax");
            if(val != null && val.length() > 0) {
                customer.setFaxNumber(val);
            } else {
                customer.setFaxNumber("");
            }

            // E-Mail
            String new_email = null, old_email = null;
            val = req.getParameter("email");
            if(val != null && val.length() > 0) {
                if(!val.equalsIgnoreCase(customer.getEmailAddr())) {
                    UserManager m = getRealm().getIdentityManager().getUserManager();
                    if(!kkEngine.isEmailValid(val)) {
                        appendErr("email.invalid", rootElement, doc);
                        valid = false;
                    } else if(kkEngine.doesCustomerExistForEmail(val) || m.existsUser(val)) {
                        appendErr("email.in-use", rootElement, doc);
                        valid = false;
                    }
                    // If the email changed, we need to update
                    // the Yanel user as well to keep in sync.
                    new_email = val;
                    old_email = customer.getEmailAddr();
                    customer.setEmailAddr(new_email);
                }
            } else {
                customer.setEmailAddr("");
                appendErr("email", rootElement, doc);
                valid = false;
            }

            // Company
            // Note: company is not a required field
            val = req.getParameter("company");
            defaddr.setCompany(val);

            val = req.getParameter("zip");
            if(val != null && val.length() > 0) {
                Pattern pzip = Pattern.compile("[1-9][0-9]{3}");
                Matcher mzip = pzip.matcher(val);
                if(mzip.find()) {
                    defaddr.setPostcode(mzip.group(0));
                } else {
                    defaddr.setPostcode(val);
                    appendErr("zip.invalid", rootElement, doc);
                    valid = false;
                }
            } else {
                defaddr.setPostcode("");
                appendErr("zip", rootElement, doc);
                valid = false;
            }
            
            // Country, Zone
            // Currently hard-coded...
            com.konakart.appif.CountryIf cn = kkEngine.getCountryPerName("Switzerland");
            defaddr.setState(getResourceConfigProperty("default-zone"));
            if(cn == null) {
                // We use the first country in the database
                // if Switzerland couldn't be found...
                com.konakart.appif.CountryIf[] cns = kkEngine.getAllCountries();
                defaddr.setCountryId(cns[0].getId());
            } else {
                defaddr.setCountryId(cn.getId());
            }

            if(valid) {
                try {
                    // Update Konakart user!
                    kkEngine.editCustomerAddress(sessionId, defaddr);
                    kkEngine.editCustomer(sessionId, customer);

                    if(new_email != null) {
                        // E-Mail changed. Update Yanel alias!
                        try { 
                            UserManager m = getRealm().getIdentityManager().getUserManager();
                            String trueId = m.getTrueId(old_email);
                            m.createAlias(new_email, trueId);
                            m.removeAlias(old_email);
                        } catch(Exception e) {
                            // This is bad...
                            log.error(e, e);
                            log.warn("Big fat warning! Unable to update Yanel user for '" + new_email + "'! " +
                                     "Associated Konakart user has possibly *lost* his/her Yanel identity unless" +
                                     "fixed manually! Whereas it's also possible the user didn't have a Yanel" +
                                     "identity in the first place, the question is why... Either way, it's broken.");
                        }
                    }
                } catch(Exception e) {
                    // Something went wrong, log exception
                    Element err = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "error"));
                    err.setAttributeNS(KONAKART_NAMESPACE, "id", "exception");
                    log.error(e, e);
                    valid = false;
                }
            }

            if(valid) {
                Element succ = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "saved-address"));
            } else {
                printData(doc, rootElement, defaddr, customer);
                Element err = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "error"));
            }
        } else if("edit_password".equals(action) && customer != null) {
            // Not much do do here...
            Element ask = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "request-password"));
        } else if("save_password".equals(action) && customer != null) {
            // Change the password
            boolean success = false;

            String old_pass = request.getParameter("oldpw");
            String new_pass = request.getParameter("newpw1");
            boolean confirmation = new_pass == null ? false : new_pass.equals(request.getParameter("newpw2"));
            
            if(confirmation && new_pass.length() >= 5) {
                try {  
                    kkEngine.changePassword(sessionId, old_pass, new_pass);
                    success = true;
                    try { 
                        UserManager m = getRealm().getIdentityManager().getUserManager();
                        User user = m.getUser(customer.getEmailAddr());
                        user.setPassword(new_pass);
                        user.save();
                    } catch(Exception e) {
                        // This is bad...
                        log.error(e, e);
                        log.warn("Big fat warning! Unable to update Yanel user for '" + customer.getEmailAddr() + "'! " +
                                 "Associated Konakart user has possibly *lost* his/her Yanel identity unless" +
                                 "fixed manually! Whereas it's also possible the user didn't have a Yanel" +
                                 "identity in the first place, the question is why... Either way, it's broken.");
                    }
                } catch(Exception e) {
                    // Old pw incorrect
                    Element err = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "error"));
                    err.setAttributeNS(KONAKART_NAMESPACE, "id", "password.incorrect");
                }
            } else {
                if(new_pass != null && new_pass.length() < 5) {
                    // Passwort insecure
                    Element err = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "error"));
                    err.setAttributeNS(KONAKART_NAMESPACE, "id", "password.too-short");
                } else {
                    // Passwords don't match
                    Element err = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "error"));
                    err.setAttributeNS(KONAKART_NAMESPACE, "id", "password.mismatch");
                }
            }

            if(success) {
                Element succ = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "saved-password"));
            } else {
                Element ask = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "request-password"));
                Element err = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "error"));
            }
        } else {
            Element actionElem = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "no-action-requested"));
        }

        // Generate output
        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
        org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
        return new java.io.ByteArrayInputStream(baout.toByteArray());
    }

    /**
     * Print address and customer information
     */
    private void printData(Document doc, Element rootElement, AddressIf addr, CustomerIf customer) {
        Element addrElem = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "customer"));
        addrElem.setAttributeNS(KONAKART_NAMESPACE, "id", "" + addr.getId());
        Element phoneElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "phone"));
        phoneElem.appendChild(doc.createTextNode("" + customer.getTelephoneNumber())); 
        if(customer.getFaxNumber() != null) {
            // Fax is allowed to be empty
            Element faxElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "fax"));
            faxElem.appendChild(doc.createTextNode("" + customer.getFaxNumber())); 
        }
        Element emailElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "email"));
        emailElem.appendChild(doc.createTextNode("" + customer.getEmailAddr())); 
        Element addrNameElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "last-name"));
        addrNameElem.appendChild(doc.createTextNode("" + addr.getLastName())); 
        Element addrfNameElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "first-name"));
        addrfNameElem.appendChild(doc.createTextNode("" + addr.getFirstName())); 
        if(addr.getCompany() != null) {
            // Company is allowed to be empty
            Element addrCompanyElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "company"));
            addrCompanyElem.appendChild(doc.createTextNode("" + addr.getCompany())); 
        }
        Element addrCityElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "city"));
        addrCityElem.appendChild(doc.createTextNode("" + addr.getCity())); 
        Element addrCountryElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "country"));
        addrCountryElem.appendChild(doc.createTextNode("" + addr.getCountryName())); 
        Element addrStreetElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "street"));
        addrStreetElem.appendChild(doc.createTextNode("" + addr.getStreetAddress())); 
        Element addrGenderElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "gender"));
        addrGenderElem.appendChild(doc.createTextNode("" + addr.getGender())); 
        Element addrPostcodeElem = (Element) addrElem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "postcode"));
        addrPostcodeElem.appendChild(doc.createTextNode("" + addr.getPostcode())); 
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
        } catch(Exception e) {
            // Can't reach KonaKart?
            return false;
        }

        // All is well
        return true;
    }

    /**
     * Append error element for given field.
     */
    public void appendErr(String field, Element elem, org.w3c.dom.Document doc) {
        Element err = (Element) elem.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "error"));
        err.setAttribute("id", "" + field);
    }
}
