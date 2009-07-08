package org.wyona.yanel.impl.resources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.ResourceNotFoundException;
import org.wyona.yanel.core.attributes.viewable.View;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Base class to handle forms. Returns the following XML streams:
 * 
 * <p>Initial call:</p>
 * <pre>
 * &lt;form-resource&gt;
 *   &lt;form/&gt;
 * &lt;/form-resource&gt;
 * </pre>
 * 
 * <p>Validation failed:</p>
 * <pre>
 * &lt;form-resource&gt;
 *   &lt;form/&gt;
 *   &lt;validation/&gt;
 *   &lt;input/&gt;
 *   &lt;result/&gt;
 * &lt;/form-resource&gt;
 * </pre>
 * 
 * <p>Successful processing:</p>
 * <pre>
 * &lt;form-resource&gt;
 *   &lt;confirmation/&gt;
 *   &lt;input/&gt;
 *   &lt;result/&gt;
 * &lt;/form-resource&gt;
 * </pre>
 * 
 * <p>Error occurred:</p>
 * <pre>
 * &lt;form-resource&gt;
 *   &lt;error/&gt;
 *   &lt;input/&gt;
 *   &lt;result/&gt;
 * &lt;/form-resource&gt;
 * </pre>
 * 
 * @author Matthias Leumann
 * 
 */
public abstract class BasicFormResource extends BasicXMLResource {
    
    private static final Logger log = Logger.getLogger(BasicFormResource.class);
    
    private static final String ENCODING = "UTF-8";
    private static final String INPUT_SUBMIT = "submit";
    
    protected static final String RESULT_CODE_DONE_SUCCESSFUL = "done-successful";
    private static final String RESULT_CODE_FAILED_INVALID = "failed-invalid";
    private static final String RESULT_CODE_FAILED_ERROR = "failed-error";
    
    private String resultCode;
    private StringBuilder resultXml = new StringBuilder();
    private LinkedList<NameValuePair> validationErrors = new LinkedList<NameValuePair>();

    public View getView(String viewId) throws Exception {
        if (getResourceConfigProperty("ssl-redirect") != null) {
            if (!getEnvironment().getRequest().isSecure() && getEnvironment().getRequest().getParameter("ssl") == null) {
                View view = new View();
                view.setResponse(false);
                URL sslURL;
                int sslPort = 443;
                if (getRealm().isProxySet()) {
                    if (realm.getProxySSLPort() >= 0) sslPort = realm.getProxySSLPort();
                    String requestURI = getEnvironment().getRequest().getRequestURI();
                    if (realm.getProxyPrefix() != null) requestURI = requestURI.substring(realm.getProxyPrefix().length());
                    sslURL = new URL("https", getRealm().getProxyHostName(), sslPort, requestURI + "?ssl=true");
                } else {
                    sslURL = new URL("https", getEnvironment().getRequest().getServerName(), sslPort, getEnvironment().getRequest().getRequestURI() + "?ssl=true");
                }
                log.warn("Redirect to SSL: " + sslURL.toString());
                getEnvironment().getResponse().setHeader("Location", sslURL.toString());
                getEnvironment().getResponse().setStatus(javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT);
                return view;
            }
        }
        return super.getView(viewId);
    }
    
    protected final InputStream getContentXML(String viewId) throws Exception {
        if (!exists()) {
            log.warn("No such resource: " + getPath());
            throw new ResourceNotFoundException("No such resource: " + getPath());
        }
        
        resultXml.append("<form-resource xmlns=\""+getNamespaceURI()+"\">");
        if (isFormSubmitted()) {
            validate();
            if (isInputValid()) {
                try {
                    resultCode = execute();
                    appendConfirmation();
                } catch (Exception e) {
                    log.error(e, e);
                    resultCode = RESULT_CODE_FAILED_ERROR;
                    appendError(e);
                }
            } else {
                resultCode = RESULT_CODE_FAILED_INVALID;
                appendForm();
                appendValidation();
            }
            appendInput();
            appendResult();
        } else {
           appendForm();
        }
        resultXml.append("</form-resource>");
        return new ByteArrayInputStream(resultXml.toString().getBytes(ENCODING));
    }
    
    /**
     * Adds a validation error. Use this method to report invalid input.
     * 
     * @param inputName name of the input parameter 
     * @param validationMessage validation message (may use i18n key)
     */
    protected final void addValidationError(String inputName, String validationMessage) {
        validationErrors.add(new NameValuePair(inputName, validationMessage));
    }
    
