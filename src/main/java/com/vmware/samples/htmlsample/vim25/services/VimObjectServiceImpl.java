/* Copyright (c) 2018 VMware, Inc. All rights reserved. */
package com.vmware.samples.htmlsample.vim25.services;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import com.vmware.samples.htmlsample.model.Host;
import com.vmware.samples.htmlsample.vim25.ssl.TrustedService;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.HostSystemConnectionState;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RetrieveOptions;
import com.vmware.vim25.RetrieveResult;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;
import com.vmware.vise.usersession.ServerInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Vim25 service used to retrieve data from a vcenter
 * uses the TrustedService in order to create trusted connection
 */
public class VimObjectServiceImpl implements VimObjectService {
   private static final Log _logger = LogFactory.getLog(VimObjectServiceImpl.class);
   private static final String SERVICE_INSTANCE = "ServiceInstance";
   private static final String SSL_SOCKET_FACTORY =
         "com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory";
   private static final String NAME = "name";
   private static final String HOST = "HostSystem";
   private static final String OBJECT_ID_FORMAT = "urn:vmomi:%s:%s:%s";
   private static final String HOST_SUMMARY = "summary.host";
   private static final String HOST_CONNECTION_STATE = "runtime.connectionState";
   private static final String NUM_CPU_CORES = "hardware.cpuInfo.numCpuCores";
   private static final String MEMORY_SIZE = "systemResources.config.memoryAllocation.limit";
   private static final String[] HOST_PROPERTIES = {NAME, HOST_SUMMARY, NUM_CPU_CORES,
         HOST_CONNECTION_STATE, MEMORY_SIZE};

   private static VimPortType _vimPort = initializeVimPort();

   private static VimPortType initializeVimPort() {
      VimService vimService = new VimService();
      VimPortType vimPort = vimService.getVimPort();

      Map<String, Object> reqContext =
            ((BindingProvider) vimPort).getRequestContext();
      try {
         SSLSocketFactory sslSocketFactory = TrustedService.getSSLSocketFactory();
         reqContext.put(SSL_SOCKET_FACTORY, sslSocketFactory);
      } catch (NoSuchAlgorithmException | KeyManagementException e) {
         _logger.error("Could not setup SSLSocketFactory in the context.", e);
      }

      return vimPort;
   }

   /**
      Used by the VimObjectServiceImpl bean, in order to destroy the VimPortType on
      bundle undeploy
    */
   private void destroy() {
      _vimPort = null;
   }

   /**
    * Retrives information about the vSphere Host Objects from a vcenter
    * specified in the ServerInfo parameter
    * @param serverInfoObject specifies information about the vcenter
    * @return a list of Host objects
    */
   @Override
   public List<Host> retrieveHosts(ServerInfo serverInfoObject) {
      paramsNotNull(serverInfoObject);
      List<Map<String, Object>> retrievedHosts =
            retrieveObjectProperties(serverInfoObject, HOST, HOST_PROPERTIES);

      List<Host> hosts =
            transformHostsPropertiesToObjects(retrievedHosts, serverInfoObject);
      return hosts;
   }

   /**
    * For a given ServerInfo(which specifies the vcenter), vSphere Object, and
    * properties, retrieves the values of properties for the given vSphere Object
    * in the vcenter defined in the ServerInfo
    *
    * Sets up PropertyCollector and ViewManager, Creates the PropertyFilterSpec,
    * retrieves data using the _vimPort field and formats them for easier usage.
    * @param serverInfo specifies the vcenter information from where the properties will be retrieved
    * @param vSphereObject for which vSphere Object to retrieve the properties
    * @param vSphereObjectProperties
    * @return List of all vSphere Objects of the specified type.
    */
   @Override
   public List<Map<String, Object>> retrieveObjectProperties(ServerInfo serverInfo,
         String vSphereObject, String vSphereObjectProperties[]) {
      paramsNotNull(serverInfo, vSphereObject, vSphereObjectProperties);
      ServiceContent serviceContent = null;
      try {
         serviceContent = getServiceContentWithSessionCookie(serverInfo);
      } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
         _logger.warn("Could not retrieve the ServiceContent using sessionCookie",
               runtimeFaultFaultMsg);
      }
      if(serviceContent == null) {
         return new ArrayList<>();
      }
      // Get references to the ViewManager and the PropertyCollector
      ManagedObjectReference viewMgrRef = serviceContent.getViewManager();
      ManagedObjectReference propColl = serviceContent.getPropertyCollector();

