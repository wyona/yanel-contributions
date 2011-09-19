/*
 * Copyright 2011 Wyona
 */
package org.wyona.yanel.impl.resources.distanceCalculator;

import org.wyona.yanel.impl.resources.distanceCalculator.Location;
import org.wyona.yanel.impl.resources.distanceCalculator.GeoUtil;
import org.wyona.yanel.core.Resource;

import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import javax.servlet.http.HttpServletRequest;
import org.wyona.yarep.core.Node;

import org.wyona.yarep.core.Repository;
import org.wyona.yanel.core.util.YarepUtil;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.io.StringBufferInputStream;
import org.wyona.yanel.core.util.PathUtil;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;

import org.wyona.commons.xml.XMLHelper;
import java.io.File;
import java.io.FileInputStream;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.apache.xml.resolver.tools.CatalogResolver;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 */
public class DistanceCalculatorResource extends BasicXMLResource   {
    
    private static Logger log = Logger.getLogger(DistanceCalculatorResource.class);
      
     /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        HttpServletRequest request = (HttpServletRequest)getEnvironment().getRequest();
        String query = request.getQueryString();
        if((query == "" || query == null) && !(viewId == "json")){
            String view = first_view(request);
            return new StringBufferInputStream(view);
        }else{
            String long_here = "";
            String lat_here = "";
            long_here  = request.getParameter("long");
            lat_here = request.getParameter("lat");
            //long_here = "13.408056";
            //lat_here = "52.518611";
            log.debug("Longitude: " + long_here);
            log.debug("Latitude: " + lat_here);
            if(long_here == "" || lat_here == "" || long_here == null || lat_here == null){
                StringBuilder sbView_disabled = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><div><h1>Look for the most  convenient Store, according to your current position:</h1><p><ul>");
                sbView_disabled.append( "<li>The geolocation of your firefox is disabled. We're sorry but we can't provide you with the closest location.</li></ul></p></div></body></html>");
                return  new StringBufferInputStream(sbView_disabled.toString()); 
            }
            else{
                Location users_loc = new Location(Double.parseDouble(lat_here), Double.parseDouble(long_here));
                Document xmlDoc;
                String xmlPath = getResourceConfigProperty("xml-path");
                if (xmlPath == null || xmlPath == ""){
                    File xmlFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xml" + File.separator + "locations.xml");
                    xmlDoc = XMLHelper.readDocument(new FileInputStream(xmlFile));
                    log.warn("No custom/specific locations file specified, hence default file is read: " + rtd.getConfigFile().getParentFile().getAbsolutePath() + "xml" + File.separator + "locations.xml" );
                } else {
                    xmlDoc = XMLHelper.readDocument(getRealm().getRepository().getNode(xmlPath).getInputStream());
                }
                Element element = xmlDoc.getDocumentElement();
                Element[] childElements = XMLHelper.getElements(element,"node","","");
                //Get all the locations specified in the xmlFile
                // fill ArrayLists with the Locations (including Name)
                ArrayList<Location> locations = new ArrayList();
                int i;
                for(i = 0; i < childElements.length; ++i){
                    String name = "";
                    String longitude = "";
                    String latitude = "";
                    NodeList text_Node_list;
                    org.w3c.dom.Node final_text_node;
                    Element current_text_element = null;
                    Element current_element = childElements[i];


                    // INFO: Get the name
                    Element[] name_elements = XMLHelper.getChildElements(current_element, "name", null);
                    String contentLanguage = getContentLanguage();
                    log.debug("Number of 'name' elements: " + name_elements.length + " (Content language : " + contentLanguage + ")");
                    if (name_elements.length >= 1) {
                        try {
                            name = getElementValue(name_elements[0]);
                            if (name_elements.length > 1) {
                                for (int j = 0; j < name_elements.length; j++) {
                                    String langAttr = name_elements[j].getAttribute("xml:lang");
                                    log.debug("Language of name element: " + langAttr);
                                    if (langAttr != null && langAttr.equals(contentLanguage)) {
                                        name = getElementValue(name_elements[j]);
                                        break;
                                    }
                                }
                            } else {
                                log.info("Name '" + name + "' seems to exist only in one language.");
                            }
                        } catch(Exception e) {
                            log.error(e.getMessage());
                            return new StringBufferInputStream(getErrorMessageAsXHTML(e).toString());
                        }
                    } else {
                        log.error("Location does not seem to have name!");
                        StringBuilder sbView_error2 = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><div><h1>Look for the most  convenient Store, according to your current position:</h1><p><ul>");
                        sbView_error2.append( "<li>We're sorry an error occured.</li></ul></p></div></body></html>");
                        return  new StringBufferInputStream(sbView_error2.toString()); 
                    }


                    // INFO: Get the latitude
                    Element[] text_elements = XMLHelper.getElements(current_element,"latitude","","");
                    current_text_element = text_elements[0];
                    text_Node_list = current_text_element.getChildNodes();
                    final_text_node = text_Node_list.item(0);
                    //log.warn("How many names elements:" + java.lang.Integer.toString((Integer)(text_Node_list.getLength())));
                    latitude = final_text_node.getNodeValue();
                    //Get the longitude
                    text_elements = XMLHelper.getElements(current_element,"longitude","","");
                    current_text_element = text_elements[0];
                    text_Node_list = current_text_element.getChildNodes();
                    final_text_node = text_Node_list.item(0);
                    //log.warn("How many names elements:" + java.lang.Integer.toString((Integer)(text_Node_list.getLength())));
                    longitude = final_text_node.getNodeValue();
                    //log.warn("This is the "+ ((Integer)i).toString() +"location with: " + longitude + latitude+ "long, lat");
                    // Collect all the predifined Location, we need to compare to the User's Location
                    locations.add(new Location(Double.parseDouble(latitude), Double.parseDouble(longitude), name, current_element.getAttribute("id")));
                }
                int n = locations.toArray().length;
                PriorityQueue<Location> priority_locs = new PriorityQueue();
                // Calculate Distance for all Locations;
                for (i = 0; i < n; i++){
                    Location current_location = locations.get(i);
                    double dist = GeoUtil.getDistance(users_loc, current_location);
                    // Set distance in order to copmare it accordingly (Comparision of a Location-Class Instance is difined by their its set distance)
                    current_location.setDistance(dist);
                    priority_locs.add(current_location);
                }

                if (viewId.equals("json")) {
                    log.warn("DEBUG: Get JSON view...");
                    return getXMLViewAsInputStream(priority_locs);
                    //return new StringBufferInputStream(getXMLView(priority_locs));
                } else {
                    log.warn("DEBUG: Get XML/XHTML view...");            
                    return new StringBufferInputStream(getXHTMLView(priority_locs));
                }
            }
        }
    } 

    /**
     * Get XML view
     */
    private InputStream getXMLViewAsInputStream(PriorityQueue priority_locs) throws Exception {
        Document doc = XMLHelper.createDocument(null, "locations");
        //Retrieve all the locations from the queue, the retrieved locations are sorted (closest Location is retrieved first)
        int numberOfLocations = priority_locs.size(); // INFO: Please note that poll() will change the size, hence set it as fixed variable first!
        for(int i = 0; i < numberOfLocations; ++i){
            Location loc = (Location)priority_locs.poll();
            String name = loc.getName();
            String distance = roundToOneDecimal(loc.getDistance());
            //log.debug("Distance: " + loc.getDistance() + ", " + distance);
            //sbView.append("<loc name=\"" + name + "\" distance=\"" + distance + "\" id=\"" + loc.getID() + "\" href=\"" + "TODO" + "\"/>");
            Element locElem = doc.createElement("loc");
            locElem.setAttribute("name", name);
            locElem.setAttribute("distance", distance);
            locElem.setAttribute("id", loc.getID());
            //locElem.setAttribute("href", "TODO");
            doc.getDocumentElement().appendChild(locElem);
        }
        return XMLHelper.getInputStream(doc, false, false, null);
    }

    /**
     * @deprecated Does not support special characters
     * Get XML view
     */
