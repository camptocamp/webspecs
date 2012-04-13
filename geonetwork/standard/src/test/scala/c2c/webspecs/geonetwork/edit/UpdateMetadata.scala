package c2c.webspecs
package geonetwork
package edit


object UpdateMetadata {
  def apply(data:(Any,Any)*):UpdateMetadata = new UpdateMetadata(true,true,data:_*)
}
/**
 * Update a metadata
 */
case class UpdateMetadata(finish:Boolean, showValidationErrors:Boolean, data:(Any,Any)*) 
	extends AbstractFormPostRequest[EditValue,EditValue](
	    "metadata.update!",
	    EditValueFactory,
	    Seq(InP("id", (_:EditValue).id),
	        SP("finish", if(finish) "yes" else "no"),
            SP('showvalidationerrors, showValidationErrors),
	    	InP("version", (_:EditValue).version)) ++ 
	    data.map(Param.stringMapping) :_* )
	    
case class UpdateMetadataHtml(finish:Boolean, showValidationErrors:Boolean, data:(Any,Any)*) 
extends AbstractFormPostRequest[EditValue,EditValue](
        "metadata.update",
        EditValueFactory,
        Seq(InP("id", (_:EditValue).id),
                SP("finish", if(finish) "yes" else "no"),
                SP('showvalidationerrors, showValidationErrors),
                InP("version", (_:EditValue).version)) ++ 
                data.map(Param.stringMapping) :_* ) 