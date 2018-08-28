/* Copyright (c) 2018 VMware, Inc. All rights reserved. */

package com.vmware.samples.htmlsample;

import com.vmware.samples.htmlsample.model.Chassis;

import java.util.*;

/**
 * Simplified fake data store for the Chassis objects, and related utilities.
 *
 * ***************************************************************************** *
 * IMPORTANT: this implementation uses in-memory data to keep the setup easy,    *
 * a real implementation should retrieve data from a remote server or database   *
 * and have very minimal or no storing/caching in the java layer because the     *
 * server must remain stateless and be able to scale out.                        *
 * ***************************************************************************** *
 *
 * Note that this class is thread-safe but doesn't deal with complex operations
 * or large data sets. It is not intended to be used as-is!
 */
public class FakeChassisStore {
   // Default number of pre-defined chassis objects is 4.
   private static final int CHASSIS_INITIAL_COUNT = 4;

   private static final String CHASSIS_ID = "chassis:%s";
   private static final String CHASSIS_NAME = "Chassis-Test-Vanita-%s";
   private static final String CHASSIS_SERVER_TYPE = "Server_Type %s";
   private static final String CHASSIS_DIMENSIONS = "20in x 30in x 17in";

   // Internal index used to create unique ids.
   private static int _index = 0;

   // Map of chassis objects used in the sample.
   // The key is a chassis object's id and the value is the chassis object.
   private Map<String, Chassis> _fakeStore;

   /**
    * Initializes the fake in-memory store with 4 chassis objects.
    * This bean init method is defined in bundle-context.xml.
    */
   public void init() {
      Map<String, Chassis> store =
              new HashMap<String, Chassis>(CHASSIS_INITIAL_COUNT);
      _fakeStore = Collections.synchronizedMap(store);

      // Create an initial set of chassis objects.
      for (int i = 0; i < CHASSIS_INITIAL_COUNT; i++) {
         Chassis newChassis = new Chassis();
         newChassis.name = String.format(CHASSIS_NAME, i);
         newChassis.serverType = String.format(CHASSIS_SERVER_TYPE, i);
         newChassis.dimensions = CHASSIS_DIMENSIONS;
         newChassis.isActive = false;
         create(newChassis);
      }
   }

   /**
    * Bean destroy method defined in bundle-context.xml.
    */
   public void destroy() {
      _fakeStore.clear();
   }

   /**
    * Retrieves all chassis objects stored in the faked store.
    * NOTE: A real implementation should retrieve objects
    * data from a plugin back-end server.
    *
    * @return  a list of chassis objects.
    */
   public List<Chassis> getObjects() {
      return new ArrayList<Chassis>(_fakeStore.values());
   }

   /**
    * Retrieves a chassis object by a given chassis id.
    *
    * @param id   the id of the chassis object.
    * @return  a chassis object for the given id
    *          or null if such object does not exist.
    */
   public Chassis getObjectById(String id) {
      if (id == null) {
         return null;
      }
      return _fakeStore.get(id);
   }

   /**
    * Adds a new chassis object to the fake store.
    *
    * @param chassis chassis object.
    * @return  the newly added chassis object
    *          or null if a chassis object with that name exists.
    */
   public Chassis create(Chassis chassis) {
      if (chassis == null || !isNameUnique(chassis.name)) {
         return null;
      }

      // Add the chassis object to the fake store.
      chassis.id = generateId();
      _fakeStore.put(chassis.id, chassis);
      return chassis;
   }

   /**
    * Updates an existing chassis object.
    *
    * @param chassis the chassis object.
    * @return  true if the chassis object was successfully updated
    *          or false otherwise.
    */
   public boolean update(Chassis chassis) {
      if(chassis == null) {
         return false;
      }
      boolean updatingTheSameChassis =
            chassis.name.equals(_fakeStore.get(chassis.id).name);
      if (!updatingTheSameChassis && !isNameUnique(chassis.name)) {
         return false;
      }

      Chassis updatedChassis = _fakeStore.put(chassis.id, chassis);
      return (updatedChassis != null);
   }

   /**
    * Deletes a chassis object from the fake store.
    *
    * @param id   the id of the chassis object which will be removed.
    * @return  the chassis object that was removed
    *          or null if a chassis object with the given id does not exist.
    */
   public Chassis delete(String id) {
      if (id == null) {
         return null;
      }
      return _fakeStore.remove(id);
   }

   /**
    * Generates a chassis object id.
    *
    * @return a chassis object id.
    */
   private static String generateId() {
      return String.format(CHASSIS_ID, _index++);
   }

   /**
    * Validates if the given chassis name is unique.
    *
    * @param name the name of a chassis object.
    * @return  true if the given chassis name is unique, or false otherwise.
    */
   private boolean isNameUnique(String name) {
      synchronized(_fakeStore) {
         for (Chassis chassis : _fakeStore.values()) {
            if (name.equals(chassis.name)) {
               return false;
            }
         }
         return true;
      }
   }
}
