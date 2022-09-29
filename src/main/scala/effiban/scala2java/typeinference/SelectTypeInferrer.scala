package effiban.scala2java.typeinference

import scala.meta.{Term, Type}

trait SelectTypeInferrer extends TypeInferrer[Term.Select]

object SelectTypeInferrer extends SelectTypeInferrer {

  private final val Future = "Future"

  override def infer(termSelect: Term.Select): Option[Type] = termSelect match {
    case Term.Select(Term.Name(Future), Term.Name("successful") | Term.Name("failed")) => Some(Type.Name(Future))
    case _ => None
  }
}