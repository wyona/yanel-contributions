package org.wyona.yanel.impl.jelly.validators;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;

/**
 * Checks if the input is an integer value.
 */
public class IntegerValidator implements Validator {

    private String failMessage = null;
    
    public IntegerValidator(String failMessage) {
        this.failMessage = failMessage;
    }

    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        try {
            Integer.parseInt((String)value);
            return new ValidationMessage(item.getName(), value, true);
        } catch (NumberFormatException e) {
            return new ValidationMessage(item.getName(), value, failMessage, false);
        }
    }

}
