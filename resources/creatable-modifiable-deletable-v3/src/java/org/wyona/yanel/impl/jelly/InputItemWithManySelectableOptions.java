package org.wyona.yanel.impl.jelly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.wyona.yanel.core.api.attributes.creatable.AbstractResourceInputItem;


/**
 * This InputItem is used to specify an input which has a predefined selection
 * from where multiple values can be selected.
 */
//NOTE: implementing ResourceInputItemCollection confuses the adapter, because the input item obtains two types then.
public abstract class InputItemWithManySelectableOptions extends AbstractResourceInputItem
/*implements ResourceInputItemCollection*/{

    private List<Option> options = new ArrayList<Option>();

    private List<Option> selected = new ArrayList<Option>();

    /**
     * Creates a InputItemWithManySelectableOptions with given name and without options.
     * @param name
     */
    public InputItemWithManySelectableOptions(String name) {
        super(name);
    }
    
    /**
     * Creates a InputItemWithManySelectableOptions with given name and options.
     * @param name
     * @param options
     */
    public InputItemWithManySelectableOptions(String name, Option [] options) {
        super(name);
        if(options == null ){
            options = new Option[0];
        }
        
        this.options = Arrays.asList(options);
    }
    
    /**
     * 
     * @param option
     */
    public void add(Option option){
        if(!options.contains(option)){
            options.add(option);
        }
    }
    
    public void remove(Option option){
        options.remove(option);
        selected.remove(option);
    }
    
    public void clearOptions(){
        options.clear();
        selected.clear();
    }
/*    
    public boolean moveValue(int fromIndex, int toIndex) {
        boolean result = false;
        
        if(fromIndex != toIndex && fromIndex >= 0 && toIndex >= 0 && fromIndex < selected.size() && toIndex < selected.size()){
            Option o = selected.remove(fromIndex);
            selected.add(toIndex, o);
        }
        
        return result;
    }
    
    public Option removeValue(int index) {
        if(index < 0){
            index = 0;
        }
        if(index >= selected.size()){
            index = selected.size()-1;
        }
        return selected.remove(index);
    }
    
    public boolean removeValue(Object value) {
        boolean result = false;
        List<Option> valOption = getOnlyAllowedValues(value);
        for (Option o : valOption) {
            selected.remove(o);
            result = true;
        }
        return result;
    }
    
    public int indexOfValue(Object value) {
        List<Option> valOption = getOnlyAllowedValues(value);
        if(valOption.size() > 0){
            return selected.indexOf(valOption.get(0));
        }else{
            return -1;
        }
    }
    
    public boolean addValue(Object value) {
        boolean result = false;
        
        List<Option> valOption = getOnlyAllowedValues(value);
        for (Option o : valOption) {
            if(!selected.contains(o)){
                selected.add(o);
                result = true;
            }
        }
        
        return result;
    }
*/    
    
    /**
     * Deals with value as String, Option, String[], Option[] 
     * */
    public boolean addValue(int index, Object value) {
        if(index < 0){
            index = 0;
        }
        if(index > selected.size()){
            index = selected.size();
        }
        
        boolean result = false;
        
        List<Option> valOption = getOnlyAllowedValues(value);
        for (Option o : valOption) {
            if(!selected.contains(valOption)){
                selected.add(index++, o);
                result = true;
            }
        }
        
        return result;
    }
    
    private List<Option> getOnlyAllowedValues(Object value){
        List<Option> result = new ArrayList<Option>();
        
        List<Option> possible = Arrays.asList(getOptions());
        
        if (value instanceof Option[]){
            List<Option> valueAsList = new ArrayList<Option>(Arrays.asList((Option[])value));
            valueAsList.retainAll(possible);
            result = valueAsList;
        }else if (value instanceof String[]){
            List<String> valueAsList = new ArrayList<String>(Arrays.asList((String[])value));
            List<String> possibleStrings = new ArrayList<String>();
            for (Iterator<Option> i = possible.iterator(); i.hasNext();) {
                possibleStrings.add(i.next().getValue());
            }
            valueAsList.retainAll(possibleStrings);
            for (Iterator<Option> i = possible.iterator(); i.hasNext();) {
                Option vtp = i.next();
                if(valueAsList.contains(vtp.getValue())){
                    result.add(vtp);
                }
            }
        }else if(value instanceof Option){
            int counter = 0;
            for (Iterator<Option> i = possible.iterator(); i.hasNext();counter++) {
                Option vtp = i.next();
                if(vtp.equals(value)){
                    result.add(vtp);
                    break;
                }
            }
        }else if(value instanceof String){
            int counter = 0;
            for (Iterator<Option> i = possible.iterator(); i.hasNext();counter++) {
                Option vtp = i.next();
                if(vtp.getValue().equals(value)){
                    result.add(vtp);
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * @return an array of Option, it can be empty. <code>null</code> is never returned 
     * */
    public Option[] getValue() {
        return selected.toArray(new Option[selected.size()]);
    }

    /**
     * Deals with String, String[], Option, Option[]
     * */
    public void doSetValue(Object value) {
        selected = new ArrayList<Option>();
        if(value == null){
            return;
        }
        selected.addAll(getOnlyAllowedValues(value));
    }
    
    public Option [] getOptions(){
        return options.toArray(new Option [options.size()]);
    }
    
    public void sort(){
        Collections.sort(options);
    } 
}
