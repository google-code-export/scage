package scagetest.objects

import su.msk.dunno.scage.objects.DynaBox
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.tracer.{Tracer, State, Trace}

class Box(leftup_coord:Vec) extends DynaBox(leftup_coord, 50, 50) {
  val trace = new Trace {
    		def getCoord = coord()
    		def getState = new State("name", "Box")
    		def changeState(s:State) = {
          if(s.contains("pull"))addForce((s.getVec("pull") - coord).n*1000)
        }
  }
  Tracer.addTrace(trace)
}