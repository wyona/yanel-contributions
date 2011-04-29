package org.wyona.yanel.impl.resources.jellyadapterofcmdv3;

import java.io.Serializable;

import org.wyona.yanel.impl.resources.jellyadapterofcmdv3.ResourceAdapter.Usecase;

/**
 * Conversation state is stored in the session.
 * During the conversation with the user the model is modified.
 * However, the model is transient.
 */
class ConversationState implements Serializable{
	private static final long serialVersionUID = 1L;

	//private static final long serialVersionUID = 1724827714145174641L;
    private Usecase usecase;

    /**
     * The path to the adapted resource, e.g. Creatable, Modifiable
     */
    private String resourcePath;
    private String refererUrl;
    private String gotoUrl;
    private String currentViewId;
    private String previousViewId;
    private transient Object model;
    private boolean invalidated;
    
    public ConversationState(Object model){
        this.model = model;
    }
    
    public ConversationState(String currentViewId, Object model){
        this.currentViewId = currentViewId;
        this.model = model;
    }
    
    public String getCurrentScreen() {
        return currentViewId;
    }

    public String getPreviousScreen() {
        return previousViewId;
    }
    
    public Object getModel() {
        return model;
    }
 
    /**
     * Get referer URL, which can be used for example to redirect if cancel is hit by a user
     */
    public String getRefererUrl() {
        return refererUrl;
    }

    /**
     * Set referer URL
     */
    void setRefererUrl(String refererUrl) {
        this.refererUrl = refererUrl;
        if(gotoUrl == null){
            this.gotoUrl = this.refererUrl;
        }
    }

    public String getGotoUrl() {
        return gotoUrl;
    }
    
    public void setGotoUrl(String gotoUrl) {
        this.gotoUrl = gotoUrl;
    }
    
    /**
     * Sets the previous and current view id
     * */
    void setCurrentScreen(String currentViewId) {
        this.previousViewId = this.currentViewId;
        this.currentViewId = currentViewId;
    }

    void setModel(Object model) {
        this.model = model;
    }
    
    void invalidate(){
        invalidated = true;
    }
    
    /**
     * Invalidated conversation state means that the state is removed from 
     * the session
     * */
    public boolean isInvalidated(){
        return invalidated;
    }

    public Usecase getUsecase() {
        return usecase;
    }

    public void setUsecase(Usecase usecase) {
        this.usecase = usecase;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}
