package org.wyona.yanel.impl.jelly.validators;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;

/**
 * Checks if the value is not the bad value.
 */
public class BadStringValidator implements Validator {

    private String badValue = null;
    private String failMessage = null;

    public BadStringValidator(String badValue, String failMessage) {
        this.badValue = badValue;
        this.failMessage = failMessage;
    }

    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        
        if (value == null)
            value = "";
        
        if (value instanceof String) {
            if (!value.equals(badValue)) {
                return new ValidationMessage(item.getName(), value, true);
            }
        }
        
        return new ValidationMessage(item.getName(), value, failMessage, false);
    }

}
