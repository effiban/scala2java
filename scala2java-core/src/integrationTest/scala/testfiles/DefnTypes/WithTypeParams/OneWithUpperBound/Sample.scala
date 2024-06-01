package testfiles.DefnTypes.WithTypeParams.OneWithUpperBound

class Sample {
  type MyType[T <: String] = List[T]
}
