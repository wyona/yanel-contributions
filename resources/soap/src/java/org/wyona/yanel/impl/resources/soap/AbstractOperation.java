package org.wyona.yanel.impl.resources.soap;

import org.apache.log4j.Logger;

public abstract class AbstractOperation implements IOperation {
    protected static final Logger log = Logger.getLogger(AbstractOperation.class);
    
    protected MessageContext context = null;
    protected AbstractOperation() {
    }
    
    public AbstractOperation(MessageContext context){
        this.context = context;
    }
}
