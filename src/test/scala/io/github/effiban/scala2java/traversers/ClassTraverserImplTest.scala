package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.ClassOrTraitContext
import io.github.effiban.scala2java.entities.JavaScope
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.{PrimaryCtors, Templates}
import org.mockito.ArgumentMatchers

import scala.meta.{Defn, Mod, Type}

class ClassTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val caseClassTraverser = mock[CaseClassTraverser]
  private val regularClassTraverser = mock[RegularClassTraverser]

  private val classTraverser = new ClassTraverserImpl(caseClassTraverser, regularClassTraverser)

  test("traverse when case class") {
    val classDef = Defn.Class(
      mods = List(Mod.Case()),
      name = ClassName,
      tparams = Nil,
      ctor = PrimaryCtors.Empty,
      templ = Templates.Empty
    )

    classTraverser.traverse(classDef, ClassOrTraitContext(JavaScope.Package))

    verify(caseClassTraverser).traverse(eqTree(classDef), ArgumentMatchers.eq(ClassOrTraitContext(JavaScope.Package)))
  }

  test("traverse when regular class") {
    val classDef = Defn.Class(
      mods = Nil,
      name = ClassName,
      tparams = Nil,
      ctor = PrimaryCtors.Empty,
      templ = Templates.Empty
    )

    classTraverser.traverse(classDef, ClassOrTraitContext(JavaScope.Package))

    verify(regularClassTraverser).traverse(eqTree(classDef), ArgumentMatchers.eq(ClassOrTraitContext(JavaScope.Package)))
  }
}
