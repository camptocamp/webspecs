package c2c.webspecs
package geonetwork

case object GetUserGroups
  extends AbstractGetRequest[UserRef,List[GroupValue]](
    "xml.usergroups.list",
    GroupValueListFactory,
    InP[UserRef,String]("id",ref => ref.userId)
  ) {
  def setIn(id:String):Request[Any,List[GroupValue]] = setIn(new UserRef{val userId=id})
}