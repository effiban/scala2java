package effiban.scala2java.transformers

import scala.meta.Term

trait TermSelectTransformer {
  def transform(termSelect: Term.Select): Term.Select
}

object TermSelectTransformer extends TermSelectTransformer {

  private final val ScalaRange = "Range"
  private final val IntStream = "IntStream"
  private final val Future = "Future"
  private final val CompletableFuture = "CompletableFuture"

  // Transform a Scala-specific qualified name into an equivalent in Java
  override def transform(termSelect: Term.Select): Term.Select = {
    termSelect match {
      case Term.Select(Term.Name(ScalaRange), Term.Name("inclusive")) => Term.Select(Term.Name(IntStream), Term.Name("rangeClosed"))
      case Term.Select(Term.Name(ScalaRange), Term.Name("apply")) => Term.Select(Term.Name(IntStream), Term.Name("range"))

      case Term.Select(Term.Name(Future), Term.Name("successful")) => Term.Select(Term.Name(CompletableFuture), Term.Name("completedFuture"))
      case Term.Select(Term.Name(Future), Term.Name("failed")) => Term.Select(Term.Name(CompletableFuture), Term.Name("failedFuture"))

      case other => other
    }
  }
}
