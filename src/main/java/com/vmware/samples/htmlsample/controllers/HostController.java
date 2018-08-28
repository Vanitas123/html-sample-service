/* Copyright (c) 2018 VMware, Inc. All rights reserved. */

package com.vmware.samples.htmlsample.controllers;

import com.vmware.samples.htmlsample.model.Chassis;
import com.vmware.samples.htmlsample.model.Host;
import com.vmware.samples.htmlsample.services.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * A controller which returns information about vsphere host objects.
 */
@Controller
public class HostController extends BaseRestController {
   private final HostService _hostService;

   @Autowired
   public HostController(HostService hostService) {
      _hostService = hostService;
   }

   /**
    * Retrieves all host objects related to a a given chassis.
    * @param chassis to which will be related host objects
    * @return list of host objects.
    */
   @RequestMapping(value = "hosts", method = RequestMethod.POST)
   @ResponseBody
   public List<Host> getHostsList(
         @RequestBody Chassis chassis) throws Exception {
      return _hostService.getRelatedHosts(chassis);
   }
}

