package c2c.webspecs

trait SystemLifeCycle[+C <: Config] {
  def tearDown[A >: C](config:A):Unit
  def setup[A >: C](config:A):Unit
}

class NoActionLifeCycle extends SystemLifeCycle[Config] {
  def setup[A >: Config](config: A) = ()
  def tearDown[A >: Config](config: A) = ()
}

object SystemLifeCycle {
  def apply[C <: Config]() = Config.loadStrategy[SystemLifeCycle[C]]("lifecycle").fold(throw _, i=>i.newInstance())
}