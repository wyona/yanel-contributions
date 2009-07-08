package org.wyona.yanel.impl.jelly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wyona.yanel.core.api.attributes.creatable.AbstractResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItemCollection;

/**
 * To be used for inputs of lists with undefined length. Many values can be
 * added. There is no specific tag associated with this InputItem. There should
 * be custom implementations in jelly files.
 */
public class FileCollectionInputItem extends AbstractResourceInputItem implements ResourceInputItemCollection {

    private List<FileItem> values = new ArrayList<FileItem>();

    public FileCollectionInputItem(String name) {
        super(name);
    }

    public boolean addValue(Object value) {
        boolean result = values.add((FileItem)value);
        if(result){
            removeValidationMessage();
        }
        return result;
    }

    public boolean removeValue(Object value) {
        boolean result = values.remove(value);
        if(result){
            removeValidationMessage();
        }
        return result;
    }

    public int getType() {
        return ResourceInputItem.INPUT_TYPE_COLLECTION;
    }

    public List<FileItem> getValue() {
        return Collections.unmodifiableList(values);
    }

    public void doSetValue(Object value) {
        try {
            if(value == null){
                values = new ArrayList<FileItem>();
            }else{
                values = new ArrayList<FileItem>((List<FileItem>)value);
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid value for item " + this.getName() + ". Value should implement List.");
        }
    }

    public boolean addValue(int index, Object value) {
        boolean result = false;
        if (index >= values.size()) {
            result = values.add((FileItem)value);
        } else {
            values.add(index, (FileItem)value);
            result = true;
        }
        if(result){
            removeValidationMessage();
        }
        return result;
    }

    public int indexOfValue(Object value) {
        return values.indexOf(value);
    }

    public boolean moveValue(int fromIndex, int toIndex) {
        if (toIndex < 0) toIndex = 0;
        if (toIndex > values.size() - 1) toIndex = values.size() - 1;
        
        FileItem o = values.remove(fromIndex);
        if (o == null) return false;
        
        values.add(toIndex, o);
        removeValidationMessage();
        return true;
    }

    public FileItem removeValue(int index) {
        FileItem removed = values.remove(index);
        if(removed != null){
            removeValidationMessage();
        }
        return removed; 
    }

    public int size() {
        return values.size();
    }
}
