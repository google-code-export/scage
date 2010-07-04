package su.msk.dunno.scage.objects

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.AI

class Platform(val leftup_start:Vec, val leftup_end:Vec, val speed:Int) 
extends StaticBox(leftup_start, 140, 10) {
	def this(leftup_start:Vec, leftup_end:Vec) = this(leftup_start, leftup_end, 1)
	
	val step = (leftup_end - leftup_start).n
	var dir = 1
	val start_condition:(Vec) => Boolean = (v:Vec) => {
		if(Math.min(leftup_start.x, leftup_start.y) == leftup_start.x) v.x < leftup_start.x
		else v.y < leftup_start.y
	}
	val end_condition:(Vec) => Boolean = (v:Vec) => {
		if(Math.min(leftup_end.x, leftup_end.y) == leftup_end.x) v.x > leftup_end.x
		else v.y > leftup_end.y
	}
	
    AI.registerAI(() => {
    	val vec = new Vec(body.getPosition)+step*dir*speed
    	if(start_condition(vec))dir = 1
    	else if(end_condition(vec))dir = -1
    	body.setPosition(vec.x, vec.y)
    })
}