package c2c.webspecs


trait Response[+A] {
  def value:A
  def basicValue:BasicHttpValue
  def map[B](mapping:A => B) = {
    val outer = this;
    new Response[B] {
      def basicValue: BasicHttpValue = outer.basicValue
      def value: B = mapping(outer.value)
    }
  }
}
object Response {
  def apply[A](constValue:A) = new Response[A]{
      val basicValue = EmptyResponse.basicValue
      val value = constValue
    }
}

