package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ClassOrTraitContext
import io.github.effiban.scala2java.spi.transformers.ClassTransformer

import scala.meta.{Defn, Mod}

trait ClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

private[traversers] class ClassTraverserImpl(caseClassTraverser: => CaseClassTraverser,
                                             regularClassTraverser: => RegularClassTraverser,
                                             classTransformer: ClassTransformer) extends ClassTraverser {

  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    val transformedClassDef = classTransformer.transform(classDef)
    if (transformedClassDef.mods.exists(_.isInstanceOf[Mod.Case])) {
      caseClassTraverser.traverse(transformedClassDef, context)
    } else {
      regularClassTraverser.traverse(transformedClassDef, context)
    }
  }
}
