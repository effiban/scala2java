package effiban.scala2java.typeinference

import scala.meta.{Term, Type}

trait NameTypeInferrer extends TypeInferrer[Term.Name]

object NameTypeInferrer extends NameTypeInferrer {

  private val OptionTypeName = Type.Name("Option")
  private val EitherTypeName = Type.Name("Either")

  override def infer(termName: Term.Name): Option[Type] = {
    termName match {
      case Term.Name("Option") => Some(OptionTypeName)
      case Term.Name("Some") => Some(OptionTypeName)
      case Term.Name("None") => Some(OptionTypeName)

      case Term.Name("Right") => Some(EitherTypeName)
      case Term.Name("Left") => Some(EitherTypeName)

      case Term.Name("Future") => Some(Type.Name("Future"))

      case Term.Name("Stream") => Some(Type.Name("Stream"))
      case Term.Name("Array") => Some(Type.Name("Array"))
      case Term.Name("List") => Some(Type.Name("List"))
      case Term.Name("Vector") => Some(Type.Name("Vector"))
      case Term.Name("Seq") => Some(Type.Name("Seq"))
      case Term.Name("Set") => Some(Type.Name("Set"))
      case Term.Name("Map") => Some(Type.Name("Map"))

      case _ => None
    }
  }
}
