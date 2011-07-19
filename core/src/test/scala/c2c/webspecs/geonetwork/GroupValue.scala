package c2c.webspecs
package geonetwork

class GroupValue(val id:String, name:String, description:String="",email:String="")
  extends Group(name,description,email)
  with Id {
  override def toString: String = "Group(%s,%s,%s,%s)".format(id,name,description,email)
}
