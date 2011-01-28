package helloworld

object StringTest extends Application {
  val s = "\n\n    \n        [Стрелки, Цифровые клавиши] - Выбрать цель [Enter] - Установить/вынуть модификатор\n        [Esc] - Отмена/Выход\n    "
  val t = s.trim
  val t1 = t.split("\n")
  val t2 = t1.foldLeft(new StringBuilder)((sb, part) => sb.append("\n"+part.trim)).toString.trim
  println(t2)
}