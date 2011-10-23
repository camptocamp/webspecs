package c2c.webspecs

import util.control.Exception.allCatch

trait TextValue {
  protected def basicValue:BasicHttpValue
  lazy val text = basicValue.data match {
    case Right(data) =>
      allCatch[String].either { val t=new String(data, "UTF8"); Log(Log.TextResponse, t);t}
    case Left(error) => Left(error)
  }
  def withText[R](f:String => R):R = text.fold(throw _, f)
  lazy val getText = withText(s => s)
}
