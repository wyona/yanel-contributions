package org.wyona.yanel.impl.resources.jellyadapterofcmdv3;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.xml.serializer.Serializer;
import org.wyona.commons.io.MimeTypeUtil;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.serialization.SerializerFactory;

/**
 * A view is configured with a template and a set of XSLTs:
 * <pre>
 * [view id="x"]
 *  [template type="JELLY"]...location...[/template]
 *  [xslt]_location_[/xslt]
 *  ...
 *  [xslt]_location_[/xslt]
 *  [mime-type]...(test/html)...[mime-type]
 *  [serializer key="...(XHTML_STRICT)..."]
 *  ... serializer props...
 *  [/serializer]
 *  
 * [/view]
 * </pred>
 * */
public class ViewDescriptorUsingTemplate extends ViewDescriptor {

    /**
     *
     */
    public static class TemplateOption{
        public static final String TYPE_JELLY = "JELLY";
        public static final String TYPE_XSLT = "XSLT";
        /**
         * This is to say that it is raw XML
         */
        public static final String TYPE_XML = "XML";
        
        private String type;
        private String url;
        
        public TemplateOption(String type, String url) {
            this.type = type;
            this.url = url;
        }
        
        public String getType() {
            return type;
        }
        
        public String getUrl() {
            return url;
        }
    }
    
    private boolean fragment = false;
    
    protected TemplateOption [] templates = new TemplateOption[0];
    
    protected String serializerKey = SerializerFactory.XHTML_STRICT_KEY;
    protected Properties serializerProperties = new Properties();

    protected ViewDescriptorUsingTemplate(String id){
        super(id);
    }
    
    public ViewDescriptorUsingTemplate(String id, Configuration viewDefinition) throws ConfigurationException{
        this(id, viewDefinition, false);
    }
    
    public ViewDescriptorUsingTemplate(String id, Configuration viewDefinition, boolean isFragment) throws ConfigurationException{
        super(id);
        configure(viewDefinition);
        this.fragment = isFragment;
    }
    
    /**
     * Defaults:
     * <p>
     * mime-type : text/html<br>
     * serializer: XHTML_STRICT<br>
     * serializer props: empty
     */
    protected void configure(Configuration config) throws ConfigurationException {
        Configuration[] templateConfigs = config.getChildren("template");
        if(templateConfigs.length == 0){
            throw new ConfigurationException("A template is not specified");
        }
        
        Configuration[] xsltConfigs = config.getChildren("xslt");
        
        templates = new TemplateOption[1 + xsltConfigs.length];
        
        templates[0] = new TemplateOption(templateConfigs[0].getAttribute("type"), templateConfigs[0].getValue());
        for (int i = 0; i < xsltConfigs.length; i++) {
            templates[i+1] = new TemplateOption(TemplateOption.TYPE_XSLT, xsltConfigs[i].getValue());
        }
        
        Configuration mimeTypeConfig = config.getChild("mime-type", false);
        if (mimeTypeConfig != null) {
            setMimeType(mimeTypeConfig.getValue());
        }else{
            setMimeType("text/html");
        }
        
        Configuration serializerConfig = config.getChild("serializer", false);
        if (serializerConfig != null) {
            serializerKey = (serializerConfig.getAttribute("key") == null ? serializerKey : serializerConfig.getAttribute("key"));
            serializerProperties = new Properties();
            Configuration propertyConfig = serializerConfig.getChild("omit-xml-declaration", false);
            if (propertyConfig != null) {
                serializerProperties.setProperty("omit-xml-declaration", propertyConfig.getValue());
            }
            propertyConfig = serializerConfig.getChild("doctype-public", false);
            if (propertyConfig != null) {
                serializerProperties.setProperty("doctype-public", propertyConfig.getValue());
            }
            propertyConfig = serializerConfig.getChild("doctype-system", false);
            if (propertyConfig != null) {
                serializerProperties.setProperty("doctype-sytem", propertyConfig.getValue());
            }
        }
    }

    public String getSerializerKey() {
        return serializerKey;
    }

    public void setSerializerKey(String serializerKey) {
        this.serializerKey = serializerKey;
    }

    public Properties getSerializerProperties() {
        return serializerProperties;
    }

    public void setSerializerProperties(Properties serializerProperties) {
        this.serializerProperties = serializerProperties;
    }

    public TemplateOption[] getTemplates() {
        return templates;
    }
    
    /**
     * @return <code>true</code> when the view represents a fragment, not a screen.
     * For instance, a fragment view is useful when doing AJAX call
     * */
    public boolean isFragment(){
        return fragment;
    }
    
    /**
     * Creates an html or xml serializer for this view descriptor
     */
    public Serializer getSerializer() throws Exception {
        Serializer serializer = null;
        String serializerKey = getSerializerKey();
        if (serializerKey != null) {
            serializer = SerializerFactory.getSerializer(serializerKey);
            if (serializer == null) {
                throw new Exception("could not create serializer for key: " + serializerKey);
            }
        } else {
            String mimeType = getMimeType();

            if (MimeTypeUtil.isHTML(mimeType) && !MimeTypeUtil.isXML(mimeType)) {
                serializer = SerializerFactory.getSerializer(SerializerFactory.HTML_TRANSITIONAL);
            } else if (MimeTypeUtil.isHTML(mimeType) && MimeTypeUtil.isXML(mimeType)){
                serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
            } else if (MimeTypeUtil.isXML(mimeType)) {
                serializer = SerializerFactory.getSerializer(SerializerFactory.XML);
            } else if (MimeTypeUtil.isTextual(mimeType)) {
                serializer = SerializerFactory.getSerializer(SerializerFactory.TEXT);
            } else{
                serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
            }
        }
        // allow to override xml declaration and doctype:
        Properties properties = getSerializerProperties();
        if (properties != null) {
            Enumeration propNames = properties.propertyNames();
            while (propNames.hasMoreElements()) {
                String name = (String)propNames.nextElement();
                String value = properties.getProperty(name);

                serializer.getOutputFormat().setProperty(name, value);
            }
        }
        return serializer;
    }
}
