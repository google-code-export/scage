package multiglobula

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.ScageAction
import su.msk.dunno.scage.support.Vec

class Node(screen:ScageScreen, val coord:Vec, val velocity:Vec) {

  screen.addAction(new ScageAction {
    override def action = {

    }
  })
}