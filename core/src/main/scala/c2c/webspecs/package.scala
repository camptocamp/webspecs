package c2c
import scala.xml.{Node,NodeSeq}
import java.net.URLEncoder
import java.net.URLDecoder

package object webspecs 
{
  private val AnyNamespace = """_(:\S+)""".r
  private val AnyName = """(\S+:)_""".r

   implicit def addAttributeSelector(node:Node) = new {
    def @@(attName:String) = getAtt(node,attName)
  }
  implicit def addSeqAttributeSelector[N <% NodeSeq](seq:N) = new {
    def \@(name:String) = seq.flatMap(n => getAtt(n,name))
  }
  private def getAtt(node:Node, attName:String):List[String] = attName match {
      case AnyName(namespace) =>
          node.attributes.asAttrMap.toList.collect{
            case (key,value) if key.startsWith(namespace) => value
          }
      case AnyNamespace(name) =>
          node.attributes.asAttrMap.toList.collect{
              case (key,value) if key.endsWith(name) || key == name => value
          }
      case _ =>
        node.attributes.asAttrMap.get(attName).toList
    }

  implicit def encodeableString(s:String) = new {
    def encode = URLEncoder.encode(s,"UTF-8")
	def decode = URLDecoder.decode(s,"UTF-8")
  }

  implicit def executeForAny[U](request:Request[Any,U]) = new {
    def execute()(implicit c:ExecutionContext, uriResolvers:UriResolver) = request.execute(None)(c, uriResolvers)
  }
}