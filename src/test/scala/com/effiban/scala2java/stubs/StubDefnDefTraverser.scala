package com.effiban.scala2java.stubs

import com.effiban.scala2java.{DefnDefTraverser, JavaEmitter}

import scala.meta.{Defn, Init}

class StubDefnDefTraverser(implicit javaEmitter: JavaEmitter) extends DefnDefTraverser {
  import javaEmitter._

  override def traverse(defDefn: Defn.Def, maybeInit: Option[Init]): Unit = {
    val initStr = maybeInit.map(init => s"\nInput Init: $init").getOrElse("")
    emitComment(
      s"""STUB METHOD$initStr
         |Scala Body:
         |$defDefn""".stripMargin
    )
  }
}
