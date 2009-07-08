package org.wyona.yanel.impl.jelly;

/**
 * Represents a Listbox (HTML: <select multiple="multiple" />)
 *
 */
public class ListboxInputItem extends InputItemWithManySelectableOptions{
    /**
     * Creates a ListBoxInputitem with the given name.
     * @param name
     */
    public ListboxInputItem(String name){
        super(name);
    }
    /**
     * Creates a ListBoxInputitem with the given name and option.
     * @param name
     * @param options
     */
    public ListboxInputItem(String name, Option [] options) {
        super(name, options);
    }
    
    /* 
     * @see org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem#getType()
     */
    public int getType() {
        return INPUT_TYPE_LISTBOX;
    }
}
