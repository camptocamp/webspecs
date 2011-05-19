package c2c.webspecs
package generated

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


case class AccumulatedResponse1[+T1,+Z](
    val _1:Response[T1],
    val last:Response[Z])
  extends AccumulatedResponse[Z] {

  def tuple = (
    _1,
    last
  )

  def values = (
    _1.value,
    last.value
  )
}