package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerSizeContext, ArrayInitializerTypedValuesContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaAny
import io.github.effiban.scala2java.core.typeinference.{CompositeCollectiveTypeInferrer, TermTypeInferrer}

import scala.meta.{Term, Type}

trait ArrayInitializerTraverser {
  def traverseWithValues(context: ArrayInitializerValuesContext): ArrayInitializerTypedValuesContext

  def traverseWithSize(context: ArrayInitializerSizeContext): ArrayInitializerSizeContext
}

private[traversers] class ArrayInitializerTraverserImpl(typeTraverser: => TypeTraverser,
                                                        expressionTermTraverser: => ExpressionTermTraverser,
                                                        termTypeInferrer: => TermTypeInferrer,
                                                        compositeCollectiveTypeInferrer: => CompositeCollectiveTypeInferrer)
  extends ArrayInitializerTraverser {

  override def traverseWithValues(context: ArrayInitializerValuesContext): ArrayInitializerTypedValuesContext = {
    import context._

    val tpe = resolveTypeWithValues(maybeType, values)
    val traversedType = typeTraverser.traverse(tpe)
    val traversedValues = values.map(expressionTermTraverser.traverse)
    ArrayInitializerTypedValuesContext(tpe = traversedType, values = traversedValues)
  }

  override def traverseWithSize(context: ArrayInitializerSizeContext): ArrayInitializerSizeContext = {
    import context._

    val traversedType = typeTraverser.traverse(tpe)
    val traversedSize = expressionTermTraverser.traverse(size)
    ArrayInitializerSizeContext(traversedType, traversedSize)
  }

  private def resolveTypeWithValues(maybeType: Option[Type] = None, values: List[Term] = Nil) = {
    (maybeType, values) match {
      case (Some(tpe), _) => tpe
      case (None, Nil) => ScalaAny
      case (None, values) => compositeCollectiveTypeInferrer.infer(values.map(termTypeInferrer.infer))
    }
  }
}
