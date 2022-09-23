package effiban.scala2java.traversers

import effiban.scala2java.contexts.{ClassOrTraitContext, StatContext}
import effiban.scala2java.entities.{JavaScope, SealedHierarchies}
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Defn, Member, Name, Stat}

trait PkgStatTraverser {
  def traverse(stat: Stat, sealedHierarchies: SealedHierarchies): Unit
}

private[traversers] class PkgStatTraverserImpl(classTraverser: => ClassTraverser,
                                               traitTraverser: => TraitTraverser,
                                               objectTraverser: => ObjectTraverser,
                                               statTraverser: => StatTraverser)
                                              (implicit javaWriter: JavaWriter) extends PkgStatTraverser {

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
