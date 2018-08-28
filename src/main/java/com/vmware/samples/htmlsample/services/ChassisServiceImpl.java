/* Copyright (c) 2018 VMware, Inc. All rights reserved. */

package com.vmware.samples.htmlsample.services;

import com.vmware.samples.htmlsample.FakeChassisStore;
import com.vmware.samples.htmlsample.model.Chassis;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.List;

/**
 * Implementation of ChassisService.
 */
public class ChassisServiceImpl implements ChassisService {
   // Log messages
   private static final String CHASSIS_OBJECT_IS_NULL_LOG_MSG =
         "Chassis object is null.";
   private static final String CHASSIS_OBJECT_EXISTS_LOG_MSG =
         "Chassis object with the name '%s' already exists.";
   private static final String CHASSIS_OBJECT_DOES_NOT_EXIST_LOG_MSG =
         "Chassis object with the ID '%s' does not exist.";
   private static final String CHASSIS_OBJECT_CREATED_LOG_MSG =
         "Chassis object with the ID '%s' was successfully created: '%s'.";

   private static final Log _logger = LogFactory.getLog(ChassisServiceImpl.class);

   private final FakeChassisStore _fakeChassisStore;

   /**
    * Constructor.
    *
    * @param fakeStore  The in-memory chassis objects store used for this sample.
    */
   public ChassisServiceImpl(FakeChassisStore fakeStore) {
      _fakeChassisStore = fakeStore;
   }

   /**
    * Retrieves a chassis data by a given chassis id.
    *
    * @param chassisId  the id of a chassis object.
    * @return  the chassis object.
    */
   public Chassis getChassisById(String chassisId) {
      return _fakeChassisStore.getObjectById(chassisId);
   }

   /**
    * Retrieves all existing chassis objects.
    * @return  a list of chassis objects.
    */
   public List<Chassis> getAllChassis() {
      return _fakeChassisStore.getObjects();
   }

   /**
    * Creates a new chassis object.
    *
    * @param chassis chassis data used to create a new chassis object.
    * @return  the id of the newly created chassis object
    *          or null if the creation failed.
    */
   public String create(Chassis chassis) {
      if (chassis == null) {
         _logger.info(CHASSIS_OBJECT_IS_NULL_LOG_MSG);
         return null;
      }

      Chassis newChassis = _fakeChassisStore.create(chassis);
      if (newChassis == null) {
         _logger.info(String.format(CHASSIS_OBJECT_EXISTS_LOG_MSG, chassis.name));
         return null;
      }

      _logger.info(
            String.format(CHASSIS_OBJECT_CREATED_LOG_MSG, newChassis.id, newChassis.toString()));
      return newChassis.id;
   }

   /**
    * Updates a chassis object.
    *
    * @param chassis    chassis data used to update a chassis object with.
    * @return  true if the chassis has been successfully updated
    *          or false otherwise.
    */
   public boolean update(Chassis chassis) {
      Chassis existingChassis = _fakeChassisStore.getObjectById(chassis.id);
      if (existingChassis == null) {
         _logger.info(String.format(CHASSIS_OBJECT_DOES_NOT_EXIST_LOG_MSG, chassis.id));
         return false;
      }

      return _fakeChassisStore.update(chassis);
   }

   /**
    * Deletes a chassis object by a given chassis id.
    *
    * @param chassisId  the id of a chassis object.
    * @return  true if the chassis has been successfully deleted
    *          or false otherwise.
    */
   public boolean delete(String chassisId) {
      Chassis chassis = _fakeChassisStore.delete(chassisId);
      if (chassis == null) {
         _logger.info(String.format(CHASSIS_OBJECT_DOES_NOT_EXIST_LOG_MSG, chassisId));
         return false;
      }
      return true;
   }
}
