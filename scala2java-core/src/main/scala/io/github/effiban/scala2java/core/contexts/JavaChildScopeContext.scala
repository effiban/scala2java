package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType

import scala.meta.Tree

case class JavaChildScopeContext(scalaTree: Tree, javaTreeType: JavaTreeType)
