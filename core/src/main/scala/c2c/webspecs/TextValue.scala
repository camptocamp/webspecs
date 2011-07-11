package c2c.webspecs

import util.control.Exception.allCatch

trait TextValue {
  protected def basicValue:BasicHttpValue
  lazy val text = basicValue.data match {
    case Right(data) =>
      allCatch[String].either { new String(data, "UTF8") }
    case Left(error) => Left(error)
  }
  def withText[R](f:String => R):R = text.fold(throw _, f)
}
