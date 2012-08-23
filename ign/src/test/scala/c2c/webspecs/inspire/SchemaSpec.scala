package c2c.webspecs
package inspire

import org.specs2._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.Matcher
import org.specs2.execute.Result
import java.sql.DriverManager

@RunWith(classOf[JUnitRunner])
class SchemaSpec extends Specification { 

    def is =

    "This is a specification to check the 'Hello world' string" ^
      p ^
      "The 'Hello world' string should" ^
      "contain 11 characters" ! e1 ^
      "start with 'Hello'" ! e2 ^
      "end with 'world'" ! e3 ^
      "open DB" ! checkDB ^
      end
      
  def e1 = "Hello world" must have size (11)
  def e2 = "Hello world" must startWith("Hello")
  def e3 = "Hello world" must endWith("world")

  def checkDB = {
    classOf[org.postgresql.Driver]
    val db = DriverManager.getConnection("jdbc:postgresql://localhost/gis", "postgres", "postgres")
    val st = db.createStatement
    
    val res = st.executeQuery(
      "SELECT gid, id_dept, nom_dept from dep")

    println(" gid   id_dept   nom_dept")
    println("----- --------- ----------")
    
    var count=0
    
    while (res.next) {
      count+=1
      for (val i <- 1 to res.getMetaData.getColumnCount - 1) {
        val r = res.getInt(i).toString
        print(r + "      ".dropRight(r.length))
      }
      val r = res.getString(3)
      print(r + "      ".dropRight(r.length))
      println
    }
    println("Count = " + count)    
    db.close
    
    (count mustEqual 6)
  }
  }