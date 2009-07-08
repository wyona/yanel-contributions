package org.wyona.yanel.impl.jelly;


/**
 * An option represents a Pair of a value and a corresponding Label. Used for
 * options on input items to give possible values.
 * 
 */
public class Option implements Comparable<Option>{

    private String value = null;
    private String label = null;

    /**
     * Creats a Option with given name and label.
     * @param value
     * @param label
     */
    public Option(String value) {
        this(value, value);
    }
    
    public Option(String value, String label) throws NullPointerException {
        super();
        if(value == null || label == null){
            throw new NullPointerException();
        }
        this.value = value;
        this.label = label;
    }

    /**
     * @return the value of the option.
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the label of the option.
     */
    public String getLabel() {
        return label;
    }

    public String toString() {
        return getValue();
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final Option other = (Option) obj;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    public int compareTo(Option o) {
        return getLabel().compareTo(o.getLabel());
    }
}