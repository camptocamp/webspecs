package c2c.webspecs

import util.control.Exception
import java.io.File

class ExceptionChain(first:Throwable, exception:Throwable*) extends Exception(first.getMessage,first) {
  def all = first +: exception
  override def getMessage = "Multiple Exceptions:"+all.map{_.getMessage}.mkString("\n\t","\n\t","\n--------------------------------\n")

  override def getStackTrace = first.getStackTrace

}

object Config {
  private def loadClass[T](name:String) = {
    (Exception.allCatch.opt(Class.forName(name).asInstanceOf[Class[T]]) orElse
    	Exception.allCatch.opt(Properties.classLoader.loadClass(name).asInstanceOf[Class[T]]))
  }
  def loadStrategy[T](property:String):Either[IllegalArgumentException,Class[T]] = Properties(property) match {
    case Some(strategyName) =>
      val fullClassName = loadClass[T](strategyName)
      val appendedPackage = loadClass[T]("c2c.webspecs."+ strategyName)
      fullClassName orElse appendedPackage match {
        case Some(instance) => Right(instance)
        case None => Left(new IllegalArgumentException("Tried "+strategyName+" and c2c.webspecs."+strategyName+" but was unable to create a SystemLifeCycle implementation"))
      }
    case _ =>
      Left(new IllegalArgumentException("the '"+property+"' property is required"))
  }

  lazy val defaultUriResolver = loadStrategy[UriResolver]("uriResolver").fold(_ => {
    Log.apply(Log.Warning, "No uriResolver defined, using the default")
    IdentityUriResolver
  },
  r => r.newInstance())
  
  def resolveURI(uri:String,params:(String,String)*) = defaultUriResolver(uri, params)
}

class Config(val specName:String) extends Log {
  def resourceFile[T](path:String)(implicit resourceBase:Class[T]) = {
    Option(resourceBase.getClassLoader.getResource(path)).
      map {url => new File(url.getFile)}.
      getOrElse {throw new IllegalArgumentException(path+" is not an available resource")}
  }
  def resourceStream[T](path:String)(implicit resourceBase:Class[T]) = {
    Option(resourceBase.getClassLoader.getResourceAsStream(path)).
      getOrElse {throw new IllegalArgumentException(path+" is not an available resource")}
  }
  val lifeCycle = SystemLifeCycle(this)

  def setUpTestEnv(implicit context:ExecutionContext) = {

    log(LifeCycle, "Setup Test Environment")
    try {

      lifeCycle.setup(context,Config.defaultUriResolver)

      log(LifeCycle, "Done Setting up Test Environment \r\n\r\n\r\n")
    } catch {
      case e:Throwable =>
        Log(Log.Error, "Exception occurred during setup:"+e.getMessage+"\n" + e.getStackTraceString)
        util.control.Exception.catching(classOf[Throwable]).either(tearDownTestEnv) match {
          case Right(_) => throw e
          case Left(error:Throwable) => throw new ExceptionChain(e,error)
        }
    }
  }


  def tearDownTestEnv(implicit context:ExecutionContext) = {
    try {
      log(LifeCycle, "\r\n\r\n\r\nTearing down Test Environment")
      lifeCycle.tearDown(context,Config.defaultUriResolver)
    } catch {
      case e:Throwable =>
        Log(Log.Error, "Exception occurred during teardown:"+e.getMessage+"\n" + e.getStackTraceString)
    }
  }

}

