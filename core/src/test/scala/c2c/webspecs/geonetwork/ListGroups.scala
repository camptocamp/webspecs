package c2c.webspecs
package geonetwork

case object ListGroups
  extends AbstractGetRequest(
    "group.list!",
    GroupValueListFactory
  )
