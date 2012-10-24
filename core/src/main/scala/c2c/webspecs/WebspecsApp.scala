package c2c.webspecs

import scala.compat.Platform.currentTime
import scala.collection.mutable.ListBuffer

trait WebspecsApp extends DelayedInit {
  def referenceSpecClass: Class[_] = getClass
  Properties.specClass = referenceSpecClass
  /** The time when the execution of this program started, in milliseconds since 1
    * January 1970 UTC. */
  val executionStart: Long = currentTime

  /** The command line arguments passed to the application's `main` method.
   */
  protected def args: Array[String] = _args

  private var _args: Array[String] = _

  private val initCode = new ListBuffer[() => Unit]
  implicit val executionContext = new DefaultExecutionContext
  implicit val uriResolver = Config.defaultUriResolver

  /** The init hook. This saves all initialization code for execution within `main`.
   *  This method is normally never called directly from user code.
   *  Instead it is called as compiler-generated code for those classes and objects
   *  (but not traits) that inherit from the `DelayedInit` trait and that do not themselves define
   *  a `delayedInit` method.
   *  @param body the initialization code to be stored for later execution
   */
  override def delayedInit(body: => Unit) {
    initCode += (() => 
      try body
      finally executionContext.close())
  }

  /** The main method.
   *  This stores all argument so that they can be retrieved with `args`
   *  and the executes all initialization code segments in the order they were
   *  passed to `delayedInit`
   *  @param args the arguments passed to the main method
   */
  def main(args: Array[String]) = {
    this._args = args
    for (proc <- initCode) proc()
    if (util.Properties.propIsSet("scala.time")) {
      val total = currentTime - executionStart
      Console.println("[total " + total + "ms]")
    }
  }
}