package io.github.effiban.scala2java.contexts

import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Mod, Tree}

case class JavaModifiersContext(scalaTree: Tree,
                                scalaMods: List[Mod],
                                javaTreeType: JavaTreeType,
                                javaScope: JavaScope)
