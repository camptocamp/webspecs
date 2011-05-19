package c2c.webspecs
package accumulating

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


case class AccumulatedResponse2[+T1,+T2,+Z](
    val _1:Response[T1],
    val _2:Response[T2],
    val last:Response[Z])
  extends AccumulatedResponse[Z] {

  def tuple = (
    _1,
    _2,
    last
  )

  def values = (
    _1.value,
    _2.value,
    last.value
  )
}