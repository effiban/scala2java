package testfiles.DefnDefs.WithTypeParams.OneWithUpperBound

trait Sample {
  def foo[T <: String](): Unit = {}
}