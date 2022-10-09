package io.github.effiban.scala2java.contexts

import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.Tree

case class JavaChildScopeContext(scalaTree: Tree, javaTreeType: JavaTreeType)
