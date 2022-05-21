package com.effiban.scala2java.stubs

import com.effiban.scala2java.{ImporterTraverser, JavaEmitter}

import scala.meta.Importer

class StubImporterTraverser(implicit javaEmitter: JavaEmitter) extends ImporterTraverser {

  import javaEmitter._

  override def traverse(importer: Importer): Unit = {
    emit(s"import ${importer.toString()}")
    emitStatementEnd()
  }
}
