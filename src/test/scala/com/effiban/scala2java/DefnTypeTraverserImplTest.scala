package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubTypeParamListTraverser, StubTypeTraverser}
import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor

import scala.meta.Type.Bounds
import scala.meta.{Defn, Init, Mod, Name, Type}

class DefnTypeTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"
  private val ModifierStr = "private"

  private val typeParams = List(
    Type.Param(
      mods = List(),
      name = Type.Name("T"),
      tparams = List(),
      tbounds = Bounds(lo = None, hi = None),
      vbounds = List(),
      cbounds = List()
    )
  )

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modsCaptor = ArgCaptor[List[Mod]]

  private val defnTypeTraverser = new DefnTypeTraverserImpl(
    new StubTypeParamListTraverser,
    new StubTypeTraverser,
    javaModifiersResolver)


  test("traverse() when has body and no bounds") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnType = Defn.Type(
      mods = modifiers,
      name = Type.Name("MyType"),
      tparams = typeParams,
      body = Type.Name("MyOtherType")
    )

    when(javaModifiersResolver.resolveForInterface(any[List[Mod]])).thenReturn(List(ModifierStr))

    defnTypeTraverser.traverse(defnType)

    outputWriter.toString shouldBe
      """private interface MyType<T> extends MyOtherType {
        |}
        |""".stripMargin

    verifyModifiersResolverInvocationForInterface()
  }

  test("traverse() when has no body and upper bound") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnType = Defn.Type(
      mods = modifiers,
      name = Type.Name("MyType"),
      tparams = typeParams,
      body = Type.AnonymousName(),
      bounds = Bounds(lo = None, hi = Some(Type.Name("MyOtherType")))
    )

    when(javaModifiersResolver.resolveForInterface(any[List[Mod]])).thenReturn(List(ModifierStr))

    defnTypeTraverser.traverse(defnType)

    outputWriter.toString shouldBe
      """private interface MyType<T> extends MyOtherType {
        |}
        |""".stripMargin

    verifyModifiersResolverInvocationForInterface()
  }

  test("traverse() when has no body and lower bound") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnType = Defn.Type(
      mods = modifiers,
      name = Type.Name("MyType"),
      tparams = typeParams,
      body = Type.AnonymousName(),
      bounds = Bounds(lo = Some(Type.Name("MyOtherType")), hi = None)
    )

    when(javaModifiersResolver.resolveForInterface(any[List[Mod]])).thenReturn(List(ModifierStr))

    defnTypeTraverser.traverse(defnType)

    outputWriter.toString shouldBe
      """private interface MyType<T>/* super MyOtherType */ {
        |}
        |""".stripMargin

    verifyModifiersResolverInvocationForInterface()
  }

  private def verifyModifiersResolverInvocationForInterface() = {
    verify(javaModifiersResolver).resolveForInterface(modsCaptor.capture)
    verifyModifiersPassedToResolver()
  }

  private def verifyModifiersPassedToResolver() = {
    val actualMods = modsCaptor.value
    verifyOneModifierPassedToResolver(actualMods)
    verifyAnnotationPassedToResolver(actualMods.head)
  }

  private def verifyOneModifierPassedToResolver(actualMods: List[Mod]) = {
    withClue("Incorrect number of modifiers passed to JavaModifiersResolver: ") {
      actualMods.size shouldBe 1
    }
  }

  private def verifyAnnotationPassedToResolver(actualMod: Mod) = {
    withClue("Incorrect type of modifier passed to resolver: ") {
      actualMod shouldBe a[Mod.Annot]
    }
    withClue("Incorrect name of annotation passed to resolver: ") {
      actualMod.asInstanceOf[Mod.Annot].init.tpe.toString() shouldBe AnnotationName
    }
  }
}
