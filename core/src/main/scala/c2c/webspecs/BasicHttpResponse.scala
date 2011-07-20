package c2c.webspecs

class BasicHttpResponse[+A](val basicValue:BasicHttpValue,val value:A) extends Response[A]
