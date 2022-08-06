package effiban.scala2java.transformers

import scala.meta.Term

trait ScalaToJavaTermApplyTransformer {
  def transform(termApply: Term.Apply): Term.Apply
}

object ScalaToJavaTermApplyTransformer extends ScalaToJavaTermApplyTransformer {

  // Transform any Scala-specific method invocations into an equivalent in Java
  override def transform(termApply: Term.Apply): Term.Apply = {
    termApply match {
      case Term.Apply(Term.Name("Range"), args) => Term.Apply(Term.Select(Term.Name("IntStream"), Term.Name("range")), args)
      case other => other
    }
  }
}
