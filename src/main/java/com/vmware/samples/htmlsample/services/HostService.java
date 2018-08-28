/* Copyright (c) 2018 VMware, Inc. All rights reserved. */
package com.vmware.samples.htmlsample.services;

import com.vmware.samples.htmlsample.model.Chassis;
import com.vmware.samples.htmlsample.model.Host;

import java.util.List;
import java.util.Map;

/**
 * Interface used to retrieve information about related hosts.
 */
public interface HostService {

   /**
    * Retrieves the related host for a given Chassis
    * @return related hosts
    */
   public List<Host> getRelatedHosts(Chassis chassis);
}
