package dummy

class Sample {
  val x = Future.failed[Int](new RuntimeException())
}