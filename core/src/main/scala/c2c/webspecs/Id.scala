package c2c.webspecs

object Id {
  def apply(idString:String) = new Id {
    val id = idString
  }
}
trait Id {
  def id:String
  override def toString = id
}
