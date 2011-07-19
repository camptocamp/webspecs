package c2c.webspecs
package geonetwork
package csw

import c2c.webspecs.AbstractGetRequest


object CswGetCapabilities extends
		AbstractGetRequest("csw", 
							XmlValueFactory, 
							SP("request" -> "GetCapabilities"), 
							SP("version" -> "1.0.0"), 
							SP("service" -> "CSW"))
					  