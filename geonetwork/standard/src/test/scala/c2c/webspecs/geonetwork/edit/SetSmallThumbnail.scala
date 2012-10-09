package c2c.webspecs
package geonetwork.edit

import org.apache.http.entity.mime.content.AbstractContentBody
import c2c.webspecs.AbstractMultiPartFormRequest
import org.apache.http.entity.mime.content.StringBody

case class SetSmallThumbnail(
    editValue: EditValue, 
    file:AbstractContentBody, 
    scaling:Option[ThumbnailScaling] = None
    )
	extends AbstractMultiPartFormRequest[Any,EditValue](
	    "metadata.thumbnail.set",
	    EditValueFactory.setId(editValue.id),
	    P("id" -> new StringBody(editValue.id)),
	    P("version" -> new StringBody(editValue.version)),
	    P("type" -> new StringBody("small")),
	    P("fname" -> file),
	    P("scaling" -> new StringBody(scaling.map(_ => "true") getOrElse "false")),
	    P("scalingFactor" -> new StringBody(scaling.map(f => ""+f.factor) getOrElse "180")),
	    P("scalingDir" -> new StringBody(scaling.map(_.direction) getOrElse "width")))
