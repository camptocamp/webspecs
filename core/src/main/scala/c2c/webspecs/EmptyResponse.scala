package c2c.webspecs
import org.apache.http.Header

object EmptyResponse extends Response[Null] {
  def basicValue = new BasicHttpValue(
    Right(Array[Byte]()),
    200,
    "",
    Map[String,List[Header]](),
    Some(0),
    None,
    None,
    "",
    None
  )

  def value = null
}
