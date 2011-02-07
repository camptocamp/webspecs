package org.fao.geonet

import actors.Futures

/**
 * Only one stress spec should be run at a time or the number of threads will not be correct
 */
abstract class StressSpecification(threads:Int,timeout:Long=10 * 60 * 1000) extends GeonetworkSpecification {
  System.setProperty("actors.maxPoolSize",threads.toString)
  System.setProperty("actors.corePoolSize",threads.toString)

  def exec[R](test: => R) = {
    run(new Request(){
      def exec(sideEffect: Option[SideEffect]) = {
        test
        new EmptyResponse(this)
      }
    })
  }
  def run(request:Request):Unit = validate(request)(_=>())
  def validate(request:Request)(validation:Response => Any):Unit = {
    val futures = 1 to threads map { i =>
      Futures.future[Either[Throwable,String]] {
        util.control.Exception.allCatch.either {
          println("Starting user simulation "+i)

          request (validation)

          println("Finished user simulation for "+i)
          i+"has finished"
        }
      }
    }

    val results = Futures.awaitAll(timeout, futures:_*)
    val finishedResults = results.filterNot{_ == None} map {_.get.asInstanceOf[Either[Throwable,String]]}

    val failures = (finishedResults filter {_.isLeft})

    if(failures.nonEmpty) {
      import System.err
      println("*********************************************************************************************************")
      println("start of failures")
      failures collect {
        case Left(error) => error.printStackTrace(System.out)
      }
      println("end of failures")
      println("*********************************************************************************************************")
      err.println("Successes: "+(finishedResults.collect{case Right(x) => x}))
      err.println("Successes: "+(finishedResults.size - failures.size)+" Failures:"+(failures.size)+" out of "+finishedResults.size)
    }

    results must haveSize (futures.size)
    failures must beEmpty

  }
}
