package dummy

class Sample {
  val x = Left[Exception, String](new RuntimeException())
}