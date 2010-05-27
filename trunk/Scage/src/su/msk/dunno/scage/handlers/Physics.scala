package su.msk.dunno.scage.handlers

import net.phys2d.raw.strategies.QuadSpaceStrategy
import net.phys2d.math.Vector2f
import su.msk.dunno.scage.prototypes.THandler
import su.msk.dunno.scage.main.Engine
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.objects.StaticLine
import net.phys2d.raw.{Body, World}

object Physics extends THandler {
  val dt = Engine.getIntProperty("dt")
  val gravity = Engine.getIntProperty("gravity")
  val world = new World(new Vector2f(0.0f, gravity), 10, new QuadSpaceStrategy(20,5));
  world.enableRestingBodyDetection(0.01f, 0.000001f, 0.01f)

  def addBody(b:Body) = if(!world.getBodies.contains(b))world.add(b) 
  Engine.addObjects(
    new StaticLine(Vec(0,0), Vec(Renderer.width,0)) ::
    new StaticLine(Vec(Renderer.width,0), Vec(Renderer.width,Renderer.height)) ::
    new StaticLine(Vec(Renderer.width,Renderer.height), Vec(0,Renderer.height)) ::
    new StaticLine(Vec(0,Renderer.height), Vec(0,0))
  )

  override def initSequence() = {
    val objects = Engine.getObjects
    val bodies = world.getBodies
    objects.foreach(o => if(!bodies.contains(o.body))world.add(o.body))
    for(i <- 0 to bodies.size-1; val this_body = bodies.get(i)) objects.find(obj => obj.body.equals(this_body)) match {
      case None => world.remove(this_body)
      case _ =>
    }
  }
  override def updateSequence() = initSequence
  override def actionSequence() = if(!Engine.onPause)for(i <- 1 to dt)world.step()
}