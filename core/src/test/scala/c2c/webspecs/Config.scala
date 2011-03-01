package c2c.webspecs

import java.net.{InetAddress}
import util.control.Exception

class ExceptionChain(first:Throwable, exception:Throwable*) extends Exception(first.getMessage,first) {
  def all = first +: exception
  override def getMessage = "Multiple Exceptions:"+all.map{_.getMessage}.mkString("\n\t","\n\t","\n--------------------------------\n")

  override def getStackTrace = first.getStackTrace

}

object Config {
  def loadStrategy[T](property:String):Either[IllegalArgumentException,Class[T]] = Properties(property) match {
    case Some(strategyName) =>
      val fullClassName = Exception.allCatch.opt(Class.forName(strategyName).asInstanceOf[Class[T]])
      val appendedPackage = Exception.allCatch.opt(Class.forName("c2c.webspecs."+ strategyName).asInstanceOf[Class[T]])
      fullClassName orElse appendedPackage match {
        case Some(instance) => Right(instance)
        case None => Left(new IllegalArgumentException("Tried "+strategyName+" and c2c.webspecs."+strategyName+" but was unable to create a SystemLifeCycle implementation"))
      }
    case _ =>
      Left(new IllegalArgumentException("the '"+property+"' property is required"))
  }

  def resolveURI(uri:String,params:(String,String)*) = {
    loadStrategy[UriResolver]("uriResolver") fold (
      _ => uri,
      _.newInstance()(uri,params)
    )
  }
}
class Config(val specName:String) extends Log {
  def inputStream(path:String) = Option(getClass.getClassLoader.getResourceAsStream(path)) getOrElse {throw new IllegalArgumentException(path+" is not an available resource")}
  val lifeCycle = SystemLifeCycle(this)

  def setUpTestEnv(implicit context:ExecutionContext) = {
    try {
      log(LifeCycle, "Setup Test Environment")

      lifeCycle.setup(context)

      log(LifeCycle, "Done Setting up Test Environment \r\n\r\n\r\n")
    } catch {
      case e:Throwable =>
        util.control.Exception.catching(classOf[Throwable]).either(tearDownTestEnv) match {
          case Right(_) => throw e
          case Left(error:Throwable) => throw new ExceptionChain(e,error)
        }
    }
  }


  def tearDownTestEnv(implicit context:ExecutionContext) = {

    try {
      log(LifeCycle, "\r\n\r\n\r\nTearing down Test Environment")
      lifeCycle.tearDown(context)
    } catch {
      case e:Throwable =>
        System.err.println("Error occurred during teardown: "+e)
        e.printStackTrace(System.err)
    }
  }

}

