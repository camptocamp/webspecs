package c2c.webspecs
package geonetwork
import c2c.webspecs.login.LoginRequest

class CreateAsNeededUserLifeCycle(config:GeonetConfig)  extends SystemLifeCycle {
  import config._
  def setup(implicit context: ExecutionContext, uriResolvers:UriResolver) = {
    def usesExists = (adminLogin then ListUsers).execute().value.exists(_.username == user)
    if(!usesExists) {
      adminLogin.assertPassed(None)
      val groupName = Properties("group") getOrElse {throw new IllegalArgumentException("A group configuration parameter is required")}
      val gid = if(! ListGroups.execute(None).value.exists(_.name == groupName)) {
        println("Creating group")
        val group = Group(name = groupName, description = "Test Groups")
        CreateGroup(group).assertPassed(None).value.id
      } else {
        groupId
      }
      println("Creating user")
      val testUser = User(idOption = Some(user), username = user, password = pass, profile = UserProfiles.Editor, groups = Seq(gid))
      CreateUser(testUser).assertPassed(None)
      println("Done creating user")
    }
    assert(usesExists, "failed to create user for some reason")
  }

  def tearDown(implicit context: ExecutionContext, uriResolvers:UriResolver) = {

    val DeleteMetadata = DeleteOwnedMetadata.setIn(UserRef(config.userId))

    (adminLogin then DeleteMetadata).execute(None)
  }
}
