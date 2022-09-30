package effiban.scala2java.typeinference

import effiban.scala2java.entities.{TermNameValues, TypeNameValues}

import scala.meta.{Term, Type}

trait NameTypeInferrer extends TypeInferrer[Term.Name]

object NameTypeInferrer extends NameTypeInferrer {

  override def infer(termName: Term.Name): Option[Type] = {
    termName match {
      case Term.Name(TermNameValues.ScalaOption) => Some(Type.Name(TypeNameValues.ScalaOption))
      case Term.Name(TermNameValues.ScalaSome) => Some(Type.Name(TypeNameValues.ScalaOption))
      case Term.Name(TermNameValues.ScalaNone) => Some(Type.Name(TypeNameValues.ScalaOption))

      case Term.Name(TermNameValues.ScalaRight) => Some(Type.Name(TypeNameValues.Either))
      case Term.Name(TermNameValues.ScalaLeft) => Some(Type.Name(TypeNameValues.Either))

      case Term.Name(TermNameValues.Try) => Some(Type.Name(TypeNameValues.Try))
      case Term.Name(TermNameValues.ScalaSuccess) => Some(Type.Name(TypeNameValues.Try))
      case Term.Name(TermNameValues.ScalaFailure) => Some(Type.Name(TypeNameValues.Try))

      case Term.Name(TermNameValues.Future) => Some(Type.Name(TypeNameValues.Future))

      case Term.Name(TermNameValues.Stream) => Some(Type.Name(TypeNameValues.Stream))
      case Term.Name(TermNameValues.ScalaArray) => Some(Type.Name(TypeNameValues.ScalaArray))
      case Term.Name(TermNameValues.List) => Some(Type.Name(TypeNameValues.List))
      case Term.Name(TermNameValues.ScalaVector) => Some(Type.Name(TypeNameValues.ScalaVector))
      case Term.Name(TermNameValues.Seq) => Some(Type.Name(TypeNameValues.Seq))
      case Term.Name(TermNameValues.Set) => Some(Type.Name(TypeNameValues.Set))
      case Term.Name(TermNameValues.Map) => Some(Type.Name(TypeNameValues.Map))

      case _ => None
    }
  }
}