    /**
     * Sends an email. E.g. use this method to send confirmation mails.
     * 
     * @param from sender email address
     * @param replyTo email address (if null, then no reply-to will be set)
     * @param to receiver email address
     * @param subject email subject
     * @param content email content
     */
    protected void sendMail(String from, String replyTo, String to, String subject, String content) throws Exception {
        // Create a mail session
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.host", getResourceConfigProperty("smtpHost"));
        props.put("mail.smtp.port", "" + getResourceConfigProperty("smtpPort"));
        // TODO: http://java.sun.com/products/javamail/javadocs/javax/mail/Session.html
        Session session = Session.getDefaultInstance(props, null);

        // Construct the message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        if (replyTo != null) {
            InternetAddress[] replyToAddresses = new InternetAddress[1];
            replyToAddresses[0] = new InternetAddress(replyTo);
            msg.setReplyTo(replyToAddresses);
        }
        if( log.isDebugEnabled() )
            log.debug("From: " + msg.getFrom() + ", " + from);
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(content);

/*
        msg.setSubject(subject,"utf-8");
        msg.setContent(content, "text/plain;charset=utf-8");
*/

        // Send the message
        Transport.send(msg);
    }
    
    /**
     * Generates valid XML containing the data to display the form screen.
     * 
     * @return valid XML string
     */
    protected abstract String generateForm() throws Exception;
    
    /**
     * Generates valid XML containing the data to display the confirmation screen.
     * 
     * @return valid XML string
     */
    protected abstract String generateConfirmation() throws Exception;
    
    /**
     * Validates the form input. Use the method <code>addValidationError</code> to report invalid input.
     */
    protected abstract void validate() throws Exception;
    
    /**
     * Executes custom operation if the form input was successfully validated.
     * 
     * @return result code (e.g. "done-successful")
     * @throws Exception if processing failed.
     */
    protected abstract String execute() throws Exception;
    
    /**
     * @return the name space URI of of the resulting XML.
     */
    protected abstract String getNamespaceURI();
    
    
    /**
     * @return returns true if and only if the input parameters contain a parameter "submit".
     */
    private boolean isFormSubmitted() {
        return getParameterAsString(INPUT_SUBMIT) != null;
    }
    
    /**
     * @return true if and only if there are no validation errors.
     */
    private boolean isInputValid() {
        return validationErrors.isEmpty();
    }
    
    /**
     * Appends the result code to the result XML.
     */
    private void appendResult() {
        resultXml.append("<result>");
        resultXml.append(resultCode);
        resultXml.append("</result>");
    }
    
    /**
     * Appends the data for the form screen to the result XML.
     */
    private void appendForm() throws Exception {
        resultXml.append("<form>");
        String form = generateForm();
        if (form != null) {
            resultXml.append(form);
        }
        resultXml.append("</form>");
    }
    
    /**
     * Appends the data for the confirmation screen to the result XML.
     */
    private void appendConfirmation() throws Exception {
        resultXml.append("<confirmation>");
        String confirmation = generateConfirmation();
        if (confirmation != null) {
            resultXml.append(confirmation);
        }
        resultXml.append("</confirmation>");
    }
    
    /**
     * Appends the validation messages to the result XML.
     */
    private void appendValidation() {
        resultXml.append("<validation>");
        for (NameValuePair error : validationErrors) {
            resultXml.append("<"+error.getName()+">"+error.getValue()+"</"+error.getName()+">");
        }
        resultXml.append("</validation>");
    }
    
    /**
     * Appends the form input to the result XML.
     */
    private void appendInput() {
        resultXml.append("<input>");
        Iterator it = getParameters().entrySet().iterator();
        while (it.hasNext()) {
            Entry param = (Entry)it.next();
            resultXml.append("<"+param.getKey()+"><![CDATA["+param.getValue()+"]]></"+param.getKey()+">");
        }
        resultXml.append("</input>");
    }
    
    private void appendError(Exception e) {
        resultXml.append("<error>");
        resultXml.append("<![CDATA[" + e.toString() + "]]>");
        resultXml.append("</error>");
    }
    
    /**
     * Private utility class to add validation errors to the list.
     */
    private class NameValuePair {
        private String name;
        private String value;
        
        public NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }
        
        public String getName() {
            return name;
        }
        
        public String getValue() {
            return value;
        }
    }

}
