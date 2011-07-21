package c2c.webspecs
package geonetwork
package edit

import c2c.webspecs.Request

/**
 * Update a metadata
 */
case class UpdateMetadata(data:(String,String)*) 
	extends AbstractFormPostRequest[EditValue,EditValue](
	    "metadata.update!",
	    EditValueFactory,
	    Seq(InP("id", (_:EditValue).id),
	    	InP("version", (_:EditValue).version)) ++ 
	    data.map(SP(_)) :_* ) 