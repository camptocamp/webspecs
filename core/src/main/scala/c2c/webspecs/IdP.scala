package c2c.webspecs

case class IdP[In](n:String) extends Param[In,String](n,in => in.toString)
