package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, ArrayInitializerSizeContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.{CurlyBrace, SquareBracket}
import io.github.effiban.scala2java.core.entities.JavaKeyword.New
import io.github.effiban.scala2java.core.entities.{JavaKeyword, ListTraversalOptions, TypeNameValues}
import io.github.effiban.scala2java.core.typeinference.{CompositeCollectiveTypeInferrer, TermTypeInferrer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Term, Type}

trait ArrayInitializerTraverser {
  def traverseWithValues(context: ArrayInitializerValuesContext): Unit

  def traverseWithSize(context: ArrayInitializerSizeContext): Unit
}

private[traversers] class ArrayInitializerTraverserImpl(typeTraverser: => TypeTraverser,
                                                        expressionTermTraverser: => TermTraverser,
                                                        termArgumentTraverser: => ArgumentTraverser[Term],
                                                        argumentListTraverser: => ArgumentListTraverser,
                                                        termTypeInferrer: => TermTypeInferrer,
                                                        compositeCollectiveTypeInferrer: => CompositeCollectiveTypeInferrer)
                                                       (implicit javaWriter: JavaWriter) extends ArrayInitializerTraverser {

  import javaWriter._

  override def traverseWithValues(context: ArrayInitializerValuesContext): Unit = {
    import context._

    val tpe = resolveTypeWithValues(maybeType, values)
    writeKeyword(New)
    write(" ")
    typeTraverser.traverse(tpe)
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
    typeTraverser.traverse(tpe)
    writeStartDelimiter(SquareBracket)
    expressionTermTraverser.traverse(size)
    writeEndDelimiter(SquareBracket)
  }

  private def resolveTypeWithValues(maybeType: Option[Type] = None, values: List[Term] = Nil) = {
    (maybeType, values) match {
      case (Some(tpe), _) => tpe
      case (None, Nil) => Type.Name(TypeNameValues.ScalaAny)
      case (None, values) => compositeCollectiveTypeInferrer.infer(values.map(termTypeInferrer.infer))
    }
  }
}
