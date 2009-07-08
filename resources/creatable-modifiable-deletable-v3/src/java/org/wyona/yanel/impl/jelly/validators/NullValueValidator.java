package org.wyona.yanel.impl.jelly.validators;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;

import org.apache.log4j.Logger;

/**
 * This validator checks, if the items value (String) is not null or "".
 * 
 */
public class NullValueValidator implements Validator {

    private static Logger log = Logger.getLogger(NullValueValidator.class);

    private String failMessage = null;

    public NullValueValidator(String failMessage) {
        this.failMessage = failMessage;
    }

    public ValidationMessage validate(ResourceInputItem item) {
        log.info("Validate item: " + item.getName() + " (Type: " + item.getType() + ")");
        Object value = item.getValue();
        ValidationMessage vm = null;
        
        if (value == null) {
            vm = new ValidationMessage(item.getName(), value, failMessage, false);
        } else {
            vm = new ValidationMessage(item.getName(), value, true);
        }
        return vm;
    }

}
