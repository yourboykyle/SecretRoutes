package xyz.yourboykyle.secretroutes.utils;

import moe.nea.libautoupdate.UpdateUtils;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.KeyStoreException;


public class SSLUtils {
    static SSLContext context = null;

    public static void makeSSLCertificate(){
        try{
            if(context == null){
                KeyStore keyStore = null;
                try {
                    keyStore = KeyStore.getInstance("JKS");
                } catch (KeyStoreException e) {
                    LogUtils.error(e);
                }
                if (keyStore != null) {
                    keyStore.load(SSLUtils.class.getResourceAsStream("/srmkeystore.jks"), "changeit".toCharArray());
                } else {
                    ChatUtils.sendChatMessage("[§3SRM§f] §cSomething went wrong wth ssl. Send the log file in the §1#support§c channel in the discord with a screenshot of this message.");
                }

                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                kmf.init(keyStore, null);
                tmf.init(keyStore);
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                context = ctx;
                //HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

            }
        }catch(Exception e){
            LogUtils.error(e);
        }
    }

    public static void setSSlCertificate(){
        try {
            makeSSLCertificate();

            UpdateUtils.patchConnection(connection -> {
                if (connection instanceof HttpsURLConnection)
                    ((HttpsURLConnection) connection).setSSLSocketFactory(context.getSocketFactory());
            });
        } catch (Exception e) {
            ChatUtils.sendChatMessage("[§3SRM§f] §cSomething went wrong wth ssl. Send the log file in the §1#support§f channel in the discord with a screenshot of this message.");
            LogUtils.error(e);
        }
    }
    public static SSLSocketFactory getSSLSocketFactory() {
        if(context == null){
            makeSSLCertificate();
        }
        return context.getSocketFactory();
    }






}
