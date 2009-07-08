package org.wyona.yanel.impl.jelly.validators;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;

/**
 * This validator checks, if the items value (String) is not null or "".
 * 
 */
public class NullStringValidator implements Validator {

    private String failMessage = null;

    public NullStringValidator(String failMessage) {
        this.failMessage = failMessage;
    }

    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        ValidationMessage vm = null;
        if (value == null) {
            vm = new ValidationMessage(item.getName(), value, failMessage, false);
        } else if (!(value instanceof String)) {
            vm = new ValidationMessage(item.getName(), value, "Value is not of type String", false);
        } else {
            String strValue = (String) value;
            strValue = strValue.trim();
            if (strValue.equals("")) {
                vm = new ValidationMessage(item.getName(), value, failMessage, false);
            } else {
                vm = new ValidationMessage(item.getName(), value, true);
            }
        }
        return vm;
    }

}
