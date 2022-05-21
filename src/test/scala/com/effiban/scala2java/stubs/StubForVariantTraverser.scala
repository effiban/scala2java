package com.effiban.scala2java.stubs

import com.effiban.scala2java.{ForVariantTraverser, JavaEmitter}

import scala.meta.{Enumerator, Term}

class StubForVariantTraverser(implicit javaEmitter: JavaEmitter) extends ForVariantTraverser {

  import javaEmitter._

  override def traverse(enumerators: List[Enumerator],
                        body: Term,
                        finalFunctionName: Term.Name): Unit = {
    emitComment(
      s"""STUB 'FOR':
         |Enumerators: $enumerators
         |Body: $body
         |Final Function Name: "$finalFunctionName"
         |""".stripMargin)
  }
}
