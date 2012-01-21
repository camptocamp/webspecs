package c2c.webspecs
package geonetwork
package geocat

import edit._

object AddXlink {
  /**
   * Add a particular xlink
   */
    def execute(addRequest:AddXLinkRequest,id:Id)(implicit c:ExecutionContext, r:UriResolver) = {
      val result = (StartEditing() then addRequest).execute(id)
      EndEditing.execute(result.value)
      result
    }

  /**
   * Add an xlink and get the resulting metadata
    */
    def addAndGetMetadata(addRequest:AddXLinkRequest, id:Id)(implicit c:ExecutionContext, r:UriResolver) = {
      val addResult = (StartEditing() then addRequest).execute(id)
      val rawXmlResult = (EndEditing then GetRawMetadataXml).execute(addResult.value)
      (addResult, rawXmlResult)
    }
}