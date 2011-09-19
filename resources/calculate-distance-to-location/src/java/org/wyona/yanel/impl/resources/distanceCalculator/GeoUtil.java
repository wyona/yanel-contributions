/*
 * Copyright 2011 Wyona
 */
package org.wyona.yanel.impl.resources.distanceCalculator;

import org.wyona.yanel.impl.resources.distanceCalculator.Location;

import org.apache.log4j.Logger;

/**
 *
 */
public class GeoUtil {
    
    private static Logger log = Logger.getLogger(DistanceCalculatorResource.class);
    private double lat_here;
    private double lat_there;
    private double dlong;

    /**
    * @return distance between two locations
    */
   public static double getDistance(Location here, Location there){
      double radius = 6371;
      double lat_here = Math.toRadians(here.getLatitude());
      double lat_there = Math.toRadians(there.getLatitude());
      double dlong = Math.toRadians(there.getLongitude())-Math.toRadians(here.getLongitude());

      double dist = Math.acos(Math.sin(lat_here)*Math.sin(lat_there) + Math.cos(lat_here)*Math.cos(lat_there)
        * Math.cos(dlong))*radius;
      return dist;
   
   }
   
/*      public static double getDistance1(Location here, Location there){
      double radius = 6371;
      double lat_here = Math.toRadians(here.getLatitude());
      double lat_there = Math.toRadians(there.getLatitude());
      double dlong = Math.toRadians(there.getLongitude())-Math.toRadians(here.getLongitude());

      double dist = Math.acos(Math.sin(lat_here)*Math.sin(lat_there) + Math.cos(lat_here)*Math.cos(lat_there)
        * Math.cos(dlong))*radius;
      return dist;
   
   }*/

}
