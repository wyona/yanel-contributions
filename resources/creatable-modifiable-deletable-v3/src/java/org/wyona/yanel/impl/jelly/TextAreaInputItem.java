package org.wyona.yanel.impl.jelly;


public class TextAreaInputItem extends TextualInputItemSupport{
    public TextAreaInputItem(String name) {
        super(name);
    }
    
    public TextAreaInputItem(String name, String value) {
        super(name, value);
    }
    
    public int getType() {
        return INPUT_TYPE_TEXTAREA;
    }
}
