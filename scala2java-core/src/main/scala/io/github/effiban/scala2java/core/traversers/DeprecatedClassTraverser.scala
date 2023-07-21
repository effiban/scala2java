package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.ClassClassifier
import io.github.effiban.scala2java.core.contexts.ClassOrTraitContext
import io.github.effiban.scala2java.spi.transformers.ClassTransformer

import scala.meta.Defn

@deprecated
trait DeprecatedClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

@deprecated
private[traversers] class DeprecatedClassTraverserImpl(caseClassTraverser: => DeprecatedCaseClassTraverser,
                                                       regularClassTraverser: => DeprecatedRegularClassTraverser,
                                                       classTransformer: ClassTransformer,
                                                       classClassifier: ClassClassifier) extends DeprecatedClassTraverser {

  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    val transformedClassDef = classTransformer.transform(classDef)
    if (classClassifier.isCase(transformedClassDef)) {
      caseClassTraverser.traverse(transformedClassDef, context)
    } else {
      regularClassTraverser.traverse(transformedClassDef, context)
    }
  }
}
