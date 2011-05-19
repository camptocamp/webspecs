package c2c.webspecs
package generated

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


case class AccumulatedResponse3[+T1,+T2,+T3,+Z](
    val _1:Response[T1],
    val _2:Response[T2],
    val _3:Response[T3],
    val last:Response[Z])
  extends AccumulatedResponse[Z] {

  def tuple = (
    _1,
    _2,
    _3,
    last
  )

  def values = (
    _1.value,
    _2.value,
    _3.value,
    last.value
  )
}