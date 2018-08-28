/* Copyright (c) 2018 VMware, Inc. All rights reserved. */
package com.vmware.samples.htmlsample.vim25.services;

import com.vmware.samples.htmlsample.model.Host;
import com.vmware.vise.usersession.ServerInfo;

import java.util.List;
import java.util.Map;

/**
 * Interface used to perform operations on vsphere objects
 */
public interface VimObjectService {
   List<Map<String, Object>> retrieveObjectProperties(ServerInfo serverInfo,
         String vSphereObject, String vSphereObjectProperties[]);

   List<Host> retrieveHosts(ServerInfo serverInfoObject);
}
