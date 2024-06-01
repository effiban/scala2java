package testfiles.DeclDefs.WithTypeParams.OneWithUpperBound

trait Sample {
  def foo[T <: String](): Unit
}