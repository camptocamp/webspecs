package c2c.webspecs


trait Log {

  // To show httpclient logging run application with:
  // -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog -Dorg.apache.commons.logging.simplelog.showdatetime=true -Dorg.apache.commons.logging.simplelog.log.org.apache.http=DEBUG -Dorg.apache.commons.logging.simplelog.log.org.apache.http.wire=ERROR
  object LoggingConfig {
    val all = false
    
    val enabled = {
      val enabledNames = {
        val baseNames = Option(System.getProperty("logging_enabled")).getOrElse("LifeCycle,Connection,Warning,Error,Constants").split(',').map(_.trim)
        val extrasEnabled = for { 
          extras <- Option(System.getProperty("extra_logs_enabled")).toList
          each <- extras.split(',')} yield each.trim
        val disabled = for {
          extras <- Option(System.getProperty("logs_disabled")).toList
          each <- extras.split(',')} yield each.trim

        (baseNames ++ extrasEnabled) filterNot (disabled.contains)
      }
      allLevels.filter(level => enabledNames.contains(level.toString))
    }

  }
  
  private[this] var _allLevels = 
    RequestXml :: Connection :: Headers :: RequestForm :: LifeCycle ::
    Constants :: Warning :: Error :: Plugins :: TextResponse :: Nil
  def allLevels = _allLevels
  
  trait Level
  case object RequestXml extends Level
  case object Connection extends Level
  case object Headers extends Level
  case object RequestForm extends Level
  case object RequestMPForm extends Level
  case object LifeCycle extends Level
  case object Constants extends Level
  case object Warning extends Level
  case object Error extends Level
  case object Plugins extends Level
  case object TextResponse extends Level

  protected def log(level:Level, msg: => Any) = {
    if(LoggingConfig.all || LoggingConfig.enabled.contains(level)) {
      System.err.println("["+level+"] "+msg)
    }
  }


}

object Log extends Log {
  def apply(level:Level, msg: => Any) = log(level,msg)
}
