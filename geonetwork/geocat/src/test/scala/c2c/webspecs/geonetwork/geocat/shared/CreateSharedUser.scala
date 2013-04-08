package c2c.webspecs
package geonetwork
package geocat.shared

import c2c.webspecs.AbstractFormPostRequest
import c2c.webspecs.BasicHttpValue
import c2c.webspecs.ExecutionContext
import c2c.webspecs.Request
import c2c.webspecs.UriResolver
import c2c.webspecs.ValueFactory
import c2c.webspecs.geonetwork.ListUsers
import c2c.webspecs.geonetwork.User
import c2c.webspecs.geonetwork.UserValue

abstract class CreateSharedUser(user:User,validated:Boolean)
  extends AbstractFormPostRequest[Any,UserValue](
    (if(validated) "validated" else "nonvalidated")+".shared.user.update!",
    SelfValueFactory(),
    (P("operation", "newuser") :: SP("validated",if(validated) "y" else "n") :: user.formParams):_*)
  with ValueFactory[Any,UserValue] {

  override def createValue[A <: Any, B >: UserValue](request: Request[A, B], in: Any, rawValue: BasicHttpValue,executionContext:ExecutionContext, uriResolver:UriResolver) = {
    new UserValue(user,rawValue) {
      override lazy val userId:String = {
        (ListUsers.valueFactory.createValue(ListUsers,in,rawValue,executionContext, uriResolver) find {_.username == user.username} map {_.userId}).get
      }
    }
  }
}

case class CreateValidatedUser(user:User) extends CreateSharedUser(user,true)
case class CreateNonValidatedUser(user:User) extends CreateSharedUser(user,false)