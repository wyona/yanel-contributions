package org.wyona.yanel.impl.jelly.validators;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;
import org.wyona.yanel.impl.jelly.Option;

/**
 * Validates the value of a single selection item. Returns a successful message
 * if and only if the selected value is not <code>null</code> and not equal
 * to <code>badValue</code>.
 *
 */
public class SingleSelectionValidator implements Validator {

    private String badValue = null;
    private String failMessage = null;

    public SingleSelectionValidator(String badValue, String failMessage) {
        this.badValue = badValue;
        this.failMessage = failMessage;
    }
    
    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        
        if (value instanceof Option) {
            String strValue = ((Option)value).getValue();
            if (strValue != null && !strValue.equals(badValue)) {
                return new ValidationMessage(item.getName(), value, true);
            }
        }
        
        return new ValidationMessage(item.getName(), value, failMessage, false);
    }

}
