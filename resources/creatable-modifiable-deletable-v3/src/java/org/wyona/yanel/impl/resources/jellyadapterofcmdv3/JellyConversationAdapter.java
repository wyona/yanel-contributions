package org.wyona.yanel.impl.resources.jellyadapterofcmdv3;

import javax.xml.transform.Transformer;

import org.apache.commons.jelly.JellyContext;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Deals with conversations.
 * A conversation is mapped to a path.
 * */
//TODO: is path ok as a key of the conversation state
//TODO[high]: conversation state object must be synchronized, because it is put into the 
//session and may be accessed by different browser instances
abstract class JellyConversationAdapter extends JellyControllerAdapter {

    private static Logger log = LogManager.getLogger(JellyConversationAdapter.class);

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
     */
    protected final ConversationState getConversationState(){
        log.warn("DEBUG: Get conversation state ...");
        if(conversationState == null){
            log.warn("DEBUG: Get conversation state from session...");
            conversationState = (ConversationState)getEnvironment().getRequest().getSession(true).getAttribute(getPath());
        }
        return conversationState;
    }
    
    /**
     * Checks whether the conversation (resource input) is already attached to the session and adds it if necessary
     * @param model Resource input
     * @param usecase Usecase (e.g. create, modify)
     * @param resourcePath Adapted resource path
     */
    protected final ConversationState initConversation(Object model, Usecase usecase, String resourcePath) throws Exception {
        log.warn("DEBUG: Continuation: " + getContinuation().getId() + ", Resource path: " + resourcePath + ", Usecase: " + usecase);

        ConversationState cs = new ConversationState(model);
        cs.setUsecase(usecase);
        cs.setResourcePath(resourcePath);
        
        ConversationState currentState = getConversationState();
        if (currentState != null && !currentState.isInvalidated()) {
            // Refresh the properties, because they are changing during the conversation
            log.warn("DEBUG: Refresh the properties, because they are changing during the conversation");
            currentState.setModel(cs.getModel());
            currentState.setCurrentScreen(cs.getCurrentScreen());
        } else {
            // TODO: What if there is no referer, e.g. copy/paste from email ...
            cs.setRefererUrl(getEnvironment().getRequest().getHeader("Referer"));
            // The new conversation state becomes current
            currentState = cs;
        }
        
        String gotoUrl = getParameterAsString(PARAM_GOTO_URL);
        if(gotoUrl != null){
            currentState.setGotoUrl(gotoUrl);
        }
        
        log.warn("DEBUG: Attach conversation state (resource input) for usecase '" + currentState.getUsecase() + "' to session with id '" + getPath() + "' (Adapted resource path: " + resourcePath + ")");

/* DEBUG
        try {
            org.wyona.yanel.core.api.attributes.creatable.ResourceInput input = (org.wyona.yanel.core.api.attributes.creatable.ResourceInput)current.getModel();
            if (input.getItem("id") != null) {
                log.warn("DEBUG: ID: " + (String) input.getItem("id").getValue());
            } else {
                log.warn("DEBUG: No item with name 'id'");
            }
        } catch(Exception e) {
            log.error(e, e);
        }
*/
        if (getEnvironment().getRequest().getSession(true).getAttribute(getPath()) != null) {
            log.error("There is already a conversation state attached to session for path '" + getPath() + "'!");
            //throw new Exception("Conversation state for path '" + getPath() + "' already set!");
        }
        getEnvironment().getRequest().getSession(true).setAttribute(getPath(), currentState);

        return currentState;
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
    @Override
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
    @Override
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
