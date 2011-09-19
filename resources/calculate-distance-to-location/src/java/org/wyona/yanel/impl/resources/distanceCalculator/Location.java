/*
 * Copyright 2011 Wyona
 */
package org.wyona.yanel.impl.resources.distanceCalculator;

/**
 * Note: this class has a natural ordering that is inconsistent with equals
 *(if no distance is specified, the distance is always equal to POSITIVE_INFINITY, eventhough this might not be the true distance, it's just means: UNKONWN.)
 */
public class Location implements Comparable{
    
    private double latitude;
    private double longitude;
    private String name = "";
    private String id;
    private double distance = Double.POSITIVE_INFINITY;
    
    
    Location(double latitude, double longitude){
    
    // One should may be add a unit checking and convertion!
      this.latitude = latitude;
      this.longitude = longitude;    
    }

    /**
     * @param name Name of location, e.g. 'New York'
     * @param id ID of location, e.g. "new-york'
     */
    Location(double latitude, double longitude, String name, String id) {
        // TODO: One should may be add a unit checking and convertion!
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.id = id;
    }

    /**
     * Get ID
     */
    public String getID(){
      return this.id;
    }

    /**
     * Get latitude
     */
    public double getLatitude(){
      return this.latitude;
    }
    
    public double getLongitude(){
      return this.longitude;
    
    }
    
    public double getDistance(){
      return this.distance;
    
    }
    public String getName(){
      return this.name;
    }
    
    public void setLatitude(double latitude){
      this.latitude = latitude;
    
    }

    public void setLongitude(double longitude){
      this.longitude = longitude;
    
    }

    public void setDistance(double distance){
      this.distance = distance;
    
    }

    public void setName(String name){
      this.name = name;
    
    }

    public int compareTo(Object otherLocation){
        /*
        If passed object is of type other than Location, throw ClassCastException.
        */
       
        if(!(otherLocation instanceof Location)){
            throw new ClassCastException("Invalid object");
        }
        double x_value = this.distance;
        double y_value = ((Location) otherLocation).distance;
       
        if(x_value > y_value)    
            return 1;
        else if ( x_value < y_value )
            return -1;
        else
            return 0;
    }

}

