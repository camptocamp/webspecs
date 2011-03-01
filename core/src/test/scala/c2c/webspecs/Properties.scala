package c2c.webspecs

import System.{getProperty => sysprop, getenv => envprop}
import java.util.{Properties => JProperties}
import scalax.file.Path
import collection.JavaConverters._

object Properties {

  def TEST_TAG = "{automated_test_metadata}"
  def TEST_URL_KEY = "test.server"
  lazy val all:Map[String,String] = {
    def load(file:String):Map[String,String] = {
      val inputStream =
        if (Path(file).exists) Some(Path(file).inputStream.open)
        else Option(getClass.getClassLoader.getResourceAsStream(file))
      inputStream map {
        in =>
          val p = new JProperties()
          p.load(in)
          var propMap = p.asScala.toMap.asInstanceOf[Map[String,String]]
          propMap.get("@includes") map { includes =>
            propMap -= "@includes"
            propMap ++= includes split "," flatMap {load(_)}
          }
          propMap.get("@override") map { overrides =>
            propMap -= "@override"
            propMap = (overrides split "," flatMap {load(_)}).toMap ++ propMap
          }
        propMap
      } getOrElse Map[String,String]()
    }

    val props = load("config.properties") map {
      case (key,value) =>
        val newVal = Option(sysprop(key)) orElse Option(envprop(key)) getOrElse value
        key -> newVal
    }
    resolveReferences(props)
  }
  private def resolveReferences(map:Map[String,String]) = {
    val Ref = """\$\{(.+?)\}""".r
    var found = true
    var updatedMap = map
    while(found) {
      found = false;
      updatedMap = updatedMap.map {
        case (key,value) if Ref.findFirstIn(value).nonEmpty =>
          found = true
          val newVal = Ref.replaceAllIn(value,{matcher =>
            map.get(matcher.group(1)) getOrElse(throw new IllegalArgumentException("No substitution for "+matcher.group(1)))
          })
          (key,newVal)
        case entry => entry
      }
    }
    updatedMap
  }

  def apply(key: String) = all.get(key)

  def get(key:String) = apply(key) getOrElse{
    throw new IllegalStateException(key+" is required to be defined.  Most likely it is expected to be a jvm option")
  }
}