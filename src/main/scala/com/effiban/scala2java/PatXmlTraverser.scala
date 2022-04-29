package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat

trait PatXmlTraverser extends ScalaTreeTraverser[Pat.Xml]

object PatXmlTraverser extends PatXmlTraverser {

  // Pattern match xml
  override def traverse(patternXml: Pat.Xml): Unit = {
    // TODO
    emitComment(patternXml.toString())
  }
}
