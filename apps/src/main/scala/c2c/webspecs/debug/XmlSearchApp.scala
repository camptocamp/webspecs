package c2c.webspecs
package debug

import geonetwork._;

object XmlSearchApp extends WebspecsApp {
  val gcResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
    override def baseServer = "www.geocat.ch"
  }

  val jenkinsResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
    override def baseServer = "ec2-46-51-142-140.eu-west-1.compute.amazonaws.com"
  }
  
  val request = XmlSearch().to(10).search('_isHarvested -> "n" )
  
  println(request.execute()(executionContext, gcResolver).value.summary)
  println(request.execute()(executionContext, jenkinsResolver).value.summary)

}