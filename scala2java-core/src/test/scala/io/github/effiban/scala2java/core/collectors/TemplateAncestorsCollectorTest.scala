package io.github.effiban.scala2java.core.collectors

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Type, XtensionQuasiquoteTemplate, XtensionQuasiquoteType}

class TemplateAncestorsCollectorTest extends UnitTestSuite {

  private val scalaReflectionLookup = mock[ScalaReflectionLookup]

  private val templateAncestorsCollector = new TemplateAncestorsCollectorImpl(scalaReflectionLookup)

  import templateAncestorsCollector._

  test("collect for template with one init and no self, with 2 ancestors, all are Type.Name-s") {
    val expectedAncestorTypes = List(t"A1", t"A2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(eqTree(t"A"))).thenReturn(expectedAncestorTypes)

    val actualAncestorTypes = collect(template"A")
    actualAncestorTypes.structure shouldBe expectedAncestorTypes.structure
  }

  test("collect for template with one init and no self, with 2 ancestors, all are Type.Select-s") {
    val expectedAncestorTypes = List(t"a.A1", t"a.A2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(eqTree(t"a.A"))).thenReturn(expectedAncestorTypes)

    val actualAncestorTypes = collect(template"a.A")
    actualAncestorTypes.structure shouldBe expectedAncestorTypes.structure
  }

  test("collect for template with one init and no self, with 2 ancestors, all are Type.Project-s") {
    val expectedAncestorTypes = List(t"a.A1#B1", t"a.A2#B2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(eqTree(t"a.A#B"))).thenReturn(expectedAncestorTypes)

    val actualAncestorTypes = collect(template"a.A#B")
    actualAncestorTypes.structure shouldBe expectedAncestorTypes.structure
  }

  test("collect for template with one init and no self, with 2 ancestors, init is a Type.Apply") {
    val expectedAncestorTypes = List(t"a.A1", t"a.A2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(eqTree(t"a.A"))).thenReturn(expectedAncestorTypes)

    val actualAncestorTypes = collect(template"a.A[B]")
    actualAncestorTypes.structure shouldBe expectedAncestorTypes.structure
  }

  test("collect for template with one init and one self, with 2 ancestors each") {
    val expectedAncestorTypesOfA = List(t"A1", t"A2")
    val expectedAncestorTypesOfB = List(t"B1", t"B2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(any[Type.Ref])).thenAnswer((tpe: Type.Ref) =>
      tpe match {
        case t"A" => expectedAncestorTypesOfA
        case t"B" => expectedAncestorTypesOfB
        case _ => Nil
      })

    val actualAncestorTypes = collect(template"A { self: B => }")
    actualAncestorTypes.structure shouldBe (expectedAncestorTypesOfA ++ expectedAncestorTypesOfB).structure
  }

  test("collect for template with two inits and 2 ancestors each") {
    val expectedAncestorTypesOfA = List(t"A1", t"A2")
    val expectedAncestorTypesOfB = List(t"B1", t"B2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(any[Type.Ref])).thenAnswer((tpe: Type.Ref) =>
      tpe match {
        case t"A" => expectedAncestorTypesOfA
        case t"B" => expectedAncestorTypesOfB
        case _ => Nil
      })

    val actualAncestorTypes = collect(template"A with B")
    actualAncestorTypes.structure shouldBe (expectedAncestorTypesOfA ++ expectedAncestorTypesOfB).structure
  }

  test("collectToMap for template with one init and no self, with 2 ancestors, Type.Name-s") {
    val expectedAncestorTypes = List(t"A1", t"A2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(eqTree(t"A"))).thenReturn(expectedAncestorTypes)

    val actualAncestorTypes = collectToMap(template"A")
    actualAncestorTypes.size shouldBe 1
    actualAncestorTypes.values.head.structure shouldBe expectedAncestorTypes.structure
  }

  test("collectToMap for template with one init and no self, with 2 ancestors, Type.Select-s") {
    val expectedAncestorTypes = List(t"a.A1", t"a.A2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(eqTree(t"a.A"))).thenReturn(expectedAncestorTypes)

    val actualAncestorTypes = collectToMap(template"a.A")
    actualAncestorTypes.size shouldBe 1
    actualAncestorTypes.values.head.structure shouldBe expectedAncestorTypes.structure
  }

  test("collectToMap for template with one init and no self, with 2 ancestors, Type.Project-s") {
    val expectedAncestorTypes = List(t"a.A1#B1", t"a.A2#B2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(eqTree(t"a.A#B"))).thenReturn(expectedAncestorTypes)

    val actualAncestorTypes = collectToMap(template"a.A#B")
    actualAncestorTypes.size shouldBe 1
    actualAncestorTypes.values.head.structure shouldBe expectedAncestorTypes.structure
  }

  test("collectToMap for template with one init and one self, with 2 ancestors each") {
    val expectedAncestorTypesOfA = List(t"A1", t"A2")
    val expectedAncestorTypesOfB = List(t"B1", t"B2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(any[Type.Ref])).thenAnswer((tpe: Type.Ref) =>
      tpe match {
        case t"A" => expectedAncestorTypesOfA
        case t"B" => expectedAncestorTypesOfB
        case _ => Nil
      })

    val actualAncestorTypes = collectToMap(template"A { self: B => }")
    actualAncestorTypes.size shouldBe 2
    TreeKeyedMap.get(actualAncestorTypes, t"A").value.structure shouldBe expectedAncestorTypesOfA.structure
    TreeKeyedMap.get(actualAncestorTypes, t"B").value.structure shouldBe expectedAncestorTypesOfB.structure
  }

  test("collectToMap for template with two inits and 2 ancestors each") {
    val expectedAncestorTypesOfA = List(t"A1", t"A2")
    val expectedAncestorTypesOfB = List(t"B1", t"B2")

    when(scalaReflectionLookup.findSelfAndBaseClassesOf(any[Type.Ref])).thenAnswer((tpe: Type.Ref) =>
      tpe match {
        case t"A" => expectedAncestorTypesOfA
        case t"B" => expectedAncestorTypesOfB
        case _ => Nil
      })

    val actualAncestorTypes = collectToMap(template"A with B")
    actualAncestorTypes.size shouldBe 2
    TreeKeyedMap.get(actualAncestorTypes, t"A").value.structure shouldBe expectedAncestorTypesOfA.structure
    TreeKeyedMap.get(actualAncestorTypes, t"B").value.structure shouldBe expectedAncestorTypesOfB.structure
  }
}
