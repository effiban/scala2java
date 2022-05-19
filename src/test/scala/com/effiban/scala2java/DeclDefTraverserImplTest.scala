package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext
import com.effiban.scala2java.stubs.{StubAnnotListTraverser, StubTermNameTraverser, StubTermParamListTraverser, StubTypeTraverser}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor

import scala.meta.Type.Bounds
import scala.meta.{Decl, Init, Mod, Name, Term, Type}

class DeclDefTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"
  private val ModifierStr = "public"

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

  private val methodParams1 = List(
    termParamInt("param1"),
    termParamInt("param2")
  )
  private val methodParams2 = List(
    termParamInt("param3"),
    termParamInt("param4")
  )

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modsCaptor = ArgCaptor[List[Mod]]

  private val declDefTraverser = new DeclDefTraverserImpl(
    new StubAnnotListTraverser,
    new StubTypeTraverser,
    new StubTermNameTraverser,
    new StubTermParamListTraverser,
    javaModifiersResolver)

  test("traverse() for class method when has one list of params") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val declDef = Decl.Def(
      mods = modifiers,
      name = Term.Name("myMethod"),
      tparams = typeParams,
      paramss = List(methodParams1),
      decltpe = Type.Name("int")
    )

    when(javaModifiersResolver.resolveForClassMethod(any[List[Mod]])).thenReturn(List(ModifierStr))

    declDefTraverser.traverse(declDef)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(int param1, int param2)""".stripMargin

    verifyModifiersResolverInvocationForClassMethod()
  }

  test("traverse() for interface method when has one list of params") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val declDef = Decl.Def(
      mods = modifiers,
      name = Term.Name("myMethod"),
      tparams = typeParams,
      paramss = List(methodParams1),
      decltpe = Type.Name("int")
    )

    when(javaModifiersResolver.resolveForInterfaceMethod(any[List[Mod]], ArgumentMatchers.eq(false))).thenReturn(List.empty)

    declDefTraverser.traverse(declDef)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |int myMethod(int param1, int param2)""".stripMargin

    verifyModifiersResolverInvocationForInterfaceMethod()
  }

  test("traverse() for interface method when has two lists of params") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val declDef = Decl.Def(
      mods = modifiers,
      name = Term.Name("myMethod"),
      tparams = typeParams,
      paramss = List(methodParams1, methodParams2),
      decltpe = Type.Name("int")
    )

    when(javaModifiersResolver.resolveForInterfaceMethod(any[List[Mod]], ArgumentMatchers.eq(false))).thenReturn(List.empty)

    declDefTraverser.traverse(declDef)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |int myMethod(int param1, int param2, int param3, int param4)""".stripMargin

    verifyModifiersResolverInvocationForInterfaceMethod()
  }

  private def termParamInt(name: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name("Int")), default = None)
  }

  private def verifyModifiersResolverInvocationForClassMethod() = {
    verify(javaModifiersResolver).resolveForClassMethod(modsCaptor.capture)
    verifyModifiersPassedToResolver()
  }

  private def verifyModifiersResolverInvocationForInterfaceMethod() = {
    verify(javaModifiersResolver).resolveForInterfaceMethod(modsCaptor.capture, ArgumentMatchers.eq(false))
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
