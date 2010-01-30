package org.wyona.yanel.impl.resources.jellyadapterofcmdv3;


/**
 * The adapter knows how to adapt resources. It works as a facade
 * for adapted resource that are implementing some specific functionality. 
 * E.g. a resource can be creatable, deletable, modifiable...
 * <p>
 * Normally the adapter should be instantiated by Yanel servlet    
 * */
public interface ResourceAdapter{    
    public static final String PARAM_ADAPTED_RESOURCE_PATH = "adapted.resource.path";
    
    public static enum Usecase{
        /**
         * If this usecase is specified, then a resource of the specified type will create something.
         */
        create,
        /**
         * If this usecase is specified, then a resource of the specified type will modify something.
         */
        modify,
        /**
         * If this usecase is specified, then a resource of the specified type will delete something.
         */
        //TODO: the value must be "delete", but YanelServlet does not allow that?
        remove;
        
        /**
         * Create the usecase out of the given parameter ignoring the case of the string.
         * Behaves much like simple valueOf
         * @return null when the parameter is null, otherwise tries to create the usecase.
         * */
        public static Usecase caseInsensitiveValueOf(String usecase){
            if(usecase == null){
                return null;
            }
            
            return Usecase.valueOf(usecase.toLowerCase());
        }
    }
    
    public String getAdaptedResourcePath();
    public void setAdaptedResourcePath(String adaptedResourcePath);
    
    /**
     * The adapter knows how to adapt the resource for specific usecases
     * */
    public Usecase getUsecase();
    public void setUsecase(Usecase usecase);
}
