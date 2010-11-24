package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.support.Vec

abstract class Living(val point:Vec) {
  val trace = -1
  
  private var last_action_time = 0
  def lastActionTime = last_action_time
  def lastActionTime_=(action_time:Int) = last_action_time = action_time
}
