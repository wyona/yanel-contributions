package org.wyona.yanel.impl.jelly.validators;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;
import org.wyona.yanel.impl.jelly.InputItemWithManySelectableOptions;
import org.wyona.yanel.impl.jelly.InputItemWithOneSelectableOption;
import org.wyona.yanel.impl.jelly.Option;

/**
 * Checks if any options are selected for the item. 
 * When no options available then empty selection is validated as true.
 * */
public class EmptySelectionValidator implements Validator{
    private String failMessage = null;
    
    public EmptySelectionValidator(String failMessage) {
        this.failMessage = failMessage;
    }
    
    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        ValidationMessage vm = null;
        
        if ((value instanceof Option[] && ((Option[])value).length > 0) || (value instanceof Option)) {
                vm = new ValidationMessage(item.getName(), value, true);
        }else{
            Option[] availableOptions = new Option[0];
            if (item instanceof InputItemWithManySelectableOptions) {
                InputItemWithManySelectableOptions iMany = (InputItemWithManySelectableOptions) item;
                availableOptions = iMany.getOptions();
            }
            
            if (item instanceof InputItemWithOneSelectableOption) {
                InputItemWithOneSelectableOption iMany = (InputItemWithOneSelectableOption) item;
                availableOptions = iMany.getOptions();
            }
            if((value instanceof Option[] || value instanceof Option || value == null) && availableOptions.length == 0){
                vm = new ValidationMessage(item.getName(), value, true);
            }else{
                vm = new ValidationMessage(item.getName(), value, failMessage, false);
            }
        }
        
        return vm;
    }
}
