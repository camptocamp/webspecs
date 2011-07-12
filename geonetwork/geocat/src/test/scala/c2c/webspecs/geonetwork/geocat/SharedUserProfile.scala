package c2c.webspecs.geonetwork.geocat

import c2c.webspecs.geonetwork.UserProfiles

/**
 * The extra user in Geocat to flag a shared user
 */
case object SharedUserProfile extends UserProfiles.UserProfile {
  override def toString: String = "Shared"
  UserProfiles.all = this :: UserProfiles.all
}