/*
    private String getXMLView(PriorityQueue priority_locs) {
        StringBuilder sbView = new StringBuilder("<?xml version=\"1.0\"?>");
        sbView.append("<locations>");
        int n = priority_locs.size();
        //Retrieve all the locations from the queue, the retrieved locations are sorted (closest Location is retrieved first)
        for(int i = 0; i < n; ++i){
            Location loc = (Location)priority_locs.poll();
            String name = loc.getName();
            String distance = roundToOneDecimal(loc.getDistance());
            //log.debug("Distance: " + loc.getDistance() + ", " + distance);
            sbView.append("<loc name=\"" + name + "\" distance=\"" + distance + "\" id=\"" + loc.getID() + "\" href=\"" + "TODO" + "\"/>");
      }
      sbView.append("</locations>");
      return sbView.toString();
    }
*/

    /**
     * Get XHTML view
     */
    private String getXHTMLView(PriorityQueue priority_locs) {
        StringBuilder sbView = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><div><h1>Look for the most  convenient Store, according to your current position:</h1><p><ul>");
        int n = priority_locs.size();
        //Retrieve all the locations from the queue, the retrieved locations are sorted (closest Location is retrieved first)
        for(int i = 0; i < n; ++i){
            Location loc = (Location)priority_locs.poll();
            String name = loc.getName();
            String distance = ((Double)loc.getDistance()).toString();
            sbView.append("<li>" + name + ":&#160;&#160;&#160;&#160;" + distance + " km</li>");
      }
      sbView.append("</ul></p></div></body></html>");
      return sbView.toString();
    }
    
    /**
     * Get form to input longitude and latitude
     */
    private String first_view(HttpServletRequest request){
      StringBuilder sbView = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
      // JavaScript of Mozilla in order to find out users location & incl. sendig post request
      sbView.append("<head><script type=\"text/javascript\">");
      sbView.append( "function getLoc(){");
      sbView.append(  "if (navigator.geolocation) {");
      sbView.append(  "/* geolocation is available */");
      sbView.append(  "navigator.geolocation.getCurrentPosition(successFunction, errorFunction);");
      sbView.append(  "} else {");
      sbView.append(  "alert(\"I'm sorry, but geolocation services are not supported by your browser.\");");
      sbView.append( "}");
      sbView.append("}");
                  // find out users location
      sbView.append( "function successFunction(position) {");
      sbView.append( "var lat = position.coords.latitude;");
      sbView.append( "var long = position.coords.longitude;");
                  /*+ "alert('Your latitude is :'+lat+' and longitude is '+long);"*/
                  // Sending Post request with users data
      sbView.append( "method = \"GET\";"); // Set method to GET by default.
      sbView.append( "var form = document.createElement(\"form\");");
      sbView.append( "form.setAttribute(\"method\", method);");
      sbView.append( "form.setAttribute(\"action\", \"\");");
      sbView.append( "var hiddenField = document.createElement(\"input\");");
      sbView.append( "hiddenField.setAttribute(\"type\", \"hidden\");");
      sbView.append( "hiddenField.setAttribute(\"name\", \"long\");");
      sbView.append( "hiddenField.setAttribute(\"value\", long);");
      sbView.append( "form.appendChild(hiddenField);");
      sbView.append( "hiddenField = document.createElement(\"input\");");
      sbView.append( "hiddenField.setAttribute(\"type\", \"hidden\");");
      sbView.append( "hiddenField.setAttribute(\"name\", \"lat\");");
      sbView.append( "hiddenField.setAttribute(\"value\", lat);");
      sbView.append( "form.appendChild(hiddenField);");
      sbView.append( "document.body.appendChild(form);");
      sbView.append( "form.submit();");
      sbView.append( "}");
      sbView.append( "function errorFunction(position) {");
      sbView.append( "alert('Error!')");
      sbView.append( "}");
      sbView.append( "</script></head>");

      sbView.append( "<body><div><h1>Look for the most  convenient Store, according to your current position:</h1><p>");
      sbView.append( "If you want to find the closest store next to your current location click on");
      sbView.append( "<p><input type=\"button\" value=\"Calculate based on my browser location\" onclick=\"getLoc()\" /></p>");

      sbView.append( "or otherwise fill in the Longitude and the Latitude. (UNIT: decimals of arcminute)");
      
      // the form to insert any Coordinates.
      sbView.append( "<p><form name=\"yoonel\" method=\"GET\" action=\"\">");
      sbView.append( "<table border=\"1\">");
      sbView.append( "<tr><td>Longitude:&#160;</td><td><input type=\"text\" name=\"long\" class=\"box\" size=\"15\"/></td><td>Example: <a href=\"http://www.mygeoposition.com/\">Wyona office Zurich</a>: 8.516852</td></tr>");
      sbView.append( "<tr><td>Latitude:&#160;</td><td><input type=\"text\" name=\"lat\" class=\"box\" size=\"15\"/></td><td>Example: <a href=\"http://www.mygeoposition.com/\">Wyona office Zurich</a>: 47.385719</td></tr>");
      sbView.append( "<tr><td>&#160;</td><td><input type=\"submit\" name=\"submit\" value=\"Submit\"></input>&#160;");
      sbView.append( "<input type=\"reset\" name=\"cancel\" value=\"Cancel\"/></td><td>&#160;</td></tr>");
      sbView.append( "</table>");
      sbView.append( "</form></p>");
      sbView.append( "</p></div></body></html>");
      return sbView.toString();
    }

    /**
     * Round to one decimal (whereas the "scientific/US" notation is used, which means a dot instead a comma)
     * @param d Double to be rounded
     */
    private String roundToOneDecimal(double d) {
        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.US); // INFO: Locale.GERMANY would use a comma instead a dot
        java.text.DecimalFormat df = (java.text.DecimalFormat)nf;
        df.applyPattern("#.#");
        return df.format(d);
    }

    /**
     * Get error message as XHTML
     * @param e Exception
     */
    private StringBuilder getErrorMessageAsXHTML(Exception e) {
        StringBuilder sb = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><div><h1>Look for the most  convenient Store, according to your current position:</h1><p><ul>");
        sb.append( "<li>We're sorry an error occured: " + e.getMessage() + "</li></ul></p></div></body></html>");
        return sb;
    }

    /**
     * Get text value of element
     */
    private String getElementValue(Element elem) throws Exception {
        NodeList text_Node_list = elem.getChildNodes();
        org.w3c.dom.Node final_text_node = text_Node_list.item(0);
        //log.debug("How many names elements:" + java.lang.Integer.toString((Integer)(text_Node_list.getLength())));
        if (text_Node_list.getLength() == 1) {
            return final_text_node.getNodeValue();
        }
        throw new Exception("Element '" + elem + "' has more than one child!");
    }
}
