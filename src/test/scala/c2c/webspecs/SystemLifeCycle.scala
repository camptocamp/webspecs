package c2c.webspecs

import util.control.Exception

trait SystemLifeCycle[+C <: Config] {
  def tearDown[A >: C](config:A):Unit
  def setup[A >: C](config:A):Unit
}

object SystemLifeCycle {
  def apply[C <: Config]() = Config.loadStrategy[SystemLifeCycle[C]]("lifecyle").fold(throw _, i=>i.newInstance())
}