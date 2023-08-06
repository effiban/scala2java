package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ClassOrTraitContext, StatContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class PkgStatTraverserImplTest extends UnitTestSuite {

  private val TheImport = q"import extpkg.ExtClass"
  private val TheTraversedImport = q"import extpkg2.ExtClass2"

  private val TheTrait = q"trait MyTrait { final var x: Int }"
  private val TheTraversedTrait = q"trait MyTraversedTrait { final var xx: Int }"

  private val TheClass = q"class MyClass { def foo(x: Int) = x + 1 }"
  private val TheTraversedClass = q"class MyTraversedClass { def foo(xx: Int) = xx + 1 }"

  private val TheObject = q"object MyObject { val x: Int = 3 } "
  private val TheTraversedObject = q"object MyTraversedObject { val xx: Int = 3 } "

  private val classTraverser = mock[ClassTraverser]
  private val traitTraverser = mock[TraitTraverser]
  private val objectTraverser = mock[ObjectTraverser]
  private val defaultStatTraverser = mock[DefaultStatTraverser]

  private val objectTraversalResult = mock[ObjectTraversalResult]

  private val pkgStatTraverser = new PkgStatTraverserImpl(
    classTraverser,
    traitTraverser,
    objectTraverser,
    defaultStatTraverser
  )

  test("traverse() for trait") {
    doReturn(TheTraversedTrait).when(traitTraverser).traverse(eqTree(TheTrait))

    pkgStatTraverser.traverse(TheTrait).value.structure shouldBe TheTraversedTrait.structure
  }

  test("traverse() for class") {
    doReturn(TheTraversedClass).when(classTraverser).traverse(
      eqTree(TheClass),
      eqTo(ClassOrTraitContext(javaScope = JavaScope.Package))
    )

    pkgStatTraverser.traverse(TheClass).value.structure shouldBe TheTraversedClass.structure
  }

  test("traverse() for object") {
    doReturn(objectTraversalResult).when(objectTraverser).traverse(
      eqTree(TheObject),
      eqTo(StatContext(javaScope = JavaScope.Package))
    )
    when(objectTraversalResult.tree).thenReturn(TheTraversedObject)

    pkgStatTraverser.traverse(TheObject).value.structure shouldBe TheTraversedObject.structure
  }

  test("traverse() for included import") {
    doReturn(Some(TheTraversedImport))
      .when(defaultStatTraverser).traverse(eqTree(TheImport), eqTo(StatContext(javaScope = JavaScope.Package)))

    pkgStatTraverser.traverse(TheImport).value.structure shouldBe TheTraversedImport.structure
  }

  test("traverse() for excluded import") {
    doReturn(None)
      .when(defaultStatTraverser).traverse(eqTree(TheImport), eqTo(StatContext(javaScope = JavaScope.Package)))

    pkgStatTraverser.traverse(TheImport) shouldBe None
  }
}
