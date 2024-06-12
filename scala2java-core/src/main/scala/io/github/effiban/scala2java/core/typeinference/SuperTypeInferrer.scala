package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.{baseClassesOf, classSymbolOf}
import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.{Name, Term, Type, XtensionParseInputLike}

trait SuperTypeInferrer extends TypeInferrer0[Term.Super]

private[typeinference] class SuperTypeInferrerImpl(innermostEnclosingMemberPathInferrer: InnermostEnclosingMemberPathInferrer)
  extends SuperTypeInferrer {

  override def infer(termSuper: Term.Super): Option[Type] = {
    val innermostEnclosingMemberPath = termSuper.thisp match {
      case Name.Anonymous() => innermostEnclosingMemberPathInferrer.infer(termSuper)
      case _ => innermostEnclosingMemberPathInferrer.infer(termSuper, Some(termSuper.thisp.value))
    }
    classSymbolOf(innermostEnclosingMemberPath)
      // NOTE: baseClassesOf will return all the base classes recursively, while for our purposes here only the direct ones are relevant -
      // but I don't know how to select them only
      .map(baseClassesOf)
      .getOrElse(Nil)
      .find(cls => termSuper.superp match {
        case Name.Anonymous() => true
        case superp => cls.name.toString == superp.value
      })
      .map(cls => cls.fullName.parse[Type])
      .flatMap(_.toOption)
  }
}

object SuperTypeInferrer extends SuperTypeInferrerImpl(InnermostEnclosingMemberPathInferrer)