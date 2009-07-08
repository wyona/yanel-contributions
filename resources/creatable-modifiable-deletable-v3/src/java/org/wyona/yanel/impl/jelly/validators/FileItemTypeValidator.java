package org.wyona.yanel.impl.jelly.validators;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;
import org.wyona.yanel.impl.jelly.FileItem;

/**
 * This validator checks, if the items value, which is a file item, is of the
 * specified content type.
 * 
 */
public class FileItemTypeValidator implements Validator {

    private String failMessage = null;

    private String contentType = null;

    public FileItemTypeValidator(String failMessage, String contentType) {
        this.failMessage = failMessage;
        this.contentType = contentType;
    }

    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        ValidationMessage vm = null;
        if (value == null) {
            vm = new ValidationMessage(item.getName(), value, true);
        } else {
            FileItem fileItem = (FileItem) value;

            if (fileItem.getContentType() != null) {
                if (fileItem.getContentType().equals(contentType)) {
                    vm = new ValidationMessage(item.getName(), value, true);
                } else {
                    vm = new ValidationMessage(item.getName(), value, failMessage, false);
                }
            }
        }
        return vm;
    }

}
