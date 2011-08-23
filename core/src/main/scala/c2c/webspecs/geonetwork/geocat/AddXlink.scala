package c2c.webspecs
package geonetwork
package geocat

import edit._

object AddXlink {
    def request(addRequest:AddXLinkRequest) =
      (StartEditing() then addRequest startTrackingThen EndEditing)
      
    def requestWithMd(addRequest:AddXLinkRequest) =
      (StartEditing() then addRequest startTrackingThen EndEditing then GetRawMetadataXml)
}