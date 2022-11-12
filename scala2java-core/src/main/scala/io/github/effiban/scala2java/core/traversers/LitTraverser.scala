package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Lit

trait LitTraverser extends ScalaTreeTraverser[Lit]

class LitTraverserImpl(implicit javaWriter: JavaWriter) extends LitTraverser {

  import javaWriter._

  // Literals in the code
  override def traverse(lit: Lit): Unit = {
    val strValue = lit match {
      // TODO - handle case of multi-line strings, they are supported in Java 17
      case str: Lit.String => fixString(str.value)
      case Lit.Symbol(sym) => fixString(sym.name)
      case _: Lit.Unit => ""
      case _: Lit.Null => "null"
      case other => other.value.toString
    }
    write(strValue)
  }

  private def fixString(str: String) = {
    quoteString(escapeString(str))
  }

  private def escapeString(str: String) = {
    //TODO - escape all other special chars
    str.replace("\n", "\\n")
  }

  private def quoteString(str: String) = s"\"$str\""
}