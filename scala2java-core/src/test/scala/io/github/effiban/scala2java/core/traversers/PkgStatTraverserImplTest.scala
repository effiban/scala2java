package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ClassOrTraitContext, StatContext}
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.matchers.ClassOrTraitContextMatcher.eqClassOrTraitContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{ClassTraversalResult, ObjectTraversalResult, SimpleStatTraversalResult, TraitTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgStatTraverserImplTest extends UnitTestSuite {

  private val TheImport = q"import extpkg.ExtClass"

  private val TheTrait =
    q"""
    trait MyTrait {
      final var x: Int
    }
    """

  private val TheClass =
    q"""
    class MyClass {
      def foo(x: Int) = x + 1
    }
    """

  private val TheObject =
    q"""
    object MyObject {
      val x: Int = 3
    }
    """

  private val classTraverser = mock[ClassTraverser]
  private val traitTraverser = mock[TraitTraverser]
  private val objectTraverser = mock[ObjectTraverser]
  private val defaultStatTraverser = mock[DefaultStatTraverser]

  private val traitTraversalResult = mock[TraitTraversalResult]
  private val classTraversalResult = mock[ClassTraversalResult]
  private val objectTraversalResult = mock[ObjectTraversalResult]
  private val importTraversalResult = mock[SimpleStatTraversalResult]

  private val pkgStatTraverser = new PkgStatTraverserImpl(
    classTraverser,
    traitTraverser,
    objectTraverser,
    defaultStatTraverser
  )

  test("traverse() for trait which is not sealed or child of sealed") {
    doReturn(traitTraversalResult).when(traitTraverser).traverse(
      eqTree(TheTrait),
      eqTo(ClassOrTraitContext(javaScope = JavaScope.Package))
    )

    pkgStatTraverser.traverse(TheTrait, SealedHierarchies()) shouldBe traitTraversalResult
  }

  test("traverse() for sealed trait which is not child of sealed") {
    val childNames = List(t"Child1", t"Child2")

    doReturn(traitTraversalResult).when(traitTraverser).traverse(
      eqTree(TheTrait),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Package))
    )

    pkgStatTraverser.traverse(TheTrait, SealedHierarchies(Map(TheTrait.name -> childNames))) shouldBe traitTraversalResult
  }

  test("traverse() for non-sealed trait which is a child of sealed") {
    val childNames = List(TheTrait.name, t"Other")

    doReturn(traitTraversalResult).when(traitTraverser).traverse(
      eqTree(TheTrait),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Sealed))
    )

    pkgStatTraverser.traverse(TheTrait, SealedHierarchies(Map(t"Parent" -> childNames))) shouldBe traitTraversalResult
  }

  test("traverse() for sealed trait which is also a child of sealed") {
    val traitChildNames = List(t"Child1", t"Child2")

    doReturn(traitTraversalResult).when(traitTraverser).traverse(
      eqTree(TheTrait),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Sealed))
    )

    val actualResult = pkgStatTraverser.traverse(TheTrait, SealedHierarchies(
      Map(
        t"Parent" -> List(TheTrait.name, t"Other"),
        TheTrait.name -> traitChildNames))
    )
    actualResult shouldBe traitTraversalResult
  }

  test("traverse() for class which is not sealed or child of sealed") {
    doReturn(classTraversalResult).when(classTraverser).traverse(
      eqTree(TheClass),
      eqTo(ClassOrTraitContext(javaScope = JavaScope.Package))
    )

    pkgStatTraverser.traverse(TheClass, SealedHierarchies()) shouldBe classTraversalResult
  }

  test("traverse() for sealed class which is not child of sealed") {
    val childNames = List(t"Child1", t"Child2")

    doReturn(classTraversalResult).when(classTraverser).traverse(
      eqTree(TheClass),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Package))
    )

    pkgStatTraverser.traverse(TheClass, SealedHierarchies(Map(TheClass.name -> childNames))) shouldBe classTraversalResult
  }

  test("traverse() for non-sealed class which is a child of sealed") {
    val childNames = List(TheClass.name, t"Other")

    doReturn(classTraversalResult).when(classTraverser).traverse(
      eqTree(TheClass),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Sealed))
    )

    pkgStatTraverser.traverse(TheClass, SealedHierarchies(Map(t"Parent" -> childNames))) shouldBe classTraversalResult
  }

  test("traverse() for sealed class which is also a child of sealed") {
    val traitChildNames = List(t"Child1", t"Child2")
    doReturn(classTraversalResult).when(classTraverser).traverse(
      eqTree(TheClass),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Sealed))
    )

    val actualResult = pkgStatTraverser.traverse(TheClass, SealedHierarchies(
      Map(
        t"Parent" -> List(TheClass.name, t"Other"),
        TheClass.name -> traitChildNames))
    )
    actualResult shouldBe classTraversalResult
  }

  test("traverse() for object which is not a child of sealed") {
    doReturn(objectTraversalResult).when(objectTraverser).traverse(
      eqTree(TheObject),
      eqTo(StatContext(javaScope = JavaScope.Package))
    )

    pkgStatTraverser.traverse(TheObject, SealedHierarchies()) shouldBe objectTraversalResult
  }

  test("traverse() for object which is a child of sealed") {
    val childNames = List(TheObject.name, Type.Name("Other"))

    doReturn(objectTraversalResult).when(objectTraverser).traverse(
      eqTree(TheObject),
      eqTo(StatContext(javaScope = JavaScope.Sealed))
    )

    pkgStatTraverser.traverse(TheObject, SealedHierarchies(Map(Type.Name("Parent") -> childNames))) shouldBe objectTraversalResult
  }

  test("traverse() for import") {
    doReturn(importTraversalResult)
      .when(defaultStatTraverser).traverse(eqTree(TheImport), eqTo(StatContext(javaScope = JavaScope.Package)))

    pkgStatTraverser.traverse(TheImport, SealedHierarchies()) shouldBe importTraversalResult
  }
}
