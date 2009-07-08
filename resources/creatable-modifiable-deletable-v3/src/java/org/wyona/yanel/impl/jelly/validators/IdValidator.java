package org.wyona.yanel.impl.jelly.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;

/**
 * Checks if an ID field contains a URL compatible id.
 * Empty string will also lead to a validation error.
 */
public class IdValidator implements Validator {

    private static final String ID_REGEX = "[a-z0-9[-][_]]*";
    
    private Pattern pattern;
    private String failMessage;
    
    public IdValidator(String failMessage) {
        this.pattern = Pattern.compile(ID_REGEX);
        this.failMessage = failMessage;
    }
    
    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        
        if (value instanceof String) {
            Matcher m = pattern.matcher((String)value);
            if (m.matches()) {
                return new ValidationMessage(item.getName(), value, true);
            }
        }
        
        return new ValidationMessage(item.getName(), value, failMessage, false);
    }

}
