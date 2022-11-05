package io.github.effiban.scala2java.core.typeinference

import scala.meta.Type

trait CollectiveTypeInferrer {

  def inferScalar(maybeTypes: List[Option[Type]]): Option[Type]
  def inferTuple(typeTuples: List[Type.Tuple]): Type.Tuple
}

private[typeinference] object CollectiveTypeInferrer extends CollectiveTypeInferrer {

  override def inferScalar(maybeTypes: List[Option[Type]]): Option[Type] = {
    val filteredMaybeTypes = maybeTypes
      .filterNot(_.exists(_.structure == Type.AnonymousName().structure))

    filteredMaybeTypes match {
      // None remaining means no appropriate type - so return the anonymous type (which is different than 'unknown type')
      case Nil => Some(Type.AnonymousName())
      case theMaybeTypes if theMaybeTypes.forall(_.structure == theMaybeTypes.head.structure) => theMaybeTypes.head
      // TODO can be improved by finding a common type
      case _ => None
    }
  }

  override def inferTuple(typeTuples: List[Type.Tuple]): Type.Tuple = {
    typeTuples match {
      case theTupleTypes if !allHaveSameLength(theTupleTypes) => throw new IllegalStateException("Cannot infer collective tuple type - tuples have different lengths")
      case theTupleTypes =>
        val collectiveTypes = theTupleTypes.head.args.indices
          .map(typeIndex => inferTypeAtIndex(theTupleTypes, typeIndex))
          .toList
        Type.Tuple(collectiveTypes)
    }
  }

  private def allHaveSameLength(theTupleTypes: List[Type.Tuple]) = {
    theTupleTypes.forall(_.args.length == theTupleTypes.head.args.length)
  }

  private def inferTypeAtIndex(theTupleTypes: List[Type.Tuple], typeIndex: Int) = {
    val typesAtIndex = theTupleTypes.map(_.args(typeIndex))
    // TODO the 'else' case can be improved by finding a common type
    if (typesAtIndex.forall(_.structure == typesAtIndex.head.structure)) typesAtIndex.head else Type.Name("Any")
  }
}
