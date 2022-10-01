package effiban.scala2java.traversers

import effiban.scala2java.contexts.{ArrayInitializerSizeContext, ArrayInitializerValuesContext}
import effiban.scala2java.entities.EnclosingDelimiter.{CurlyBrace, SquareBracket}
import effiban.scala2java.entities.{JavaKeyword, ListTraversalOptions, TypeNameValues}
import effiban.scala2java.typeinference.ScalarArgListTypeInferrer
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Term, Type}

trait ArrayInitializerTraverser {
  def traverseWithValues(context: ArrayInitializerValuesContext): Unit

  def traverseWithSize(context: ArrayInitializerSizeContext): Unit
}

private[traversers] class ArrayInitializerTraverserImpl(typeTraverser: => TypeTraverser,
                                                        termTraverser: => TermTraverser,
                                                        argumentListTraverser: => ArgumentListTraverser,
                                                        scalarArgListTypeInferrer: ScalarArgListTypeInferrer)
                                                       (implicit javaWriter: JavaWriter) extends ArrayInitializerTraverser {

  import javaWriter._

  override def traverseWithValues(context: ArrayInitializerValuesContext): Unit = {
    import context._

    val tpe = resolveTypeWithValues(maybeType, values)
    writeKeyword(JavaKeyword.New)
    write(" ")
    typeTraverser.traverse(tpe)
    writeStartDelimiter(SquareBracket)
    writeEndDelimiter(SquareBracket)
    write(" ")
    val options = ListTraversalOptions(maybeEnclosingDelimiter = Some(CurlyBrace), traverseEmpty = true)
    argumentListTraverser.traverse(args = values, argTraverser = termTraverser, options = options)
  }

  override def traverseWithSize(context: ArrayInitializerSizeContext): Unit = {
    import context._

    writeKeyword(JavaKeyword.New)
    write(" ")
    typeTraverser.traverse(tpe)
    writeStartDelimiter(SquareBracket)
    termTraverser.traverse(size)
    writeEndDelimiter(SquareBracket)
  }

  private def resolveTypeWithValues(maybeType: Option[Type] = None, values: List[Term] = Nil) = {
    (maybeType, values) match {
      case (Some(tpe), _) => tpe
      case (None, Nil) => Type.Name(TypeNameValues.ScalaAny)
      case (None, values) => scalarArgListTypeInferrer.infer(values)
    }
  }
}
