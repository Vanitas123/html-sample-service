/* Copyright (c) 2018 VMware, Inc. All rights reserved. */

package com.vmware.samples.htmlsample.model;

/**
 * Data model of a chassis object.
 */
public class Chassis {

   public String id;
   public String name;
   public String dimensions;
   public String serverType;
   public boolean isActive;

   public Chassis() {
      // A default constructor is needed for the JSON serialization to work.
   }
}
