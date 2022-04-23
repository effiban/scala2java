package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Lit

object LitTraverser extends ScalaTreeTraverser[Lit] {

  // Literals in the code
  def traverse(lit: Lit): Unit = {
    val strValue = lit.value match {
      case str: Lit.String => s"\"$str\""
      case Lit.Unit => ""
      case other => other.toString
    }
    emit(strValue)
  }
}
