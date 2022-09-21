package effiban.scala2java.traversers

import effiban.scala2java.contexts.ClassOrTraitContext

import scala.meta.{Defn, Mod}

trait ClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

private[traversers] class ClassTraverserImpl(caseClassTraverser: => CaseClassTraverser,
                                             regularClassTraverser: => RegularClassTraverser) extends ClassTraverser {

  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    if (classDef.mods.exists(_.isInstanceOf[Mod.Case])) {
      caseClassTraverser.traverse(classDef, context)
    } else {
      regularClassTraverser.traverse(classDef, context)
    }
  }
}
