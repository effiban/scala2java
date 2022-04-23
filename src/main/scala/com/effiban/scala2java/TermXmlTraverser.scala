package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Term

object TermXmlTraverser extends ScalaTreeTraverser[Term.Xml] {

  override def traverse(termXml: Term.Xml): Unit = {
    // TODO
    emitComment(termXml.toString())
  }
}