      // Create a container view for the vSphere Object.
      List<String> vObjects = new ArrayList<>();
      vObjects.add(vSphereObject);

      RetrieveResult props = null;
      try {
         ManagedObjectReference cViewRef = _vimPort.createContainerView(viewMgrRef,
               serviceContent.getRootFolder(), vObjects, true );

         props = retrieveProperties(cViewRef, propColl, vSphereObject, vSphereObjectProperties);
      } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
         _logger.error("Could not create ContainerView for " + vSphereObject , runtimeFaultFaultMsg);
      } catch (InvalidPropertyFaultMsg invalidPropertyFaultMsg) {
         _logger.error("Could not retrieveProperties for " + vSphereObject, invalidPropertyFaultMsg);
      }

      return formatRetrievedProperties(props);
   }

   /**
    * Formats the list of host properties to a real Host objects
    */
   private List<Host> transformHostsPropertiesToObjects(
         List<Map<String, Object>> retrievedHosts, ServerInfo serverInfoObject) {
      List<Host> hosts = new ArrayList<>();
      for(Map<String, Object> retrievedHost : retrievedHosts) {
         String objectId = getFormattedObjectId(retrievedHost, serverInfoObject.serviceGuid);
         String connectionState = getHostConnectionState(retrievedHost);

         Object numCpuObject = retrievedHost.get(NUM_CPU_CORES);
         Object memSizeObject = retrievedHost.get(MEMORY_SIZE);
         String numCpus = (numCpuObject == null) ? "" : numCpuObject.toString();
         String memSize = (memSizeObject == null) ? "" : memSizeObject.toString();

         Host host = new Host(objectId, (String)retrievedHost.get(NAME), connectionState,
               serverInfoObject.name, memSize, numCpus);

         hosts.add(host);
      }
      return hosts;
   }

   /**
    * Uses the host id and the serviceGuid to create the objectId for the current host
    */
   private String getFormattedObjectId(Map<String, Object> host, String serviceGuid) {
      String hostId = ((ManagedObjectReference)host.get(HOST_SUMMARY)).getValue();
      String objectId = String.format(OBJECT_ID_FORMAT, HOST, hostId, serviceGuid);
      return objectId;
   }

   /**
    * Retrieves the connection state for the given host
    */
   private String getHostConnectionState(Map<String, Object> host) {
      HostSystemConnectionState conState =
            (HostSystemConnectionState)host.get(HOST_CONNECTION_STATE);
      return (conState == null) ? "" : conState.value();
   }

   /**
    * Sets up the Service Instance ManagedObjectReference and
    * sets the thumbprint taken from the ServerInfo and
    * retrieves the ServiceContent using the _vimPort
    * @return The newly retrieved ServiceContent
    * @throws RuntimeFaultFaultMsg
    */
   private ServiceContent getServiceContentWithSessionCookie(ServerInfo sInfo)
         throws RuntimeFaultFaultMsg {
      TrustedService.setThumbprint(sInfo);
      ManagedObjectReference serviceInstanceRef =
            createSvcInstanceRef(sInfo.serviceUrl, sInfo.sessionCookie);

      ServiceContent serviceContent =
            _vimPort.retrieveServiceContent(serviceInstanceRef);

      return serviceContent;
   }

   /**
    * Adds the sessionCookie and the serviceUrl to the BindingProvider of the already
    * created _vimPort and creates a service instance ManagedObjectReference
    * @return The Service Instance ManagedObjectReference
    */
   private ManagedObjectReference createSvcInstanceRef(final String serviceUrl,
         final String sessionCookie) {
      List<String> values = new ArrayList<>();
      values.add("vmware_soap_session=" + sessionCookie);
      Map<String, List<String>> reqHeadrs =
            new HashMap<>();
      reqHeadrs.put("Cookie", values);

      Map<String, Object> reqContext =
            ((BindingProvider) _vimPort).getRequestContext();
      reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);
      reqContext.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
      reqContext.put(MessageContext.HTTP_REQUEST_HEADERS, reqHeadrs);

      final ManagedObjectReference svcInstanceRef = new ManagedObjectReference();
      svcInstanceRef.setType(SERVICE_INSTANCE);
      svcInstanceRef.setValue(SERVICE_INSTANCE);

      return svcInstanceRef;
   }

   /**
    * Retrieves properties from the given ViewManager and PropertyCollector
    *
    * @return Retrieved properties if it succeds, null otherwise
    */
   private RetrieveResult retrieveProperties(ManagedObjectReference cViewRef,
         ManagedObjectReference propColl, String vSphereObject,
         String vSphereObjectProperties[]) throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
      PropertyFilterSpec fSpec = createPropertyFilterSpec(cViewRef, vSphereObject,
            vSphereObjectProperties);

      List<PropertyFilterSpec> fSpecList = new ArrayList<>();
      fSpecList.add(fSpec);

      RetrieveOptions ro = new RetrieveOptions();
      RetrieveResult props = _vimPort.retrievePropertiesEx(propColl, fSpecList, ro);

      return props;
   }

   /**
    * Creates PropertyFilterSpec which retrievs properties from the vSphereObject
    * The properties are defined in the vSphereObjectProperties array of properties
    *
    * @return the newly created PropertyFilterSpec
    */
   private PropertyFilterSpec createPropertyFilterSpec(ManagedObjectReference cViewRef,
         String vSphereObject, String vSphereObjectProperties[]) {
      // Creates an object specification to define the starting point for inventory navigation
      ObjectSpec oSpec = new ObjectSpec();
      oSpec.setObj(cViewRef);
      oSpec.setSkip(true);

      // Creates a traversal specification to identify the path for collection
      TraversalSpec tSpec = new TraversalSpec();
      tSpec.setName("traverseEntities");
      tSpec.setPath("view");
      tSpec.setSkip(false);
      tSpec.setType("ContainerView");

      // Adds the TraversalSpec to the ObjectSpec.selectSet array.
      oSpec.getSelectSet().add(tSpec);

      // Identify the properties to be retrieved.
      PropertySpec pSpec = new PropertySpec();
      pSpec.setType(vSphereObject);
      pSpec.getPathSet().addAll(Arrays.asList(vSphereObjectProperties));

      // Adds the object and property specifications to the property filter specification.
      PropertyFilterSpec fSpec = new PropertyFilterSpec();
      fSpec.getObjectSet().add(oSpec);
      fSpec.getPropSet().add(pSpec);

      return fSpec;
   }

   /**
    * Given RetrieveResult, converts the properties in a list of maps, where the maps
    * contain string keys(i.e. the property name) and Object values(i.e. the retrieved
    * properties)
    * @param props containing the properties of the retrieved vSphere Object
    * @return The retrieved object in a more suitable format
    */
   private List<Map<String, Object>> formatRetrievedProperties(RetrieveResult props) {
      List<Map<String, Object>> objectsProperties = new ArrayList<>();

      if (props != null) {
         for (ObjectContent oc : props.getObjects()) {
            List<DynamicProperty> dps = oc.getPropSet();

            Map<String, Object> managedObject = new HashMap<>(dps.size());

            if (dps != null) {
               for (DynamicProperty dp : dps) {
                  managedObject.put(dp.getName(), dp.getVal());
               }
            }
            objectsProperties.add(managedObject);
         }
      }
      return objectsProperties;
   }

   /**
    * Tests the args array for null elements and throws an IllegalArgumentException if
    * any null elements are encountered.
    *
    * @param args
    *    The values of the parameters to be checked for null.
    *
    * @throws IllegalArgumentException if args is null, if any elements are null.
    */
   public static void paramsNotNull(Object... args) {
      if (args == null) {
         throw new IllegalArgumentException("Null argument args");
      }

      for (int i = 0; i < args.length; i++) {
         if (args[i] == null) {
            throw new IllegalArgumentException("Null param at index: " + i);
         }
      }
   }
}
