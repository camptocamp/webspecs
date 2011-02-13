package org.fao.geonet

trait Log {

  private object LoggingConfig {
    val all = false
    val enabled = RequestXml :: RequestForm :: Connection :: Headers :: LifeCycle :: Nil
  }

  sealed trait Level
  case object RequestXml extends Level
  case object Connection extends Level
  case object Headers extends Level
  case object RequestForm extends Level
  case object RequestMPForm extends Level
  case object LifeCycle extends Level
  case object Constants extends Level

  protected def log(level:Level, msg: => Any) = {
    if(LoggingConfig.all || LoggingConfig.enabled.contains(level)) {
      write(msg)
    }
  }

  private def write(msg:Any) = println(msg)

}

object Log extends Log {
  def apply(level:Level, msg: => Any) = if(LoggingConfig.all || LoggingConfig.enabled.contains(level)) { write(msg) }
}
