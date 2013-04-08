package c2c.webspecs.geonetwork

/**
 * All standard privileges
 */
object MetadataOperation {
  var all = Set[MetadataOperation]()
  val Publish = new MetadataOperation("Publish", 0).register()
  val Download = new MetadataOperation("Download", 1).register()
  val Editing = new MetadataOperation("Editing", 2).register()
  val Notify = new MetadataOperation("Notify", 3).register()
  val Interactive = new MetadataOperation("Interactive", 5).register()
  val Featured = new MetadataOperation("Featured", 6).register()
  
  def apply (name: String, id: Int) = {
    all.find( _.id == id ) getOrElse (new MetadataOperation(name, id))
  }
  
  def apply(id:Int) = {
    all.find( _.id == id ) getOrElse (new MetadataOperation(s"Operation with id=$id", id))
  }
}
/**
 * Represents a metadata privilege.  It is an open set.  Each new privilege that
 * is created should be registed.
 */
class MetadataOperation(val name:String, val id:Int) {
  def register() = {
    MetadataOperation.all += this
    this
  }
}