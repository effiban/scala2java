package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extractors.TypeRefNameExtractor
import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.{Name, Term, Type}

trait SuperTypeInferrer extends TypeInferrer0[Term.Super]

private[typeinference] class SuperTypeInferrerImpl(innermostEnclosingTemplateAncestorsInferrer: InnermostEnclosingTemplateAncestorsInferrer)
  extends SuperTypeInferrer {

  override def infer(termSuper: Term.Super): Option[Type.Ref] = {
    val parentTypes = termSuper.thisp match {
      case Name.Anonymous() => innermostEnclosingTemplateAncestorsInferrer.infer(termSuper)
      case _ => innermostEnclosingTemplateAncestorsInferrer.infer(termSuper, Some(termSuper.thisp.value))
    }
    parentTypes.find(tpe => termSuper.superp match {
      case Name.Anonymous() => true
      case superp => TypeRefNameExtractor.extract(tpe).value == superp.value
    })
  }
}

object SuperTypeInferrer extends SuperTypeInferrerImpl(InnermostEnclosingTemplateAncestorsInferrer)