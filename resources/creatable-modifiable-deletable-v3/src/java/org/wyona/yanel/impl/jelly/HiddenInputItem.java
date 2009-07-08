package org.wyona.yanel.impl.jelly;


public class HiddenInputItem extends TextualInputItemSupport{
    public HiddenInputItem(String name) {
        super(name);
    }
    
    public HiddenInputItem(String name, String value) {
        super(name, value);
    }
    
    public int getType() {
        return INPUT_TYPE_HIDDEN;
    }
}
