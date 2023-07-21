package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.ClassClassifier
import io.github.effiban.scala2java.core.contexts.ClassOrTraitContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Templates}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.transformers.ClassTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Defn, Mod, Type}

@deprecated
class DeprecatedClassTraverserImplTest extends UnitTestSuite {

  private val caseClassTraverser = mock[DeprecatedCaseClassTraverser]
  private val regularClassTraverser = mock[DeprecatedRegularClassTraverser]
  private val classTransformer = mock[ClassTransformer]
  private val classClassifier = mock[ClassClassifier]

  private val classTraverser = new DeprecatedClassTraverserImpl(
    caseClassTraverser,
    regularClassTraverser,
    classTransformer,
    classClassifier
  )

  test("traverse when case class") {
    val classDef = classDefOf("MyClass", List(Mod.Case()))
    val transformedClassDef = classDefOf("MyTransformedClass", List(Mod.Case()))

    when(classTransformer.transform(eqTree(classDef))).thenReturn(transformedClassDef)
    when(classClassifier.isCase(eqTree(transformedClassDef))).thenReturn(true)

    classTraverser.traverse(classDef, ClassOrTraitContext(JavaScope.Package))

    verify(caseClassTraverser).traverse(eqTree(transformedClassDef), ArgumentMatchers.eq(ClassOrTraitContext(JavaScope.Package)))
  }

  test("traverse when regular class") {
    val classDef = classDefOf("MyClass")
    val transformedClassDef = classDefOf("MyTransformedClass")
    when(classClassifier.isCase(eqTree(transformedClassDef))).thenReturn(false)

    when(classTransformer.transform(eqTree(classDef))).thenReturn(transformedClassDef)

    classTraverser.traverse(classDef, ClassOrTraitContext(JavaScope.Package))

    verify(regularClassTraverser).traverse(eqTree(transformedClassDef), ArgumentMatchers.eq(ClassOrTraitContext(JavaScope.Package)))
  }

  private def classDefOf(name: String, mods: List[Mod] = Nil) = {
    Defn.Class(
      mods = mods,
      name = Type.Name(name),
      tparams = Nil,
      ctor = PrimaryCtors.Empty,
      templ = Templates.Empty
    )
  }
}
