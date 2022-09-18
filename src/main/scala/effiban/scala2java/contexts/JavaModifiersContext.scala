package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Mod, Tree}

case class JavaModifiersContext(scalaTree: Tree,
                                scalaMods: List[Mod],
                                javaTreeType: JavaTreeType,
                                javaScope: JavaScope)
