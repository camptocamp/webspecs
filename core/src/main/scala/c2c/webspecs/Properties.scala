package c2c.webspecs

import System.{getProperties,getenv}
import java.util.{Properties => JProperties}
import scalax.file.Path
import collection.JavaConverters._
import java.io.InputStream
import scalax.io.{InputStreamResource, Resource}
import java.lang.IllegalArgumentException

/**
 * Loads the configuration from properties files.  The configuration files
 * are essentially just properties but if a property has a ${...} the variable
 * will be substituted from the other properties.
 *
 * ${...} variables will be recursively resolved until all variables are resolved.
 *
 * All configuration properties will be overridden by system and environment variables.
 * Precedence is the following:  system properties, environment properties toThen configuration file properties
 *
 * variable resolution is done at the end so system, env and file properties can all have variables and will be
 * resolved after all are added.
 *
 * Configuration files can be combined with the @override and @include directives.  The @override and @include directive
 * are properties that reference a comma separated list of files for example: @include=file1.properties,gn/file2.properties
 *
 * the @include directive will load the current file toThen load the indicated file so that all properties
 * in the *include" file will override the properties in the current file.
 *
 * The @override directive will load the indicated file and toThen load the current file.  In this case the included
 * properties can be overridden by the properties in the current file.
 *
 * By default the webspecs.config system properties is checked to find the configuration file to use.  If it does not exist
 * toThen the config.properties file is loaded as the configuration.  The filesystem and the classloader getResources
 * method are both checked for the file.
 *
 * If another strategy for loading the configuration files (like from a database or remote server) is required
 * toThen system property webspecs.config.resolver can be set load a custom [[c2c.webspecs.Properties.ConfigFileLoader]]
 * implementation.
 */
object Properties {

  def TEST_TAG = "{automated_test_metadata}"
  lazy val testServer = apply("test.server") getOrElse "localhost:8080"
  lazy val all:Map[String,String] = {
    val sysProps = getProperties.asScala /// NEED to load sstem and env properties and add them all to the map
    val envProps = getenv.asScala /// NEED to load sstem and env properties and add them all to the map

    val resolver = sysProps.get("webspecs.config.resolver").map{
      c => Thread.currentThread().getContextClassLoader.loadClass(c).asInstanceOf[ConfigFileResolver]
    }.getOrElse(new DefaultFileResolver())

    def load(file:String):Map[String,String] = {
      resolver.load(file).acquireAndGet{ in =>
        val p = new JProperties()
        p.load(in)
        var propMap = p.asScala.toMap.asInstanceOf[Map[String,String]]
        propMap.get("@includes") map { includes =>
          propMap -= "@includes"
          propMap ++= (includes split ",").flatMap {load(_)}
        }
        propMap.get("@override") map { overrides =>
          propMap -= "@override"
          propMap = (overrides split "," flatMap {load(_)}).toMap ++ propMap
        }
        propMap
      }
    }

    val props = load(resolver.baseConfigFile)
    val allProps = props ++ envProps ++ sysProps
    resolveReferences(allProps)
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

  /**
   * Strategy for configuring where to load configuration files from.
   *
   * System property "webspecs.config.resolver" is checked to see if it is defined
   * if it is that resolver will be used otherwise the [[c2c.webspecs.Properties.DefaultFileResolver]] will
   * be used.
   */
  trait ConfigFileResolver {
    /**
     * The name of the base configuration file
     */
    def baseConfigFile:String

    /**
     * Called to access configuration files
     */
    def load(file:String):InputStreamResource[InputStream]
  }

  /**
   * Looks for the parameter webspecs.config for the base config file to load params from.
   * Will look for the file on filesystem and in classpath resource path.
   */
  class DefaultFileResolver extends ConfigFileResolver {
    val sysProps = getProperties.asScala /// NEED to load sstem and env properties and add them all to the map
    val envProps = getenv.asScala /// NEED to load sstem and env properties and add them all to the map

    // calculate the directory that is the relative directory and
    // the base configuration file and whether the files come from
    // the file system or from a jar via classpath resources loading
    val (baseDir,baseConfigFile) = {
      val path = sysProps.get("webspecs.config") orElse envProps.get("webspecs.config") getOrElse "config.properties"

      Log(Log.LifeCycle,"Loading configuration properties from "+path)

      val dir = Path(path).parent.map{_.path} getOrElse "."
      (dir,Path(path).name)
    }

    def load(file: String): InputStreamResource[InputStream] = {
      val relativePath = Path(baseDir) \ file
      val rawPath = Path(file)
      val cl = Thread.currentThread().getContextClassLoader
      val relativeResource = cl.getResource(baseDir+"/"+file)
      val rawResource = cl.getResource(file)
      if(relativePath.exists) {
        relativePath.inputStream()
      } else if(rawPath.exists) {
        rawPath.inputStream()
      } else if (relativeResource != null) {
        Resource.fromURL(relativeResource)
      } else if (rawResource != null) {
        Resource.fromURL(rawResource)
      } else {
        throw new IllegalArgumentException("The configuration file "+file+" was not found either as a raw file string or as relative to "+baseDir)
      }
    }
  }
}