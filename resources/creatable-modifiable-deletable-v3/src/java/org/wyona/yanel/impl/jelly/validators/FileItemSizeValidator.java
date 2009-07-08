package org.wyona.yanel.impl.jelly.validators;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;
import org.wyona.yanel.impl.jelly.FileItem;

/**
 * This validator checks, if the items value, which is a fileitem,
 * has not more and not less than the specified bytes.
 *
 */
public class FileItemSizeValidator implements Validator {
    
    private String failMessage = null;
    
    private long minBytes = 0;
    
    private long maxBytes = 0;
    
    public FileItemSizeValidator(String failMessage, long minBytes, long maxBytes) {
        this.failMessage = failMessage;
        this.minBytes = minBytes;
        this.maxBytes = maxBytes;
    }

    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        ValidationMessage vm = null;
        if (value == null) {
            if (this.minBytes > 0) {
                vm = new ValidationMessage(item.getName(), value, failMessage, false);
            } else {
                vm = new ValidationMessage(item.getName(), value, true);
            }
        } else {
            FileItem fileItem = (FileItem) value;
            
            if (fileItem.getData().length > minBytes && fileItem.getData().length < maxBytes) {
                vm = new ValidationMessage(item.getName(), value, true);
            } else {
                vm = new ValidationMessage(item.getName(), value, failMessage, false);
            }
        }
        return vm;
    }

}
