package c2c.webspecs
import org.apache.http.client.HttpClient
import org.apache.http.protocol.HttpContext
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager
import org.apache.http.client.params.HttpClientParams
import org.apache.http.client.params.CookiePolicy

class DefaultExecutionContext(httpClientFactory: => HttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager()),
  val createHttpContext: () => HttpContext = () => new BasicHttpContext) extends ExecutionContext {

  lazy val httpClient: HttpClient = httpClientFactory
  httpClient.getConnectionManager().getSchemeRegistry().register(SSLUtilities.fakeSSLScheme(443))
  httpClient.getConnectionManager().getSchemeRegistry().register(SSLUtilities.fakeSSLScheme(8443))
  httpClient.getConnectionManager().asInstanceOf[ThreadSafeClientConnManager].setDefaultMaxPerRoute(10)
  httpClient.getConnectionManager().asInstanceOf[ThreadSafeClientConnManager].setMaxTotal(50)
//  HttpClientParams.setRedirecting(httpClient.getParams, true)
  HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);

  def createNew = {
    val context = new DefaultExecutionContext(httpClientFactory, createHttpContext)
    context
  }
}
