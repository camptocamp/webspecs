package c2c.webspecs

case class InP[-In,+Out](n:String,f:In => Out) extends Param[In,Out](n,f)
