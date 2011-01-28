package bf

import java.lang.String
import java.io.FileInputStream
import scala.Array

object BFC {
  def main(args:Array[String]):Unit = {
    val fis = args.find((s:String) => s.startsWith("-file=")) match {
      case Some(s:String) => new FileInputStream(s.replace("-file=","").trim)
      case None => {
        System.out.println("usage: bfc -file=<bf-sourcefile>")
        return
      }
    }

    val program:Array[Char] = {
      def _obtainProgram(fis:FileInputStream, acc:List[Char]):List[Char] = {
        if(fis.available > 0) {
          val new_acc = List[Char](fis.read.toChar) ::: acc
          _obtainProgram(fis, new_acc)
        }
        else acc
      }
      _obtainProgram(fis, List[Char]()).reverse.toArray[Char]
    }

    new BFMachine(program).processProgram
  }

  class BFMachine(program:Array[Char]) {
    var arr:Array[Int] = new Array[Int](1000)
    var cur_pos:Int = 0
    var cur_program_pos = 0
    var inner_cycles:Int = 0

    def processProgram():Unit = {
      while(cur_program_pos != program.length) {
        val before_process = cur_program_pos
        process(program.apply(cur_program_pos))
        if(cur_program_pos == before_process)cur_program_pos += 1
      }
      System.out.println()
    }

    def process(c:Char):Unit = {
      c match {
        case '+' => arr.update(cur_pos,arr.apply(cur_pos)+1)
        case '-' => arr.update(cur_pos,arr.apply(cur_pos)-1)
        case '>' => cur_pos = cur_pos+1
        case '<' => cur_pos = cur_pos-1
        case '.' => {
          System.out.print(arr.apply(cur_pos)/*.toChar*/+" ")
          Thread sleep 1000
        }
        case '[' => {
          if(arr.apply(cur_pos) == 0) {
            inner_cycles = -1
            while(program.apply(cur_program_pos) != ']' && cur_program_pos < program.length-1 || inner_cycles != 0) {
              val cc:Char = program.apply(cur_program_pos)
              if(cc == '[')inner_cycles += 1
              if(cc == ']' && inner_cycles > 0)inner_cycles -= 1
              cur_program_pos += 1
            }
            cur_program_pos += 1
          }
        }
        case ']' => {
          if(arr.apply(cur_pos) != 0) {
            inner_cycles = -1
            while(program.apply(cur_program_pos) != '[' && cur_program_pos > 0 || inner_cycles != 0) {
              val cc:Char = program.apply(cur_program_pos)
              if(cc == ']') inner_cycles += 1
              if(cc == '[' && inner_cycles > 0)inner_cycles -= 1
              cur_program_pos -= 1
            }
          }
        }
        case _ =>
      }
      while(cur_pos < 0 || cur_pos >= arr.length) {
        if(cur_pos < 0)cur_pos += arr.length
        if(cur_pos >= arr.length) cur_pos -= arr.length
      }
    }
  }
}