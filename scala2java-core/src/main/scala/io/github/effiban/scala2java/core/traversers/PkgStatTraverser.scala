package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ClassOrTraitContext, StatContext}
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.traversers.results.StatTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Defn, Member, Name, Stat}

trait PkgStatTraverser {
  def traverse(stat: Stat, sealedHierarchies: SealedHierarchies): StatTraversalResult
}

private[traversers] class PkgStatTraverserImpl(classTraverser: => ClassTraverser,
                                               traitTraverser: => TraitTraverser,
                                               objectTraverser: => ObjectTraverser,
                                               defaultStatTraverser: => DefaultStatTraverser) extends PkgStatTraverser {

  override def traverse(stat: Stat, sealedHierarchies: SealedHierarchies): StatTraversalResult = {
    stat match {
      case `class`: Defn.Class => classTraverser.traverse(`class`, generateClassOrTraitContext(`class`, sealedHierarchies))
      case `trait`: Defn.Trait => traitTraverser.traverse(`trait`, generateClassOrTraitContext(`trait`, sealedHierarchies))
      case `object`: Defn.Object => objectTraverser.traverse(`object`, StatContext(resolveJavaScope(`object`.name, sealedHierarchies)))
      case stat => defaultStatTraverser.traverse(stat, StatContext(JavaScope.Package))
    }
  }

  private def generateClassOrTraitContext(memberType: Member.Type, sealedHierarchies: SealedHierarchies) = {
    val javaScope = resolveJavaScope(memberType.name, sealedHierarchies)
    ClassOrTraitContext(javaScope = javaScope)
  }

  private def resolveJavaScope(name: Name, sealedHierarchies: SealedHierarchies) = {
    if (sealedHierarchies.isSubType(name)) JavaScope.Sealed else JavaScope.Package
  }
}
