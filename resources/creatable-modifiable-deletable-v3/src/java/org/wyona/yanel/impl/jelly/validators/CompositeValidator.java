package org.wyona.yanel.impl.jelly.validators;

import java.util.ArrayList;
import java.util.List;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;

/**
 * All added vaitators will be executed in the order they were added on validate.
 *
 */
public class CompositeValidator implements Validator {
    
    private List<Validator> validators = new ArrayList<Validator>();

    public boolean add(Validator o) {
        return validators.add(o);
    }

    public void clear() {
        validators.clear();
    }

    public boolean remove(Object o) {
        return validators.remove(o);
    }

    public int size() {
        return validators.size();
    }

    public ValidationMessage validate(ResourceInputItem item) {
        
        boolean ok = true;
        StringBuffer collectedMessage = new StringBuffer();
        
        for (Validator v : this.validators) {
            ValidationMessage vm = v.validate(item);
            if (vm != null) {
                ok &= vm.isValidationOK();
                if (vm.getMessage() != null) {
                    collectedMessage.append(vm.getMessage());
                }
            }
        }
        
        ValidationMessage validationMessage = new ValidationMessage(item.getName(), item.getValue(),collectedMessage.toString(),ok);
        
        return validationMessage;
    }
}
