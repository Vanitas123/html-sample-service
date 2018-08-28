/* Copyright (c) 2018 VMware, Inc. All rights reserved. */
package com.vmware.samples.htmlsample.vim25.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.vmware.vise.usersession.ServerInfo;

/**
 * Abstract Service which initializes SSLContext
 */
public class TrustedService {
   /**
    * Using the ThumbprintTrustManager and the ThumbprintHostNameVerifier
    * creates SSLContext
    * @return The SocketFactory of the created SSLContext
    */
   public static SSLSocketFactory getSSLSocketFactory()
         throws NoSuchAlgorithmException, KeyManagementException {
      TrustManager[] trustManagers = new TrustManager[1];
      TrustManager tm = new ThumbprintTrustManager();
      trustManagers[0] = tm;

      SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

      SSLSessionContext sslsc = sslContext.getServerSessionContext();
      sslsc.setSessionTimeout(0);
      sslContext.init(null, trustManagers, null);

      return sslContext.getSocketFactory();
   }

   /**
    * Gets the thumbprint from the ServerInfo and sets it to the ThumbprintTrustManager
    * @param sInfo containing thumbprint
    */
   public static void setThumbprint(ServerInfo sInfo){
      String thumbprint = sInfo.thumbprint;
      if (thumbprint != null) {
         thumbprint = thumbprint.replaceAll(":", "").toLowerCase();
         ThumbprintTrustManager.addThumbprint(thumbprint);
      }
   }
}
