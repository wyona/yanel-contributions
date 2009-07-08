package org.wyona.yanel.impl.jelly;

/**
 * Represents an Checkbox where you can select multiple values from a list.
 *
 */

public class CheckboxInputItem extends InputItemWithManySelectableOptions{
    /**
     * Creates a Checkbox without options. Options can be added with Method add(Option option).
     * @param name
     */
    public CheckboxInputItem(String name) {
        super(name);
    }
    
    /**
     * Creates a Checkbox with the specified name and Options.
     * @param name
     * @param options
     */
    public CheckboxInputItem(String name, Option [] options) {
        super(name, options);
    }
    
    /* 
     * @see org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem#getType()
     */
    public int getType() {
        return INPUT_TYPE_CHECK;
    }
}
