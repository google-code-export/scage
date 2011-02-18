package lulz

object GossipTest extends Application {
  def numCalls(n:Int):Int = {
    if(n == 2) 1
    else if(n == 3) 3
    else n/2 + numCalls(n/2)*2 + (if(n%2 != 0) 2 else 0)
  }

  for(i <- 2 to 20) println(i+": "+numCalls(i))
}