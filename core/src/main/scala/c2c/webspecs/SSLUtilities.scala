package c2c.webspecs

import org.apache.http.conn.scheme.Scheme
import java.security.cert.X509Certificate
import java.lang.String
import java.security.SecureRandom
import org.apache.http.conn.ssl.{X509HostnameVerifier, SSLSocketFactory}
import javax.net.ssl._

object SSLUtilities {

  val sslcontext = SSLContext.getInstance(SSLSocketFactory.TLS);
  sslcontext.init(Array[KeyManager](), Array[TrustManager](TrustingTrustManager), new SecureRandom());
  def socketFactory = new SSLSocketFactory(sslcontext,TrustingHostNameVerifier){
    override def toString = "Trusting Socket Factory"
  };
  def fakeSSLScheme(port:Int) = new Scheme("https", port, socketFactory);

  object TrustingTrustManager extends X509TrustManager {
    def getAcceptedIssuers = null
    def checkServerTrusted(p1: Array[X509Certificate], p2: String) = {}
    def checkClientTrusted(p1: Array[X509Certificate], p2: String) = {}
    override def toString = "Trusting TrustManager"
  }

  object TrustingHostNameVerifier extends X509HostnameVerifier {
    def verify(host: String, cns: Array[String], subjectAlts: Array[String]) = {}

    def verify(host: String, cert: X509Certificate) = {}

    def verify(host: String, ssl: SSLSocket) = {}

    def verify(p1: String, p2: SSLSession) = true
  }
}
