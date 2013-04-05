package c2c.webspecs

import System.{getProperties,getenv}
import java.util.{Properties => JProperties}
import scalax.file.Path
import collection.JavaConverters._
import java.io.InputStream
import scalax.io.{Resource}
import java.lang.IllegalArgumentException
import java.net.URL
import scalax.io.managed.InputStreamResource

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
 * toThen system property webspecs.config.resolver can be set load a custom [[c2c.webspecs.Properties.ConfigFileResolver]]
 * implementation.
 */
object Properties {

  var specClass:Class[_] = _
  def classLoader:ClassLoader = specClass.getClassLoader
  def TEST_TAG = "{automated_test_metadata}"
  lazy val testServer = apply("test.server") getOrElse "localhost:8080"
  lazy val httpsServer = apply("https.server") getOrElse "localhost:8443"
  lazy val all:Map[String,String] = {
    val sysProps = getProperties.asScala /// NEED to load system and env properties and add them all to the map
    val envProps = getenv.asScala /// NEED to load system and env properties and add them all to the map

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
    val sysProps = getProperties.asScala /// NEED to load system and env properties and add them all to the map
    val envProps = getenv.asScala /// NEED to load system and env properties and add them all to the map

    // calculate the directory that is the relative directory and
    // the base configuration file and whether the files come from
    // the file system or from a jar via classpath resources loading
    val (baseDir,baseConfigFile) = {
      val path = sysProps.get("webspecs.config") orElse envProps.get("webspecs.config") getOrElse "config.properties"

      Log(Log.LifeCycle,"Loading configuration properties from "+path)
      
      val dir = Path.fromString(path).parent.map{_.path} getOrElse "."
      val file = Path.fromString(path).name
      
      if(util.control.Exception.allCatch.opt(load(dir,file)).isDefined) {
        (dir,file)
      } else {
        defaultOnClassPath("config.properties","defaultConfig.properties") getOrElse (dir,file)
      }
    }

    private def defaultOnClassPath(filenameOptions:String*): Option[(String, String)] = {
      def interfaces(cl:Class[_]):List[Class[_]] = {
        val int = Option(cl).flatMap(i => Option(i.getInterfaces())).toList.flatten
        (cl :: int ::: int.flatMap(interfaces)).filter(cl => classOf[WebSpecsSpecification[_]].isAssignableFrom(cl) || classOf[WebspecsApp].isAssignableFrom(cl))
      }
      val allInterfaces = interfaces(specClass)
      
      val discovered = for {
        webspecInterface <- allInterfaces
        resource <- filenameOptions.foldLeft(None:Option[URL]) {(result, nextFileName) =>
          result orElse Option(webspecInterface.getResource(nextFileName))
        }
      } yield {
        val path = Path(resource.toURI).get
        (path.parent.get.path, path.name)
      }
      discovered.headOption
    }

    def load(file: String): InputStreamResource[InputStream] = {
        load(baseDir, file)
    }
    private def load(baseDir:String, file: String) = {
      val relativePath = Path.fromString(baseDir) resolve Path(file.split('/'):_*)
      val rawPath = Path.fromString(file)
      val cl = Properties.classLoader
      val loadedFromClasspath = (Option(cl.getResource(baseDir + "/" + file)) orElse
        Option(cl.getResource(file)) orElse Option(cl.getResource("/" + baseDir + "/" + file))).map(Resource.fromURL(_).inputStream)
      val defaults = (defaultOnClassPath(file,rawPath.name).map(paths => (Path.fromString(paths._1) / paths._2).inputStream))
      val resources = loadedFromClasspath orElse defaults
      if (relativePath.exists) {
        println("Loading configuration file: "+relativePath)
        relativePath.inputStream
      } else if (rawPath.exists) {
        println("Loading configuration file: "+rawPath)
        rawPath.inputStream
      } else if (resources.isDefined) {
        println("Loading configuration file: "+resources)
        resources.get
      } else {
        throw new IllegalArgumentException("The configuration file " + file + " was not found either as a raw file string or as relative to " + baseDir)
      }
    }
  }
}