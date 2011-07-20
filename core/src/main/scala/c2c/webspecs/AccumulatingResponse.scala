package c2c.webspecs

trait AccumulatedResponse[+Z] extends Response[Z] {
  self : Product =>
  def last:Response[Z]

  def basicValue = last.basicValue
  def value = last.value

  override def toString = productIterator.mkString(getClass.getSimpleName+"(",",",")")
}
