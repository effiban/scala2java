package effiban.scala2java.typeinference

import scala.meta.Type

trait CollectiveTypeInferrer extends TypeInferrer[List[Option[Type]]]

private[typeinference] object CollectiveTypeInferrer extends CollectiveTypeInferrer {

  override def infer(maybeTypes: List[Option[Type]]): Option[Type] = {
    val filteredMaybeTypes = maybeTypes
      .filterNot(_.exists(_.structure == Type.AnonymousName().structure))

    filteredMaybeTypes match {
      // None remaining means no appropriate type - so return the anonymous type (which is different than 'unknown type')
      case Nil => Some(Type.AnonymousName())
      // If all are the same - we have a common type we can return
      case theMaybeTypes if theMaybeTypes.forall(_ == theMaybeTypes.head) => theMaybeTypes.head
      // TODO can be improved by finding a common type
      case _ => None
    }
  }
}
