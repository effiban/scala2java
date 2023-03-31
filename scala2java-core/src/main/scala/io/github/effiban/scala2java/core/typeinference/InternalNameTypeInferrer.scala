package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.predicates.TermNameSupportsNoArgInvocation
import io.github.effiban.scala2java.spi.typeinferrers.NameTypeInferrer

import scala.meta.{Term, Type}


trait InternalNameTypeInferrer {
  def infer(termName: Term.Name): Option[Type]
}

private[typeinference] class InternalNameTypeInferrerImpl(applyReturnTypeInferrer: => ApplyReturnTypeInferrer,
                                                          nameTypeInferrer: => NameTypeInferrer,
                                                          termNameSupportsNoArgInvocation: TermNameSupportsNoArgInvocation)
  extends InternalNameTypeInferrer {

  override def infer(termName: Term.Name): Option[Type] = {
    if (termNameSupportsNoArgInvocation(termName)) {
      applyReturnTypeInferrer.infer(Term.Apply(termName, Nil))
    } else {
      nameTypeInferrer.infer(termName)
    }
  }
}
