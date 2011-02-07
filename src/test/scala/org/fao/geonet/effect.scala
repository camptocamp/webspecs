package org.fao.geonet

import java.net.HttpURLConnection

trait SideEffectFactory {
  def apply(response:Response):SideEffect
}
object JSessionEffectFactory extends SideEffectFactory {
  def apply(response: Response) = new JSessionEffect(response.headers)
}
class ChainedEffectFactory(facs:SideEffectFactory*) extends SideEffectFactory {
  def apply(response: Response) = ((NoEffect:SideEffect) /: facs){case(eff,fac) => eff addEffect fac(response)}
}
abstract class SideEffect() extends SideEffectFactory {
  def apply(response: Response) = this
  def apply(conn: HttpURLConnection):Unit
  def addEffect(effect:SideEffect):SideEffect = new ChainedEffect(this, effect)
}
private class ChainedEffect(effects:SideEffect*) extends SideEffect {
  def apply(conn: HttpURLConnection) = {
    effects.foreach{_.apply(conn)}
  }
}
object NoEffect extends SideEffect {
  def apply(conn: HttpURLConnection) = conn
  override def addEffect(effect: SideEffect) = effect
}

class JSessionEffect(headers:Map[String,String]) extends SideEffect {
  def apply(conn: HttpURLConnection) = {
    headers.get("Set-Cookie").foreach{c => conn.addRequestProperty("Cookie",c)}
    conn
  }
}
