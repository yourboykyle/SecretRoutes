package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.exception.ExceptionContext;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class SSLUtils {
    // For some reason, downloading the routes.json file from the GitHub causes an SSL error. This is my workaround, though it's not great to disable SSL certificate checking.

    public static void disableSSLCertificateChecking() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public static void enableSSLCertificateChecking() {
        try {
            // Reset to the default SSL context
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Reset to the default hostname verifier
            HttpsURLConnection.setDefaultHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());

        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public static void setSSlCertificate(){
        try {
            KeyStore keyStore = null;
            try {
                keyStore = KeyStore.getInstance("JKS");
            } catch (KeyStoreException e) {
                LogUtils.error(e);
            }
            if(keyStore != null){
                keyStore.load(SSLUtils.class.getResourceAsStream("/mykeystore.jks"), "changeit".toCharArray());
            }else{
                ChatUtils.sendChatMessage("[§3SRM§f] §cSomething went wrong wth ssl. Send the log file in the §1#support§f channel in the discord with a screenshot of this message.");
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, null);
            tmf.init(keyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

        }catch(Exception e){
            ChatUtils.sendChatMessage("[§3SRM§f] §cSomething went wrong wth ssl. Send the log file in the §1#support§f channel in the discord with a screenshot of this message.");
            LogUtils.error(e);
        }

    }






}
