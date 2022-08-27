package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Mod, Tree}

case class JavaModifiersResolverParams(scalaTree: Tree,
                                       scalaMods: List[Mod],
                                       javaTreeType: JavaTreeType,
                                       javaScope: JavaTreeType)

