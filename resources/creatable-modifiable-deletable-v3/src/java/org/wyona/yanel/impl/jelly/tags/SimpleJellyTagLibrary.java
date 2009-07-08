package org.wyona.yanel.impl.jelly.tags;

import org.apache.commons.jelly.TagLibrary;

public class SimpleJellyTagLibrary extends TagLibrary {

    public SimpleJellyTagLibrary() {
        registerTag("textfield", TextFieldTag.class);
        registerTag("password", PasswordTag.class);
        registerTag("hidden", HiddenTag.class);
        registerTag("textarea", TextAreaTag.class);
        registerTag("radio", RadioTag.class);
        registerTag("checkbox", CheckboxTag.class);
        registerTag("select", SelectTag.class);
        registerTag("listbox", ListboxTag.class);
        registerTag("fileupload", FileUploadTag.class);
        registerTag("ajax-get", AjaxGetTag.class);
    }
}
