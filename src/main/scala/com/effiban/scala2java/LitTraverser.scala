package com.effiban.scala2java

import scala.meta.Lit

trait LitTraverser extends ScalaTreeTraverser[Lit]

class LitTraverserImpl(javaEmitter: JavaEmitter) extends LitTraverser {

  import javaEmitter._

  // Literals in the code
  override def traverse(lit: Lit): Unit = {
    val strValue = lit match {
      case str: Lit.String => fixString(str.value)
      case _: Lit.Unit => ""
      case other => other.value.toString
    }
    emit(strValue)
  }

  private def fixString(str: String) = {
    quoteString(escapeString(str))
  }

  private def escapeString(str: String) = {
    //TODO - escape properly
    str.replace("\n", "\\n")
  }

  private def quoteString(str: String) = {
    s"\"$str\""
  }
}

object LitTraverser extends LitTraverserImpl(JavaEmitter)
