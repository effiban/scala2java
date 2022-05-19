package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, TypeParamListTraverser}

import scala.meta.Type

class StubTypeParamListTraverser(implicit javaEmitter: JavaEmitter) extends TypeParamListTraverser {
  import javaEmitter._

  override def traverse(typeParams: List[Type.Param]): Unit = emit(s"<${typeParams.mkString(", ")}>")
}
