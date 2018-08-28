/* Copyright (c) 2018 VMware, Inc. All rights reserved. */

package com.vmware.samples.htmlsample.services;

import com.vmware.samples.htmlsample.model.Chassis;
import java.util.List;

/**
 * Interface used to perform operations on a chassis object and retrieve data for chassis objects.
 */
public interface ChassisService {
   /**
    * Retrieves a chassis object by a given chassis id.
    *
    * @param chassisId  the id of a chassis object.
    * @return  the chassis object.
    */
   Chassis getChassisById(String chassisId);

   /**
    * Retrieves all existing chassis objects.
    * @return  a list of chassis objects.
    */
   List<Chassis> getAllChassis();

   /**
    * Creates a new chassis object.
    *
    * @param chassis chassis data used to create a new chassis object.
    * @return  the id of the newly created chassis object
    *          or null if the creation failed.
    */
   String create(Chassis chassis);

   /**
    * Updates a chassis object.
    *
    * @param chassis    chassis data used to update a chassis object with.
    * @return  true if the chassis has been successfully updated
    *          or false otherwise.
    */
   boolean update(Chassis chassis);

   /**
    * Deletes a chassis object by a given chassis id.
    *
    * @param chassisId   the id of a chassis object.
    * @return  true if the chassis has been successfully deleted
    *          or false otherwise.
    */
   boolean delete(String chassisId);
}
