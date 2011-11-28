package net.scage.support

import collection.generic.Growable
import collection.mutable.ArrayBuffer

class SortedBuffer[A <: Ordered[A]](init_arr:ArrayBuffer[A]) extends Growable[A] with Traversable[A] {
  def this(elems:A*) {this(ArrayBuffer(elems:_*))}

  private val arr = init_arr.sortWith(_ < _)

  def clear() {arr.clear()}

  def +=(elem:A) = {
    def pos = arr.indexWhere(elem < _)
    if(pos == -1) arr += elem
    else arr.insert(pos, elem)
    arr.contains(elem)
    this
  }

  def -=(elem:A) = {
    arr -= elem
    this
  }

  def foreach[U](f: A => U) {
    arr.foreach(f)
  }
}

object SortedBuffer {
  def apply[A <: Ordered[A]](init_arr:ArrayBuffer[A]) = new SortedBuffer(init_arr)
  def apply[A <: Ordered[A]](elems:A*) = new SortedBuffer(elems:_*)
}