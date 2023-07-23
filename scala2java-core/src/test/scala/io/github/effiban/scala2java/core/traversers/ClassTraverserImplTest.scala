package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.ClassClassifier
import io.github.effiban.scala2java.core.contexts.ClassOrTraitContext
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Templates}
import io.github.effiban.scala2java.core.traversers.results.matchers.ClassTraversalResultScalatestMatcher.equalClassTraversalResult
import io.github.effiban.scala2java.core.traversers.results.{CaseClassTraversalResult, RegularClassTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.transformers.ClassTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Defn, Mod, Type, XtensionQuasiquoteType}

class ClassTraverserImplTest extends UnitTestSuite {

  private val caseClassTraverser = mock[CaseClassTraverser]
  private val regularClassTraverser = mock[RegularClassTraverser]
  private val classTransformer = mock[ClassTransformer]
  private val classClassifier = mock[ClassClassifier]

  private val classTraverser = new ClassTraverserImpl(
    caseClassTraverser,
    regularClassTraverser,
    classTransformer,
    classClassifier
  )

  test("traverse when case class") {
    val classDef = classDefOf("MyClass", List(Mod.Case()))
    val transformedClassDef = classDefOf("MyTransformedClass", List(Mod.Case()))
    val expectedTraversalResult = CaseClassTraversalResult(
      javaModifiers = List(JavaModifier.Public),
      name = t"MyTransformedClass",
      ctor = PrimaryCtors.Empty
    )

    when(classTransformer.transform(eqTree(classDef))).thenReturn(transformedClassDef)
    when(classClassifier.isCase(eqTree(transformedClassDef))).thenReturn(true)
    doReturn(expectedTraversalResult)
      .when(caseClassTraverser).traverse(eqTree(transformedClassDef), eqTo(ClassOrTraitContext(JavaScope.Package)))

    classTraverser.traverse(classDef, ClassOrTraitContext(JavaScope.Package)) should equalClassTraversalResult(expectedTraversalResult)
  }

  test("traverse when regular class") {
    val classDef = classDefOf("MyClass")
    val transformedClassDef = classDefOf("MyTransformedClass")
    val expectedTraversalResult = RegularClassTraversalResult(
      javaModifiers = List(JavaModifier.Public),
      name = t"MyTransformedClass",
      ctor = PrimaryCtors.Empty
    )

    when(classClassifier.isCase(eqTree(transformedClassDef))).thenReturn(false)
    when(classTransformer.transform(eqTree(classDef))).thenReturn(transformedClassDef)
    doReturn(expectedTraversalResult)
      .when(regularClassTraverser).traverse(eqTree(transformedClassDef), eqTo(ClassOrTraitContext(JavaScope.Package)))

    classTraverser.traverse(classDef, ClassOrTraitContext(JavaScope.Package)) should equalClassTraversalResult(expectedTraversalResult)
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
