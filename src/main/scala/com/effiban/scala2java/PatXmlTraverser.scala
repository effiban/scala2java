package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat

object PatXmlTraverser extends ScalaTreeTraverser[Pat.Xml] {

  // Pattern match xml
  def traverse(patternXml: Pat.Xml): Unit = {
    // TODO
    emitComment(patternXml.toString())
  }
}
