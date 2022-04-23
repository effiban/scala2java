package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Lit

object LitTraverser extends ScalaTreeTraverser[Lit] {

  // Literals in the code
  def traverse(lit: Lit): Unit = {
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
    // TODO - escape properly
    str.replace("\n", "\\n")
  }

  private def quoteString(str: String) = {
    s"\"$str\""
  }
}
