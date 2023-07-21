package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ClassOrTraitContext, StatContext}
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Defn, Member, Name, Stat}

@deprecated
trait DeprecatedPkgStatTraverser {
  def traverse(stat: Stat, sealedHierarchies: SealedHierarchies): Unit
}

@deprecated
private[traversers] class DeprecatedPkgStatTraverserImpl(classTraverser: => DeprecatedClassTraverser,
                                                         traitTraverser: => DeprecatedTraitTraverser,
                                                         objectTraverser: => DeprecatedObjectTraverser,
                                                         statTraverser: => DeprecatedStatTraverser) extends DeprecatedPkgStatTraverser {

  override def traverse(stat: Stat, sealedHierarchies: SealedHierarchies): Unit = {
    stat match {
      case `class`: Defn.Class => classTraverser.traverse(`class`, generateClassOrTraitContext(`class`, sealedHierarchies))
      case `trait`: Defn.Trait => traitTraverser.traverse(`trait`, generateClassOrTraitContext(`trait`, sealedHierarchies))
      case `object`: Defn.Object => objectTraverser.traverse(`object`, StatContext(resolveJavaScope(`object`.name, sealedHierarchies)))
      case stat => statTraverser.traverse(stat, StatContext(JavaScope.Package))
    }
  }

  private def generateClassOrTraitContext(memberType: Member.Type, sealedHierarchies: SealedHierarchies) = {
    val javaScope = resolveJavaScope(memberType.name, sealedHierarchies)
    ClassOrTraitContext(javaScope = javaScope, permittedSubTypeNames = sealedHierarchies.getSubTypeNames(memberType.name))
  }

  private def resolveJavaScope(name: Name, sealedHierarchies: SealedHierarchies) = {
    if (sealedHierarchies.isSubType(name)) JavaScope.Sealed else JavaScope.Package
  }
}
