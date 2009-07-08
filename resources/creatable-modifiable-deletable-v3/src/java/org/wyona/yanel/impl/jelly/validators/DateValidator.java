package org.wyona.yanel.impl.jelly.validators;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;

/**
 * Checks if the input is the specified data format (see SimpleDateFormat for patterns).
 */
public class DateValidator implements Validator {
    
    private DateFormat dateFormat;
    private String failMessage = null;
    
    public DateValidator(String datePattern, String failMessage) {
        this.dateFormat = new SimpleDateFormat(datePattern);
        this.failMessage = failMessage;
    }

    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        
        if (value == null)
            value = "";
        
        try {
            dateFormat.parse((String)value);
            return new ValidationMessage(item.getName(), value, true);
        } catch (ParseException e) {
            return new ValidationMessage(item.getName(), value, failMessage, false);
        }
    }

}
