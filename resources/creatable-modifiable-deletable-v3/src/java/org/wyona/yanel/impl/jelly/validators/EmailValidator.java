package org.wyona.yanel.impl.jelly.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;

public class EmailValidator implements Validator {

    private static final String EMAIL_REGEX = "[a-zA-Z0-9[-][_][.]]+@[a-zA-Z0-9[-][.]]+\\.[a-zA-Z]{2,4}";
    
    private Pattern pattern;
    private String failMessage = null;
    
    public EmailValidator(String failMessage) {
        this.pattern = Pattern.compile(EMAIL_REGEX);
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
