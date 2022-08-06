package effiban.scala2java.transformers

import scala.meta.Term

trait ScalaToJavaTermSelectTransformer {
  def transform(termSelect: Term.Select): Term.Select
}

object ScalaToJavaTermSelectTransformer extends ScalaToJavaTermSelectTransformer {

  private final val ScalaRange = "Range"
  private final val IntStream = "IntStream"

  // Transform a Scala-specific qualified name into an equivalent in Java
  override def transform(termSelect: Term.Select): Term.Select = {
    termSelect match {
      case Term.Select(Term.Name(ScalaRange), Term.Name("inclusive")) => Term.Select(Term.Name(IntStream), Term.Name("rangeClosed"))
      case Term.Select(Term.Name(ScalaRange), Term.Name("apply")) => Term.Select(Term.Name(IntStream), Term.Name("range"))
      case other => other
    }
  }
}
