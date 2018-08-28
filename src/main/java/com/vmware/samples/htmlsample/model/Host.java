/* Copyright (c) 2018 VMware, Inc. All rights reserved. */
package com.vmware.samples.htmlsample.model;

/**
 * Data model of a host object.
 */
public class Host {

   public String id;
   public String name;
   public String state;
   public String vCenterName;
   public String memorySize;
   public String numCpus;

   public Host(String id, String name, String state, String vCenterName, String memorySize, String numCpus) {
      this.id = id;
      this.name = name;
      this.state = state;
      this.vCenterName = vCenterName;
      this.memorySize = memorySize;
      this.numCpus = numCpus;
   }
}
