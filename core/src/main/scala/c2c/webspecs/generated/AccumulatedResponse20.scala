package c2c.webspecs
package generated

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


case class AccumulatedResponse20[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+T18,+T19,+T20,+Z](
    val _1:Response[T1],
    val _2:Response[T2],
    val _3:Response[T3],
    val _4:Response[T4],
    val _5:Response[T5],
    val _6:Response[T6],
    val _7:Response[T7],
    val _8:Response[T8],
    val _9:Response[T9],
    val _10:Response[T10],
    val _11:Response[T11],
    val _12:Response[T12],
    val _13:Response[T13],
    val _14:Response[T14],
    val _15:Response[T15],
    val _16:Response[T16],
    val _17:Response[T17],
    val _18:Response[T18],
    val _19:Response[T19],
    val _20:Response[T20],
    val last:Response[Z])
  extends AccumulatedResponse[Z] {

  def tuple = (
    _1,
    _2,
    _3,
    _4,
    _5,
    _6,
    _7,
    _8,
    _9,
    _10,
    _11,
    _12,
    _13,
    _14,
    _15,
    _16,
    _17,
    _18,
    _19,
    _20,
    last
  )

  def values = (
    _1.value,
    _2.value,
    _3.value,
    _4.value,
    _5.value,
    _6.value,
    _7.value,
    _8.value,
    _9.value,
    _10.value,
    _11.value,
    _12.value,
    _13.value,
    _14.value,
    _15.value,
    _16.value,
    _17.value,
    _18.value,
    _19.value,
    _20.value,
    last.value
  )
}