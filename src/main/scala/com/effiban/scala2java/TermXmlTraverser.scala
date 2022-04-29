package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Term

trait TermXmlTraverser extends ScalaTreeTraverser[Term.Xml]

object TermXmlTraverser extends TermXmlTraverser {

  override def traverse(termXml: Term.Xml): Unit = {
    // TODO
    emitComment(termXml.toString())
  }
}
