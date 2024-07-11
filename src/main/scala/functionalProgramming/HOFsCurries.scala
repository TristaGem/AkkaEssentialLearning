package functionalProgramming

object HOFsCurries extends App {
  // function that applies n times over a value x
  // nTimes(f, n, x)
  def nTimes(f: Int => Int, n: Int, x: Int): Int = {
    if(n <= 0) x
    else {
      nTimes(f, n-1, f(x))
    }
  }

//  def nTimesBetter(n: Int,

}
