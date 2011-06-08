package c2c.webspecs
import collection.JavaConverters._
import java.io.File
import scalax.file.PathMatcher

object FindSpecs {
	def specsIn(pattern:PathMatcher, packageName:String, classLoader:ClassLoader) = {
        assert (classLoader != null);
        
        val path = packageName.replace('.', '/');
        val resources = classLoader.getResources(path).asScala;

        // TODO handle case where classes are in a jar 
        resources.map{resource => 
          findClasses(new File(resource.toURI), path)
        }
    }
	private def findClasses(dir:File,packageName:String):List[Class[_]] = {
	    if (!dir.exists()) {
            Nil;
        } else {
        	dir.listFiles().toList.
        		filter{_.getName endsWith ".class"}.
        		flatMap{ 
        		  case dir if dir.isDirectory => 
        		    findClasses(dir, packageName + "." + dir.getName())
        		  case file =>
        		    val className = packageName + '.' + file.getName().dropRight(6)
        		    List(Class.forName(className))
        		}
        }
	}
}