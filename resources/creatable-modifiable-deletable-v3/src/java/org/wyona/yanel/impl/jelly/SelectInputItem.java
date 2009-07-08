package org.wyona.yanel.impl.jelly;


/**
 * Is this the same as a Combo Box aka Drop-down list?
 */
public class SelectInputItem extends InputItemWithOneSelectableOption {
    public SelectInputItem(String name){
        super(name);
    }
    
    public SelectInputItem(String name, Option[] options) {
        super(name, options);
    }
    
    public SelectInputItem(String name, Option[] options, int selectIndex) {
        super(name, options, selectIndex);
    }
    
    public int getType() {
        return INPUT_TYPE_SELECT;
    }
}
