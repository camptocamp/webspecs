package c2c.webspecs

abstract class Param[-In,+Out](val name:String,val value:In => Out)
object Param {
  def stringMapping[B] = (p:(String,B)) => P(p._1,p._2.toString)
}
