package org.wyona.yanel.core.api.attributes.creatable;

import junit.framework.TestCase;

import org.wyona.yanel.core.api.attributes.CreatableV3;
import org.wyona.yanel.impl.jelly.HiddenInputItem;
import org.wyona.yanel.impl.jelly.PasswordInputItem;
import org.wyona.yanel.impl.jelly.ResourceInputImpl;
import org.wyona.yanel.impl.jelly.TextAreaInputItem;
import org.wyona.yanel.impl.jelly.TextFieldInputItem;

public class SimpleCreatableV3Test extends TestCase {
    
    private CreatableV3 creatable = null;

    protected void setUp() throws Exception {
        super.setUp();
        creatable = new SimpleCreatable();
    }
    
    public void testGetResourceInput() {
        try {
            String[] names = creatable.getResourceInputForCreation().getItemNames();
            
            for (int i = 0; i < names.length; i++) {
                ResourceInputItem item = creatable.getResourceInputForCreation().getItem(names[i]);
                if (item.getType() == ResourceInputItem.INPUT_TYPE_TEXT) assertTrue(item.getName().equals("text1"));
                if (item.getType() == ResourceInputItem.INPUT_TYPE_HIDDEN) assertTrue(item.getName().equals("text2-hidden"));
                if (item.getType() == ResourceInputItem.INPUT_TYPE_PASSWORD) assertTrue(item.getName().equals("text3-password"));
                if (item.getType() == ResourceInputItem.INPUT_TYPE_TEXTAREA) assertTrue(item.getName().equals("text4-area"));
            }
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    
    
    private static class SimpleCreatable implements CreatableV3 {

        public void create(ResourceInput input) throws Exception {
            //do something
        }

        public ResourceInput getResourceInputForCreation() throws Exception {
            //create the bean
            ResourceInputImpl resourceInput = new ResourceInputImpl();
            
            //add some items
            TextFieldInputItem item1 = new TextFieldInputItem("text1");
            HiddenInputItem item2 = new HiddenInputItem("text2-hidden");
            PasswordInputItem item3 = new PasswordInputItem("text3-password");
            TextAreaInputItem item4 = new TextAreaInputItem("text4-area");

            resourceInput.add(item1);
            resourceInput.add(item2);
            resourceInput.add(item3);
            resourceInput.add(item4);
            
            return resourceInput;
        }
        
    }
}
