package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ClassOrTraitContext
import io.github.effiban.scala2java.core.entities.JavaScope
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Templates}
import io.github.effiban.scala2java.spi.transformers.ClassTransformer
import org.mockito.ArgumentMatchers

import scala.meta.{Defn, Mod, Type}

class ClassTraverserImplTest extends UnitTestSuite {

  private val caseClassTraverser = mock[CaseClassTraverser]
  private val regularClassTraverser = mock[RegularClassTraverser]
  private val classTransformer = mock[ClassTransformer]

  private val classTraverser = new ClassTraverserImpl(
    caseClassTraverser,
    regularClassTraverser,
    classTransformer
  )

  test("traverse when case class") {
    val classDef = classDefOf("MyClass", List(Mod.Case()))
    val transformedClassDef = classDefOf("MyTransformedClass", List(Mod.Case()))

    when(classTransformer.transform(eqTree(classDef))).thenReturn(transformedClassDef)

    classTraverser.traverse(classDef, ClassOrTraitContext(JavaScope.Package))

    verify(caseClassTraverser).traverse(eqTree(transformedClassDef), ArgumentMatchers.eq(ClassOrTraitContext(JavaScope.Package)))
  }

  test("traverse when regular class") {
    val classDef = classDefOf("MyClass")
    val transformedClassDef = classDefOf("MyTransformedClass")

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
