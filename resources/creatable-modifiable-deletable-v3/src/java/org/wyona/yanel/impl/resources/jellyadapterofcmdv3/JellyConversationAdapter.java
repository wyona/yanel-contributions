package org.wyona.yanel.impl.resources.jellyadapterofcmdv3;

import javax.xml.transform.Transformer;

import org.apache.commons.jelly.JellyContext;

import org.apache.log4j.Logger;

/**
 * Deals with conversations.
 * A conversation is mapped to a path.
 * */
//TODO: is path ok as a key of the conversation state
//TODO[high]: conversation state object must be synchronized, because it is put into the 
//session and may be accessed by different browser instances
abstract class JellyConversationAdapter extends JellyControllerAdapter {

    private static Logger log = Logger.getLogger(JellyConversationAdapter.class);

    /**
     * This is the view where the user should get after all the activities are finished.
     * */
    protected static final String DONE_VIEW_ID = "done";
    
    /**
     * This is the view where the user should get when the conversation is canceled.
     * */
    protected static final String CANCEL_VIEW_ID = "cancel";
    
    /**
     * This parameter is useful when the referrer url is not enough.
     * For example, the page may needs to go to some page (other than a referrer) after the conversation has been completed.  
     * */
    public static final String PARAM_GOTO_URL = "goto.url";
    
    private ConversationState conversationState;
    
    public JellyConversationAdapter() {
        super();
        supportedViewIds.add(CANCEL_VIEW_ID);
        supportedViewIds.add(DONE_VIEW_ID);
    }
    
    /**
     * Initialize or keep up to date the conversation state
     */
    protected abstract void init() throws Exception;
    
    /**
     * In this method the conversation should be commited. Data may need to be validated befor commit.
     */
    public abstract void commit()throws Exception;
    
    
    /**
     * Returns the conversation state object. It may be available in the session
     * or invalidated (no more in the session).
     * @return - <code>null</code> when called before initConversation(), in other cases the conversation state object (maybe invalidated already)
     * */
    protected final ConversationState getConversationState(){
        if(conversationState == null){
            conversationState = (ConversationState)getEnvironment().getRequest().getSession(true).getAttribute(getPath());
        }
        return conversationState;
    }
    
    /**
     * Checks if the conversation is already in the session and creates it if necessary
     * */
    protected final ConversationState initConversation(Object model, Usecase usecase, String resourcePath){
        log.warn("Continuation: " + getContinuation().getId());

        ConversationState cs = new ConversationState(model);
        cs.setUsecase(usecase);
        cs.setResourcePath(resourcePath);
        
        ConversationState current = getConversationState();
        if (current != null && !current.isInvalidated()) {
            // Refresh the properties, because they are changing during the conversation
            current.setModel(cs.getModel());
            current.setCurrentScreen(cs.getCurrentScreen());
        } else {
            cs.setRefererUrl(getEnvironment().getRequest().getHeader("Referer"));
            // The new conversation state becomes current
            current = cs;
        }
        
        String gotoUrl = getParameterAsString(PARAM_GOTO_URL);
        if(gotoUrl != null){
            current.setGotoUrl(gotoUrl);
        }
        
        log.warn("Attach conversation state for usecase '" + current.getUsecase() + "' to session with id '" + getPath() + "'");
        getEnvironment().getRequest().getSession(true).setAttribute(getPath(), current);
        return current;
    }
    
    /**
     * Removes the conversation state from the session and invalidates the conversation state object.
     * */
    protected final void destroyConversation(){
        ConversationState current = getConversationState();
        if(current != null){
            current.invalidate();
        }
        getEnvironment().getRequest().getSession(true).removeAttribute(getPath());
    }
    
    /**
     * No additional parameters passed. Subclasses should consult conversation state for additional parameters
     */
    protected void passParameters(JellyContext jellyContext) throws Exception {
        super.passParameters(jellyContext);
        ConversationState cs = getConversationState();
        if (cs != null) {
            if(cs.getGotoUrl() != null){
                jellyContext.setVariable(PARAM_GOTO_URL, cs.getGotoUrl());
            }
        } else {
            log.warn("The conversation was not initialized");
        }
        
    }
    
    /**
     * No additional parameters passed. Subclasses should consult conversation state for additional parameters
     */
    protected void passParameters(Transformer transformer) throws Exception {
        super.passParameters(transformer);
        ConversationState cs = getConversationState();
        if (cs != null) {
            if(cs.getGotoUrl() != null){
                transformer.setParameter(PARAM_GOTO_URL, cs.getGotoUrl());
            }
        } else {
            log.warn("The conversation was not initialized");
        }
    }
}
