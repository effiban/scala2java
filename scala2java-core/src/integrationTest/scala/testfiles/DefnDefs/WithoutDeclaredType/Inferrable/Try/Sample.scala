package testfiles.DefnDefs.WithoutDeclaredType.Inferrable.Try

class Sample {
  private def foo = {
    try {
      "ok"
    } catch {
      case e: IllegalStateException => "illegal state"
      case e: IllegalArgumentException => "illegal argument"
    }
  }
}