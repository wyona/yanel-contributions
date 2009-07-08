package org.wyona.yanel.impl.jelly;


public class TextFieldInputItem extends TextualInputItemSupport{
    public TextFieldInputItem(String name) {     
        super(name);
    }
    
    public TextFieldInputItem(String name, String value) {
        super(name, value);
    }
    
    public int getType() {
        return INPUT_TYPE_TEXT;
    }

    /**
     *
     */
    public String toString() {
        return getName() + ": " + getValue();
    }
}
