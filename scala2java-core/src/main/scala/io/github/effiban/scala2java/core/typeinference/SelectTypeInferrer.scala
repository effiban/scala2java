package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TermNameValues.{Empty, ScalaInclusive}
import io.github.effiban.scala2java.core.entities.{TermNameValues, TypeNameValues}
import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

trait SelectTypeInferrer extends TypeInferrer0[Term.Select]

object SelectTypeInferrer extends SelectTypeInferrer {

  override def infer(termSelect: Term.Select): Option[Type] = {
    (termSelect.qual, termSelect.name) match {
      case (Term.Name(TermNameValues.ScalaRange), Term.Name(ScalaInclusive)) => Some(Type.Name(TypeNameValues.ScalaRange))
      case (Term.Name(TermNameValues.ScalaOption), Term.Name(Empty)) => Some(Type.Name(TypeNameValues.ScalaOption))
      case (Term.Name(TermNameValues.Future), Term.Name(TermNameValues.ScalaSuccessful) | Term.Name(TermNameValues.ScalaFailed)) =>
        Some(Type.Name(TypeNameValues.Future))
      case (Term.Name(TermNameValues.Stream), Term.Name(Empty)) => Some(Type.Name(TypeNameValues.Stream))
      case (Term.Name(TermNameValues.Seq), Term.Name(Empty)) => Some(Type.Name(TypeNameValues.Seq))
      case (Term.Name(TermNameValues.List), Term.Name(Empty)) => Some(Type.Name(TypeNameValues.List))
      case (Term.Name(TermNameValues.ScalaVector), Term.Name(Empty)) => Some(Type.Name(TypeNameValues.ScalaVector))
      case (Term.Name(TermNameValues.Set), Term.Name(Empty)) => Some(Type.Name(TypeNameValues.Set))
      case (Term.Name(TermNameValues.Map), Term.Name(Empty)) => Some(Type.Name(TypeNameValues.Map))
      case (_, q"toString") => Some(t"String")
      case _ => None
    }
  }
}