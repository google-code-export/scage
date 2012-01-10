package net.scage.support.parsers

import scala.math._
import scala.util.parsing.combinator._
import scala.util.Random
import com.weiglewilczek.slf4s.Logger

/**
 * Simple parser for arithmetic expressions based on Scala's Combinator Parsers framework
 * author: Peter Schmitz
 * url: http://stackoverflow.com/questions/5805496/arithmetic-expression-grammar-and-parser
 *
 * Usage example:
 * val formulaParser = new FormulaParser(
 *   constants = Map("radius" -> 8D,
 *                  "height" -> 10D,
 *                  "c" -> 299792458, // m/s
 *                  "v" -> 130 * 1000 / 60 / 60, // 130 km/h in m/s
 *                  "m" -> 80),
 *    userFcts  = Map("perimeter" -> { _.toDouble * 2 * Pi } ))
 *
 * println(formulaParser.evaluate("2+3*5")) // 17.0
 * println(formulaParser.evaluate("height*perimeter(radius)")) // 502.6548245743669
 * println(formulaParser.evaluate("m/sqrt(1-v^2/c^2)"))  // 80.00000000003415
 */

class FormulaParser(val constants: Map[String,Double] = Map(), val userFcts: Map[String,String => Double] = Map(), random: Random = new Random) extends JavaTokenParsers {
  require(constants.keySet.intersect(userFcts.keySet).isEmpty)

  private val log = Logger(this.getClass.getName)

  private lazy val allConstants = constants ++ Map("E" -> E, "PI" -> Pi, "Pi" -> Pi) // shouldn´t be empty
  private lazy val unaryOps: Map[String,Double => Double] = Map(
   "sqrt" -> (sqrt(_)), "abs" -> (abs(_)), "floor" -> (floor(_)), "ceil" -> (ceil(_)), "ln" -> (math.log(_)), "round" -> (round(_)), "signum" -> (signum(_))
  )
  private lazy val binaryOps1: Map[String,(Double,Double) => Double] = Map(
   "+" -> (_+_), "-" -> (_-_), "*" -> (_*_), "/" -> (_/_), "^" -> (pow(_,_))
  )
  private lazy val binaryOps2: Map[String,(Double,Double) => Double] = Map(
   "max" -> (max(_,_)), "min" -> (min(_,_))
  )
  private def fold(d: Double, l: List[~[String,Double]]) = l.foldLeft(d){ case (d1,op~d2) => binaryOps1(op)(d1,d2) }
  private implicit def map2Parser[V](m: Map[String,V]) = m.keys.map(_ ^^ (identity)).reduceLeft(_ | _)
  private lazy val expression:  Parser[Double] = sign~term~rep(("+"|"-")~term) ^^ { case s~t~l => fold(s * t,l) }
  private lazy val sign:        Parser[Double] = opt("+" | "-") ^^ { case None => 1; case Some("+") => 1; case Some("-") => -1 }
  private lazy val term:        Parser[Double] = longFactor~rep(("*"|"/")~longFactor) ^^ { case d~l => fold(d,l) }
  private lazy val longFactor:  Parser[Double] = shortFactor~rep("^"~shortFactor) ^^ { case d~l => fold(d,l) }
  private lazy val shortFactor: Parser[Double] = fpn | sign~(constant | rnd | unaryFct | binaryFct | userFct | "("~>expression<~")") ^^ { case s~x => s * x }
  private lazy val constant:    Parser[Double] = allConstants ^^ (allConstants(_))
  private lazy val rnd:         Parser[Double] = "rnd"~>"("~>fpn~","~fpn<~")" ^^ { case x~_~y => require(y > x); x + (y-x) * random.nextDouble } | "rnd" ^^ { _ => random.nextDouble() }
  private lazy val fpn:         Parser[Double] = floatingPointNumber ^^ (_.toDouble)
  private lazy val unaryFct:    Parser[Double] = unaryOps~"("~expression~")" ^^ { case op~_~d~_ => unaryOps(op)(d) }
  private lazy val binaryFct:   Parser[Double] = binaryOps2~"("~expression~","~expression~")" ^^ { case op~_~d1~_~d2~_ => binaryOps2(op)(d1,d2) }
  private lazy val userFct:     Parser[Double] = userFcts~"("~(expression ^^ (_.toString) | ident)<~")" ^^ { case fct~_~x => userFcts(fct)(x) }

  def evaluate(formula: String) = {
    val result = parseAll(expression, formula).get
    log.debug("parsed formula: "+formula+" = "+result)
    result
  }
}