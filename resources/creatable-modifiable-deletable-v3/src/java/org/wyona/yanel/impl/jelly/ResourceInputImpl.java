package org.wyona.yanel.impl.jelly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInput;
import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;

import org.apache.log4j.Logger;

/**
 * Standard implementation of ResourceInput. Sufficient for all Standard HTML
 * Tags.
 * 
 */
public class ResourceInputImpl implements ResourceInput {
    private static Logger log = Logger.getLogger(ResourceInputImpl.class);
    private List<ResourceInputItem> items = new ArrayList<ResourceInputItem>();

    /*
     * @see org.wyona.yanel.core.api.attributes.creatable.ResourceInput#getResourceInputItem(java.lang.String)
     */
    public ResourceInputItem getItem(String name) throws Exception {
        for (Iterator<ResourceInputItem> i = items.iterator(); i.hasNext();) {
            ResourceInputItem item = i.next();
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    /*
     * @see org.wyona.yanel.core.api.attributes.creatable.ResourceInput#getResourceInputItemNames()
     */
    public String[] getItemNames() throws Exception {
        List<String> itemNames = new ArrayList<String>();
        for (Iterator<ResourceInputItem> i = items.iterator(); i.hasNext();) {
            ResourceInputItem item = i.next();
            itemNames.add(item.getName());
        }

        return itemNames.toArray(new String[itemNames.size()]);
    }

    /*
     * @see org.wyona.yanel.core.api.attributes.creatable.ResourceInput#getItems()
     */
    public ResourceInputItem[] getItems() throws Exception {
        return items.toArray(new ResourceInputItem[items.size()]);
    }

    /**
     * Validate various items
     */
    public boolean validate() {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSZ");
        if( log.isDebugEnabled() )
            log.debug("Start validation " + dateFormat.format(new java.util.Date()) );
        for (Iterator<ResourceInputItem> i = items.iterator(); i.hasNext();) {
            ResourceInputItem item = i.next();
            if (item.getValidationMessage() == null || !item.getValidationMessage().isValidationOK()) {
                item.validate();
            }
        }
        if( log.isDebugEnabled() )
            log.debug("End validation " + dateFormat.format(new java.util.Date()));
        return getValidationMessages().size() == 0;
    }

    public boolean isValid() {
        return getValidationMessages().size() == 0;
    }

    /**
     * The validation messages must be collected new. The call of validate on
     * the whole resource input is not clear.
     */
    public List<ValidationMessage> getValidationMessages() {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        // check non valid and non validated items and collect all the messages
        for (Iterator<ResourceInputItem> i = items.iterator(); i.hasNext();) {
            ResourceInputItem item = i.next();
            ValidationMessage itemValidationMessage = item.getValidationMessage();
            if (itemValidationMessage != null) {
                if (!itemValidationMessage.isValidationOK()) {
                    validationMessages.add(itemValidationMessage);
                }
            }
        }
        return Collections.unmodifiableList(validationMessages);
    }

    /**
     * Method adds a ResourceInputItem to the ResourceInput. If an item with a
     * equal name as an item which is already in the ResourceInput is added,
     * then the older item get replaced.
     * 
     * @param item
     */
    public void add(ResourceInputItem item) {
        // Check for existance
        for (Iterator i = items.iterator(); i.hasNext();) {
            ResourceInputItem toCheck = (ResourceInputItem) i.next();
            if (toCheck.getName().equals(item.getName())) {
                throw new IllegalArgumentException("Item with the name already exists");
            }
        }

        items.add(item);
    }
    
    public void remove(ResourceInputItem item){
        for (Iterator i = items.iterator(); i.hasNext();) {
            ResourceInputItem toCheck = (ResourceInputItem) i.next();
            if (toCheck.getName().equals(item.getName())) {
                i.remove();
                break;
            }
        }
    }

    /**
     * Output the content of the input
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resource input content:\n");
        try {
            String[] itemNames = getItemNames();
            for (int i = 0; i < itemNames.length; i++) {
                sb.append(itemNames[i] + ": " + getItem(itemNames[i]).getValue() + "\n");
            }
        } catch(Exception e) {
            log.error(e, e);
        }
        return sb.toString();
    }
}
