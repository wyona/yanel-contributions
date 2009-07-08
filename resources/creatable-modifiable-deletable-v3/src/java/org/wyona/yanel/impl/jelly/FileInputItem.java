package org.wyona.yanel.impl.jelly;

import org.wyona.yanel.core.api.attributes.creatable.AbstractResourceInputItem;

/**
 * Represents a file input.
 * 
 */
public class FileInputItem extends AbstractResourceInputItem {
    
    private FileItem fileItem = null;
    
    public FileInputItem(String name) {
        super(name);
    }
    
    public FileInputItem(String name, FileItem value) {
        super(name);
        this.fileItem = value;
    }
    
    public int getType() {
        return INPUT_TYPE_FILE_UPLOAD;
    }
    
    /**
     * the return value will be of type FileItem.
     */
    public Object getValue() {
        return fileItem;
    }

    /**
     * the method expects a parameter of type FileItem.
     */
    public void doSetValue(Object value) {
        if(value == null){
            this.fileItem = null;
            return;
        }
        
        if (!(value instanceof FileItem)) throw new IllegalArgumentException("Passed argument is not an instance of fileItem: " + value.toString());
        fileItem = (FileItem) value;
    }
    
}
