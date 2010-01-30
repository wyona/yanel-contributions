package org.wyona.yanel.impl.resources;

/**
 *
 */
public class Continuation {

    private String id;

    /**
     *
     */
    public Continuation() {
        // TODO: Use UUID
        id = "" + new java.util.Date().getTime();
    }

    /**
     *
     */
    public Continuation(String id) {
        this.id = id;
    }

    /**
     *
     */
    public String getId() {
        return id;
    }
}
