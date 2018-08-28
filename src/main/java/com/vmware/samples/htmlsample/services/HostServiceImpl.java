/* Copyright (c) 2018 VMware, Inc. All rights reserved. */
package com.vmware.samples.htmlsample.services;


import java.util.ArrayList;
import java.util.List;

import com.vmware.samples.htmlsample.model.Chassis;
import com.vmware.samples.htmlsample.model.Host;
import com.vmware.samples.htmlsample.vim25.services.VimObjectService;
import com.vmware.vise.usersession.ServerInfo;
import com.vmware.vise.usersession.UserSessionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Service used to retrieve information about HostSystem vsphere objects
 */
public class HostServiceImpl implements HostService {
   private final UserSessionService _userSessionService;
   private final VimObjectService _vimObjectService;

   private static final Log _logger = LogFactory.getLog(HostServiceImpl.class);

   public HostServiceImpl(UserSessionService userSessionService, VimObjectService vimObjectService) {
      _userSessionService = userSessionService;
      _vimObjectService = vimObjectService;
   }

   /**
    * Retrieves all hosts related to a given chassis
    * @param chassis for which the related hosts will be returned
    * @return The related host for the given Chassis
    */
   @Override
   public List<Host> getRelatedHosts(Chassis chassis) {
      List<Host> hosts = new ArrayList<>();

      if (_userSessionService.getUserSession()== null || chassis == null) {
         _logger.warn(String.format("The %s is not defined.",
               (chassis == null) ? "chassis" : "UserSession"));
         return hosts;
      }

      for (ServerInfo serverInfoObject : _userSessionService.getUserSession().serversInfo) {
         final List<Host> retrievedHosts = _vimObjectService.retrieveHosts(serverInfoObject);
         _logger.info("Session Key >>> "+serverInfoObject.sessionKey);
         System.out.println("Session Key >>> "+serverInfoObject.sessionKey);
         hosts.addAll(retrievedHosts);
      }

      
      List<Host> hostsForChassis = getConnectedHosts(hosts);
      return hostsForChassis;
   }

   /**
    * This is an example logic which demonstrates that for a given chassis
    * we can have relation to a selected host.
    * This logic should be adjusted to your business case and the situation
    * in which your custom objects relate to a vSphere object.
    *
    * In this example the related hosts tab for a chassis shows only the connected hosts.
    *
    * @return a list of connected hosts
    */
   private List<Host> getConnectedHosts(List<Host> hosts) {
      List<Host> result = new ArrayList<>();
      final String expectedState = "connected";
      for (Host host : hosts) {
         if (expectedState.equals(host.state)) {
            // capitalize the first letter of the host state.
            host.state = Character.toUpperCase(host.state.charAt(0)) + host.state.substring(1);
            result.add(host);
         }
      }

      return result;
   }
}
