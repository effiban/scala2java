package effiban.scala2java.typeinference

import effiban.scala2java.entities.{TermNameValues, TypeNameValues}

import scala.meta.{Term, Type}

trait SelectTypeInferrer extends TypeInferrer[Term.Select]

object SelectTypeInferrer extends SelectTypeInferrer {

  override def infer(termSelect: Term.Select): Option[Type] = termSelect match {
    case Term.Select(Term.Name(TermNameValues.Future), Term.Name(TermNameValues.ScalaSuccessful) | Term.Name(TermNameValues.ScalaFailed)) =>
      Some(Type.Name(TypeNameValues.Future))
    case _ => None
  }
}