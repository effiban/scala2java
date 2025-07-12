package io.github.effiban.scala2java.core.typeinference


import io.github.effiban.scala2java.core.entities.Regexes.ScalaTupleElementRegex
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation
import io.github.effiban.scala2java.spi.typeinferrers.{SelectTypeInferrer, TypeInferrer0}

import scala.meta.{Term, Type}

trait InternalSelectTypeInferrer extends TypeInferrer0[Term.Select]

private[typeinference] class InternalSelectTypeInferrerImpl(applyReturnTypeInferrer: => ApplyReturnTypeInferrer,
                                                            qualifierTypeInferrer: => QualifierTypeInferrer,
                                                            selectTypeInferrer: => SelectTypeInferrer,
                                                            termSelectSupportsNoArgInvocation: TermSelectSupportsNoArgInvocation,
                                                            scalaReflectionTypeInferrer: ScalaReflectionTypeInferrer)
  extends InternalSelectTypeInferrer {

  override def infer(termSelect: Term.Select): Option[Type] = {
    val maybeQualType = qualifierTypeInferrer.infer(termSelect)
    val inferenceContext = TermSelectInferenceContext(maybeQualType)
    if (termSelectSupportsNoArgInvocation(termSelect, inferenceContext)) {
      applyReturnTypeInferrer.infer(Term.Apply(termSelect, Nil))
    } else {
      selectTypeInferrer.infer(termSelect, inferenceContext)
        .orElse(inferByReflection(termSelect, inferenceContext))
        .orElse(inferSpecialCase(termSelect, inferenceContext))
    }
  }

  private def inferByReflection(termSelect: Term.Select, inferenceContext: TermSelectInferenceContext) = {
    import scalaReflectionTypeInferrer._

    (termSelect.qual, inferenceContext.maybeQualType, termSelect.name) match {
      case (qual: Term.Ref, None, name) => inferScalaMetaTypeOf(qual, name)
      case (_, Some(qualType: Type.Ref), name) => inferScalaMetaTypeOf(qualType, name)
      case (_, Some(Type.Apply(qualType: Type.Ref, args)), name) => inferScalaMetaTypeOf(qualType, args, name)
      case _ => None
    }
  }

  private def inferSpecialCase(termSelect: Term.Select, inferenceContext: TermSelectInferenceContext) = {
    (termSelect.qual, inferenceContext.maybeQualType, termSelect.name) match {
      case (_,  Some(Type.Tuple(typeArgs)), Term.Name(ScalaTupleElementRegex(indexStr))) => Some(typeArgs.apply(indexStr.toInt - 1))
      case _ => None
    }
  }
}