package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.ClassClassifier
import io.github.effiban.scala2java.core.contexts.ClassOrTraitContext
import io.github.effiban.scala2java.spi.transformers.ClassTransformer

import scala.meta.Defn

trait ClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Defn.Class
}

private[traversers] class ClassTraverserImpl(caseClassTraverser: => CaseClassTraverser,
                                             regularClassTraverser: => RegularClassTraverser,
                                             classTransformer: ClassTransformer,
                                             classClassifier: ClassClassifier) extends ClassTraverser {

  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Defn.Class = {
    val transformedClassDef = classTransformer.transform(classDef)
    if (classClassifier.isCase(transformedClassDef)) {
      caseClassTraverser.traverse(transformedClassDef, context)
    } else {
      regularClassTraverser.traverse(transformedClassDef, context)
    }
  }
}
