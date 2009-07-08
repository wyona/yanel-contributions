package org.wyona.yanel.impl.jelly;


public class RadioInputItem extends InputItemWithOneSelectableOption {
    public RadioInputItem(String name){
        super(name);
    }
    
    public RadioInputItem(String name, Option[] options) {
        super(name, options);
    }
    
    public RadioInputItem(String name, Option[] options, int selectIndex) {
        super(name, options, selectIndex);
    }
    
    public int getType() {
        return INPUT_TYPE_RADIO;
    }
}
