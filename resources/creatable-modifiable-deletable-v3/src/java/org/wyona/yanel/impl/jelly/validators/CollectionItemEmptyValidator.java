package org.wyona.yanel.impl.jelly.validators;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItemCollection;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;

/**
 * Checks if a collection item has at least one Element.
 *
 */
public class CollectionItemEmptyValidator implements Validator {
    
    private String failMessage = null;
    
    public CollectionItemEmptyValidator(String failMessage) {
        this.failMessage = failMessage;
    }

    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        ValidationMessage vm = null;
        if (value == null) {
            vm = new ValidationMessage(item.getName(), value, failMessage, false);
        } else {
            ResourceInputItemCollection cItem = (ResourceInputItemCollection) item;
            
            if (cItem.size() > 0) {
                vm = new ValidationMessage(item.getName(), value, true);
            } else {
                vm = new ValidationMessage(item.getName(), value, failMessage, false);
            }
        }
        return vm;
    }

}
