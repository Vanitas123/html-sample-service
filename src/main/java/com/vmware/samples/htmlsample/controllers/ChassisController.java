/* Copyright (c) 2018 VMware, Inc. All rights reserved. */

package com.vmware.samples.htmlsample.controllers;

import com.vmware.samples.htmlsample.model.Chassis;
import com.vmware.samples.htmlsample.services.ChassisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * A controller to serve HTTP JSON GET/POST requests to the endpoint "/".
 */
@Controller
public class ChassisController extends BaseRestController {
   private final ChassisService _chassisService;

   @Autowired
   public ChassisController(ChassisService chassisService) {
      _chassisService = chassisService;
   }

   /**
    * Retrieves a chassis data by a given chassis id.
    *
    * @param objectId   id of the chassis object.
    * @return  the chassis object.
    */
   @RequestMapping(value = "/{objectId}", method = RequestMethod.GET)
   @ResponseBody
   public Chassis getChassisById(@PathVariable("objectId") String objectId) throws Exception {
      return _chassisService.getChassisById(objectId);
   }

   /**
    * Retrieves all chassis objects.
    *
    * @return list of chassis objects or null if there are none.
    * @throws Exception
    */
   @RequestMapping(value = "/list", method = RequestMethod.GET)
   @ResponseBody
   public List<Chassis> getChassisList() throws Exception {
      return _chassisService.getAllChassis();
   }

   /**
    * Creates a new chassis object.
    *
    * @param chassis  the new chassis.
    * @return the id of the created chassis or null if no chassis object is created.
    */
   @RequestMapping(value = "/create", method = RequestMethod.POST)
   @ResponseBody
   public String create(@RequestBody Chassis chassis) {
      return _chassisService.create(chassis);
   }

   /**
    * Edits a chassis object.
    *
    * @param chassis chassis object to be updated.
    * @return true if chassis object was successfully updated or false otherwise.
    */
   @RequestMapping(value = "/edit", method = RequestMethod.POST)
   @ResponseBody
   public boolean edit(@RequestBody Chassis chassis) {
      return _chassisService.update(chassis);
   }

   /**
    * Deletes a chassis object.
    *
    * @param targetIds ids of chassis objects to be deleted.
    * @return true if at least one chassis object was successfully deleted or false otherwise.
    */
   @RequestMapping(value = "/delete", method = RequestMethod.POST)
   @ResponseBody
   public boolean delete(@RequestBody String targetIds[]) {
      boolean result = false;
      for(String targetId: targetIds) {
         result = _chassisService.delete(targetId) || result;
      }
      return result;
   }

   /**
    * Perform headless action on a virtual machine vSphere object.
    */
   @RequestMapping(value = "/vm-headless-action", method = RequestMethod.POST)
   @ResponseBody
   public void vmHeadlessAction() {
      // Note: Implement your own logic for triggering an action on a virtual machine.
   }
}

