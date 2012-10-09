package c2c.webspecs
package geonetwork.edit

import org.apache.http.entity.mime.content.AbstractContentBody
import c2c.webspecs.AbstractMultiPartFormRequest
import org.apache.http.entity.mime.content.StringBody

case class SetLargeThumbnail(
    editValue: EditValue, 
    file:AbstractContentBody, 
    largeScaling:Option[ThumbnailScaling] = None,
    smallScaling:Option[ThumbnailScaling] = None
    )
	extends AbstractMultiPartFormRequest[Any,EditValue](
	    "metadata.thumbnail.set",
	    EditValueFactory.setId(editValue.id),
	    P("id" -> new StringBody(editValue.id)),
	    P("version" -> new StringBody(editValue.version)),
	    P("type" -> new StringBody("large")),
	    P("fname" -> file),
	    P("scaling" -> new StringBody(largeScaling.map(_ => "true") getOrElse "false")),
	    P("scalingFactor" -> new StringBody(largeScaling.map(f => ""+f.factor) getOrElse "180")),
	    P("scalingDir" -> new StringBody(largeScaling.map(_.direction) getOrElse "width")),
	    P("createSmall" -> new StringBody(smallScaling.map(_ => "true") getOrElse "false")),
	    P("smallScalingFactor" -> new StringBody(smallScaling.map(f => ""+f.factor) getOrElse "180")),
	    P("smallScalingDir" -> new StringBody(smallScaling.map(_.direction) getOrElse "width"))
	  )
