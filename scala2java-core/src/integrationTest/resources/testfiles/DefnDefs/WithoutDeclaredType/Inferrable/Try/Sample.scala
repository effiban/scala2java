package dummy

class Sample {
  def foo = {
    try {
      "ok"
    } catch {
      case e: IllegalStateException => "illegal state"
      case e: IllegalArgumentException => "illegal argument"
    }
  }
}