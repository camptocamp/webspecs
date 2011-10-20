package c2c.webspecs
package geonetwork
package csw

import c2c.webspecs.AbstractGetRequest


case class CswGetCapabilities(url:String="csw") extends
		AbstractGetRequest(url, 
							XmlValueFactory, 
							SP("request" -> "GetCapabilities"), 
							SP("version" -> "1.0.0"), 
							SP("service" -> "CSW"))
					  