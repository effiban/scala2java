package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, ArrayInitializerSizeContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.{CurlyBrace, SquareBracket}
import io.github.effiban.scala2java.core.entities.JavaKeyword.New
import io.github.effiban.scala2java.core.entities.TypeNameValues.ScalaAny
import io.github.effiban.scala2java.core.entities.{JavaKeyword, ListTraversalOptions}
import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.typeinference.{CompositeCollectiveTypeInferrer, TermTypeInferrer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Term, Type}

@deprecated
trait DeprecatedArrayInitializerTraverser {
  def traverseWithValues(context: ArrayInitializerValuesContext): Unit

  def traverseWithSize(context: ArrayInitializerSizeContext): Unit
}

@deprecated
private[traversers] class DeprecatedArrayInitializerTraverserImpl(typeTraverser: => TypeTraverser,
                                                                  typeRenderer: => TypeRenderer,
                                                                  expressionTermTraverser: => DeprecatedExpressionTermTraverser,
                                                                  termArgumentTraverser: => DeprecatedArgumentTraverser[Term],
                                                                  argumentListTraverser: => DeprecatedArgumentListTraverser,
                                                                  termTypeInferrer: => TermTypeInferrer,
                                                                  compositeCollectiveTypeInferrer: => CompositeCollectiveTypeInferrer)
                                                                 (implicit javaWriter: JavaWriter) extends DeprecatedArrayInitializerTraverser {

  import javaWriter._

  override def traverseWithValues(context: ArrayInitializerValuesContext): Unit = {
    import context._

    val tpe = resolveTypeWithValues(maybeType, values)
    writeKeyword(New)
    write(" ")
    val traversedType = typeTraverser.traverse(tpe)
    typeRenderer.render(traversedType)
    writeStartDelimiter(SquareBracket)
    writeEndDelimiter(SquareBracket)
    write(" ")
    val options = ListTraversalOptions(maybeEnclosingDelimiter = Some(CurlyBrace), traverseEmpty = true)
    argumentListTraverser.traverse(
      args = values,
      argTraverser = termArgumentTraverser,
      context = ArgumentListContext(options = options))
  }

  override def traverseWithSize(context: ArrayInitializerSizeContext): Unit = {
    import context._

    writeKeyword(JavaKeyword.New)
    write(" ")
    val traversedType = typeTraverser.traverse(tpe)
    typeRenderer.render(traversedType)
    writeStartDelimiter(SquareBracket)
    expressionTermTraverser.traverse(size)
    writeEndDelimiter(SquareBracket)
  }

  private def resolveTypeWithValues(maybeType: Option[Type] = None, values: List[Term] = Nil) = {
    (maybeType, values) match {
      case (Some(tpe), _) => tpe
      case (None, Nil) => Type.Name(ScalaAny)
      case (None, values) => compositeCollectiveTypeInferrer.infer(values.map(termTypeInferrer.infer))
    }
  }
}
