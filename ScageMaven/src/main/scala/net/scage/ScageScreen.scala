package net.scage

import handlers.controller2.{MultiController, SingleController}
import handlers.Renderer

class ScageScreen(val unit_name:String = "Scage App", val is_main_unit:Boolean = false, val properties:String = "")
extends Scage with Renderer with SingleController

class MultiControlledScreen(val unit_name:String = "Scage App", val is_main_unit:Boolean = false, val properties:String = "")
extends Scage with Renderer with MultiController
