package net.scage.support.tracer3

import net.scage.support.Vec

trait HaveLocationAndId {   // maybe make HaveId as separate trait
  def id:Int
  def location:Vec
}