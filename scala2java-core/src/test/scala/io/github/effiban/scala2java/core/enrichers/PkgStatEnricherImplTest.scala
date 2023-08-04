package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedClass, EnrichedObject, EnrichedSimpleStat, EnrichedTrait}
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgStatEnricherImplTest extends UnitTestSuite {

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

  private val classEnricher = mock[ClassEnricher]
  private val traitEnricher = mock[TraitEnricher]
  private val objectEnricher = mock[ObjectEnricher]
  private val defaultStatEnricher = mock[DefaultStatEnricher]

  private val enrichedTrait = mock[EnrichedTrait]
  private val enrichedClass = mock[EnrichedClass]
  private val enrichedObject = mock[EnrichedObject]
  private val enrichedImport = mock[EnrichedSimpleStat]

  private val pkgStatEnricher = new PkgStatEnricherImpl(
    classEnricher,
    traitEnricher,
    objectEnricher,
    defaultStatEnricher
  )

  test("enrich() for trait which is not sealed or child of sealed") {
    doReturn(enrichedTrait).when(traitEnricher).enrich(
      eqTree(TheTrait),
      eqTo(StatContext(javaScope = JavaScope.Package))
    )

    pkgStatEnricher.enrich(TheTrait, SealedHierarchies()) shouldBe enrichedTrait
  }

  test("enrich() for sealed trait which is not child of sealed") {
    val childNames = List(t"Child1", t"Child2")

    doReturn(enrichedTrait).when(traitEnricher).enrich(
      eqTree(TheTrait),
      eqTo(StatContext(javaScope = JavaScope.Package))
    )

    pkgStatEnricher.enrich(TheTrait, SealedHierarchies(Map(TheTrait.name -> childNames))) shouldBe enrichedTrait
  }

  test("enrich() for non-sealed trait which is a child of sealed") {
    val childNames = List(TheTrait.name, t"Other")

    doReturn(enrichedTrait).when(traitEnricher).enrich(
      eqTree(TheTrait),
      eqTo(StatContext(javaScope = JavaScope.Sealed))
    )

    pkgStatEnricher.enrich(TheTrait, SealedHierarchies(Map(t"Parent" -> childNames))) shouldBe enrichedTrait
  }

  test("enrich() for sealed trait which is also a child of sealed") {
    val traitChildNames = List(t"Child1", t"Child2")

    doReturn(enrichedTrait).when(traitEnricher).enrich(
      eqTree(TheTrait),
      eqTo(StatContext(javaScope = JavaScope.Sealed))
    )

    val actualResult = pkgStatEnricher.enrich(TheTrait, SealedHierarchies(
      Map(
        t"Parent" -> List(TheTrait.name, t"Other"),
        TheTrait.name -> traitChildNames))
    )
    actualResult shouldBe enrichedTrait
  }

  test("enrich() for class which is not sealed or child of sealed") {
    doReturn(enrichedClass).when(classEnricher).enrich(
      eqTree(TheClass),
      eqTo(StatContext(javaScope = JavaScope.Package))
    )

    pkgStatEnricher.enrich(TheClass, SealedHierarchies()) shouldBe enrichedClass
  }

  test("enrich() for sealed class which is not child of sealed") {
    val childNames = List(t"Child1", t"Child2")

    doReturn(enrichedClass).when(classEnricher).enrich(
      eqTree(TheClass),
      eqTo(StatContext(javaScope = JavaScope.Package))
    )

    pkgStatEnricher.enrich(TheClass, SealedHierarchies(Map(TheClass.name -> childNames))) shouldBe enrichedClass
  }

  test("enrich() for non-sealed class which is a child of sealed") {
    val childNames = List(TheClass.name, t"Other")

    doReturn(enrichedClass).when(classEnricher).enrich(
      eqTree(TheClass),
      eqTo(StatContext(javaScope = JavaScope.Sealed))
    )

    pkgStatEnricher.enrich(TheClass, SealedHierarchies(Map(t"Parent" -> childNames))) shouldBe enrichedClass
  }

  test("enrich() for sealed class which is also a child of sealed") {
    val traitChildNames = List(t"Child1", t"Child2")
    doReturn(enrichedClass).when(classEnricher).enrich(
      eqTree(TheClass),
      eqTo(StatContext(javaScope = JavaScope.Sealed))
    )

    val actualResult = pkgStatEnricher.enrich(TheClass, SealedHierarchies(
      Map(
        t"Parent" -> List(TheClass.name, t"Other"),
        TheClass.name -> traitChildNames))
    )
    actualResult shouldBe enrichedClass
  }

  test("enrich() for object which is not a child of sealed") {
    doReturn(enrichedObject).when(objectEnricher).enrich(
      eqTree(TheObject),
      eqTo(StatContext(javaScope = JavaScope.Package))
    )

    pkgStatEnricher.enrich(TheObject, SealedHierarchies()) shouldBe enrichedObject
  }

  test("enrich() for object which is a child of sealed") {
    val childNames = List(TheObject.name, Type.Name("Other"))

    doReturn(enrichedObject).when(objectEnricher).enrich(
      eqTree(TheObject),
      eqTo(StatContext(javaScope = JavaScope.Sealed))
    )

    pkgStatEnricher.enrich(TheObject, SealedHierarchies(Map(Type.Name("Parent") -> childNames))) shouldBe enrichedObject
  }

  test("enrich() for import") {
    doReturn(enrichedImport)
      .when(defaultStatEnricher).enrich(eqTree(TheImport), eqTo(StatContext(javaScope = JavaScope.Package)))

    pkgStatEnricher.enrich(TheImport, SealedHierarchies()) shouldBe enrichedImport
  }
}
