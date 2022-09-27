package effiban.scala2java.transformers

import effiban.scala2java.classifiers.TermApplyClassifier

import scala.annotation.tailrec
import scala.meta.Term

trait ScalaToJavaTermApplyTransformer {
  def transform(termApply: Term.Apply): Term.Apply
}

class ScalaToJavaTermApplyTransformerImpl(termApplyClassifier: TermApplyClassifier,
                                          collectionInitializerTransformer: ScalaToJavaCollectionInitializerTransformer)
  extends ScalaToJavaTermApplyTransformer {

  // Transform any method invocations which have a Scala-specific naming or style into Java equivalents
  @tailrec
  override final def transform(termApply: Term.Apply): Term.Apply = {
    termApply match {
      case Term.Apply(Term.Name("Range"), args) => Term.Apply(Term.Select(Term.Name("IntStream"), Term.Name("range")), args)
      case aTermApply if termApplyClassifier.isCollectionInitializer(aTermApply) => collectionInitializerTransformer.transform(aTermApply)
      // Invocation of method with more than one param list
      case Term.Apply(Term.Apply(fun, args1), args2) => transform(Term.Apply(fun, args1 ++ args2))
      case other => other
    }
  }
}

object ScalaToJavaTermApplyTransformer extends ScalaToJavaTermApplyTransformerImpl(
  TermApplyClassifier,
  ScalaToJavaCollectionInitializerTransformer
)
