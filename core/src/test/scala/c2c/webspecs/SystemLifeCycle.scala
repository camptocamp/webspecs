package c2c.webspecs

import util.control.Exception

/**
 * Implementation either take a config object as a parameter or no parameters
 */
trait SystemLifeCycle {
  def tearDown(implicit context:ExecutionContext):Unit
  def setup(implicit context:ExecutionContext):Unit
}

class NoActionLifeCycle extends SystemLifeCycle {
  def setup(implicit context:ExecutionContext) = ()
  def tearDown(implicit context:ExecutionContext) = ()
}

object SystemLifeCycle {
  def apply(config:Config) = {
    Config.loadStrategy[SystemLifeCycle]("lifecycle").fold(
      e => {
        e.printStackTrace(System.err)
        throw e
      },
      i=>{
        val instance = Exception.allCatch.opt{i.getConstructor(classOf[Config]).newInstance(config)}
        instance getOrElse {i.newInstance()}
      }
    )
  }
}