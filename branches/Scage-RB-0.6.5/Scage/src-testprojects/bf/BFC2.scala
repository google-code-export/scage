package bf

import java.io.FileInputStream

object BFC2 {
  def main(args:Array[String]):Unit = {
    val fis = args.find((s:String) => s.startsWith("-file=")) match {
      case Some(s:String) => new FileInputStream(s.replace("-file=","").trim)
      case None => {
        System.out.println("usage: bfc -file=<bf-sourcefile>")
        return
      }
    }
    def fileReader(symbols:List[Char], fis:FileInputStream):List[Char] = {
      if(fis.available > 0) fileReader(symbols ::: List(fis.read.toChar), fis)
      else symbols
    }
    val program = fileReader(List[Char](), fis)

    def findBracket(s:List[Char], from:Int, bracket:Char):Int = {
      val opposite_bracket = bracket match {
        case '[' => ']'
        case ']' => '['
        case _ => '['
      }
      s.indexWhere(_ == opposite_bracket, from+1) match {
        case -1 => s.indexWhere(_ == bracket, from)
        case open_bracket_pos:Int => s.indexWhere(_ == bracket, findBracket(s, open_bracket_pos, bracket)+1)
      }
    }

    def findCloseBracket(from:Int):Int = {
      findBracket(program, from, ']')
    }

    def findOpenBracket(from:Int):Int = {
      program.length-1 - findBracket(program.reverse, program.length-1 - from, '[')
    }

    def checkDataEdges(data:Array[Int], cur_data_pos:Int):Int = {
      if(cur_data_pos >= data.length) checkDataEdges(data, cur_data_pos - data.length)
      else if(cur_data_pos < 0) checkDataEdges(data, cur_data_pos + data.length)
      else cur_data_pos
    }

    def programmRunner(data:Array[Int], cur_program_pos:Int, cur_data_pos:Int):Unit = {
      if(cur_program_pos >= program.length) return
      val cur_data_pos_checked = checkDataEdges(data, cur_data_pos)
      program(cur_program_pos) match {
        case '+' => {
          data(cur_data_pos_checked) = data(cur_data_pos_checked)+1
          programmRunner(data, cur_program_pos+1, cur_data_pos_checked)
        }
        case '-' => {
          data(cur_data_pos_checked) = data(cur_data_pos_checked)-1
          programmRunner(data, cur_program_pos+1, cur_data_pos_checked)
        }
        case '>' => {
          programmRunner(data, cur_program_pos+1, cur_data_pos_checked+1)
        }
        case '<' => {
          programmRunner(data, cur_program_pos+1, cur_data_pos_checked-1)
        }
        case '.' => {
          print(data(cur_data_pos_checked)+" ")
          Thread.sleep(1000)
          programmRunner(data, cur_program_pos+1, cur_data_pos_checked)
        }
        case '[' => {
          programmRunner(data,
                         if(data(cur_data_pos_checked) == 0) findCloseBracket(cur_program_pos)+1 else cur_program_pos+1,
                          cur_data_pos_checked)
        }
        case ']' => {
          programmRunner(data,
                         if(data(cur_data_pos_checked) != 0) findOpenBracket(cur_program_pos) else cur_program_pos+1,
                         cur_data_pos_checked)
        }
        case _ => programmRunner(data, cur_program_pos+1, cur_data_pos_checked)
      }
    }
    programmRunner(new Array[Int](100), 0, 0)
  }
}