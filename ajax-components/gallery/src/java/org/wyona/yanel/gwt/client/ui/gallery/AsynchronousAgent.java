package org.wyona.yanel.gwt.client.ui.gallery;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Asynchronous agents are calling some URL in an asynchronous way. 
 * */
public abstract class AsynchronousAgent implements RequestCallback{
	private RequestBuilder requestBuilder = null;
	
	private String url = null;
	private Map/*<String, String>*/ parameters = new HashMap/*<String, String>*/();
	
	public AsynchronousAgent(String url){
		requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		initializeHeaders();
	}
	
	public AsynchronousAgent(String url, RequestBuilder.Method method){
		requestBuilder = new RequestBuilder(method, url);
		initializeHeaders();
	}
	
	/**
	 * If no request headers have been set, the header "Content-Type" will be used with a value of "text/plain; charset=utf-8"
	 * */
	protected void initializeHeaders(){
		requestBuilder.setHeader("Content-Type", "application/x-www-form-urlencoded");
	}

	/**
	 * Sending the parameter depends on a content type and the receiving party
	 * */
	protected String buildRequestData(){
		String data = "";
		
		Map copyOfParameters = new HashMap(parameters);
		for (Iterator i = copyOfParameters.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			
			data += entry.getKey() +""+ entry.getValue();
			
			if(i.hasNext()){
				data += "&";
			}
		}
		
		return data;
	}
	
	public final Request execute() throws RequestException{
		return requestBuilder.sendRequest(buildRequestData(), this);
	}
	
	public void addParameter(String name, String value){
		parameters.put(name, value);
	} 
	
	
}
