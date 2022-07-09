package effiban.scala2java.traversers

import scala.meta.{Defn, Mod}

trait ClassTraverser extends ScalaTreeTraverser[Defn.Class]

private[traversers] class ClassTraverserImpl(caseClassTraverser: => CaseClassTraverser,
                                             regularClassTraverser: => RegularClassTraverser) extends ClassTraverser {

  def traverse(classDef: Defn.Class): Unit = {
    if (classDef.mods.exists(_.isInstanceOf[Mod.Case])) {
      caseClassTraverser.traverse(classDef)
    } else {
      regularClassTraverser.traverse(classDef)
    }
  }
}

object ClassTraverser extends ClassTraverserImpl(CaseClassTraverser, RegularClassTraverser)
