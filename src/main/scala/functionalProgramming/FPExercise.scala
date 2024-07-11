package functionalProgramming

object FPExercise extends App{
  val concate = new Function2[String, String, String] {

    override def apply(v1: String, v2: String): String = v1 + v2
  }

  println(concate("a", "b"))
  val concate1 = concate("hello", _)
  println(concate1("wolrd"))


  val specialFunction: Function1[Int, Function1[Int, Int]] = new Function1[Int, Function1[Int, Int]] {
    override def apply(v1: Int): Int => Int = new Function1[Int, Int] {
      override def apply(v2: Int): Int = v1 + v2
    }
  }
  println(specialFunction(3)(2))

  val spFunction: Int => Int => Int = x => {
    y => {
      x + y
    }
  }

  println(spFunction(2)(3))

  val spFunction2 = (x: Int) => (y: Int) => x+y
  println(spFunction2(2)(3))
  // higher order function: a function that either takes a function as parameter or return a function
}
trait MyPredicate[-T] {
  def test(elem: T):Boolean
}
