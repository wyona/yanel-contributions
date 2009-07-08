package org.wyona.yanel.impl.jelly;


public class PasswordInputItem extends TextualInputItemSupport{
    public PasswordInputItem(String name) {
        super(name);
    }
    
    public PasswordInputItem(String name, String value) {
        super(name, value);
    }
    
    public int getType() {
        return INPUT_TYPE_PASSWORD;
    }
}
