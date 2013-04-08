package c2c.webspecs
package geonetwork
package geocat.shared

case class DeleteSharedUser(id:String, forceDelete:Boolean) 
    extends AbstractGetRequest(
        "shared.user.remove",
        new DeletedSharedObjectIdFactory(), 
        P("id",id), 
        SP("forceDelete", forceDelete),
        SP("testing" -> true)
    )