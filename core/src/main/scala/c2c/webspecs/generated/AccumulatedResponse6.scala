package c2c.webspecs
package generated

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


case class AccumulatedResponse6[+T1,+T2,+T3,+T4,+T5,+T6,+Z](
    val _1:Response[T1],
    val _2:Response[T2],
    val _3:Response[T3],
    val _4:Response[T4],
    val _5:Response[T5],
    val _6:Response[T6],
    val last:Response[Z])
  extends AccumulatedResponse[Z] {

  def tuple = (
    _1,
    _2,
    _3,
    _4,
    _5,
    _6,
    last
  )

  def values = (
    _1.value,
    _2.value,
    _3.value,
    _4.value,
    _5.value,
    _6.value,
    last.value
  )
}