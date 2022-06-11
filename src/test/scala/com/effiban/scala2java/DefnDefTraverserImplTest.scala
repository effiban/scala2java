package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext
import com.effiban.scala2java.stubs._
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor

import scala.meta.Term.Block
import scala.meta.Type.Bounds
import scala.meta.{Defn, Init, Mod, Name, Term, Type}

class DefnDefTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"
  private val PublicModifierStr = "public"
  private val DefaultModifierStr = "default"

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

  private val defnDefTraverser = new DefnDefTraverserImpl(
    new StubAnnotListTraverser,
    new StubTermNameTraverser,
    new StubTypeTraverser,
    new StubTermParamListTraverser,
    new StubBlockTraverser,
    javaModifiersResolver)

  test("traverse() for class method with one statement returning int") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnDef = Defn.Def(
      mods = modifiers,
      name = Term.Name("myMethod"),
      tparams = typeParams,
      paramss = List(methodParams1),
      decltpe = Some(Type.Name("int")),
      body = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))
    )

    when(javaModifiersResolver.resolveForClassMethod(any[List[Mod]])).thenReturn(List(PublicModifierStr))

    defnDefTraverser.traverse(defnDef)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(int param1, int param2)
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   doSomething(param1)
        |* }
        |*/
        |""".stripMargin

    verifyModifiersResolverInvocationForClassMethod()
  }

  test("traverse() for class method with one statement retuning Unit") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnDef = Defn.Def(
      mods = modifiers,
      name = Term.Name("myMethod"),
      tparams = typeParams,
      paramss = List(methodParams1),
      decltpe = Some(Type.Name("Unit")),
      body = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))
    )

    when(javaModifiersResolver.resolveForClassMethod(any[List[Mod]])).thenReturn(List(PublicModifierStr))

    defnDefTraverser.traverse(defnDef)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public Unit myMethod(int param1, int param2)
        |/**
        |* STUB BLOCK
        |* Scala Body:
        |* {
        |*   doSomething(param1)
        |* }
        |*/
        |""".stripMargin

    verifyModifiersResolverInvocationForClassMethod()
  }

  test("traverse() for constructor") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnDef = Defn.Def(
      mods = modifiers,
      name = Term.Name("MyClass"),
      tparams = Nil,
      paramss = List(methodParams1),
      decltpe = Some(Type.AnonymousName()),
      body = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))
    )

    when(javaModifiersResolver.resolveForClassMethod(any[List[Mod]])).thenReturn(List(PublicModifierStr))

    val init = Init(
      tpe = Type.Singleton(Term.This(qual = Name.Anonymous())),
      name = Name.Anonymous(),
      argss = List(List(Term.Name("superParam1")))
    )

    defnDefTraverser.traverse(defnDef = defnDef, maybeInit = Some(init))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public MyClass(int param1, int param2)
        |/**
        |* STUB BLOCK
        |* Input Init: this(superParam1)
        |* Scala Body:
        |* {
        |*   doSomething(param1)
        |* }
        |*/
        |""".stripMargin

    verifyModifiersResolverInvocationForClassMethod()
  }

  test("traverse() for class method with one statement missing return type") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnDef = Defn.Def(
      mods = modifiers,
      name = Term.Name("myMethod"),
      tparams = typeParams,
      paramss = List(methodParams1),
      decltpe = None,
      body = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))
    )

    when(javaModifiersResolver.resolveForClassMethod(any[List[Mod]])).thenReturn(List(PublicModifierStr))

    defnDefTraverser.traverse(defnDef)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public /* UnknownType */ myMethod(int param1, int param2)
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   doSomething(param1)
        |* }
        |*/
        |""".stripMargin

    verifyModifiersResolverInvocationForClassMethod()
  }

  test("traverse() for class method with block") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnDef = Defn.Def(
      mods = modifiers,
      name = Term.Name("myMethod"),
      tparams = typeParams,
      paramss = List(methodParams1),
      decltpe = Some(Type.Name("int")),
      body = Block(
        stats = List(
          Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1"))),
          Term.Apply(fun = Term.Name("doSomethingElse"), args = List(Term.Name("param2")))
        )
      )
    )

    when(javaModifiersResolver.resolveForClassMethod(any[List[Mod]])).thenReturn(List(PublicModifierStr))

    defnDefTraverser.traverse(defnDef)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(int param1, int param2)
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   doSomething(param1)
        |*   doSomethingElse(param2)
        |* }
        |*/
        |""".stripMargin

    verifyModifiersResolverInvocationForClassMethod()
  }
  
  test("traverse() for interface method with one list of params") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnDef = Defn.Def(
      mods = modifiers,
      name = Term.Name("myMethod"),
      tparams = typeParams,
      paramss = List(methodParams1),
      decltpe = Some(Type.Name("int")),
      body = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))
    )

    when(javaModifiersResolver.resolveForInterfaceMethod(any[List[Mod]], ArgumentMatchers.eq(true)))
      .thenReturn(List(DefaultModifierStr))

    defnDefTraverser.traverse(defnDef)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |default int myMethod(int param1, int param2)
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   doSomething(param1)
        |* }
        |*/
        |""".stripMargin

    verifyModifiersResolverInvocationForInterfaceMethod()
  }

  test("traverse() for interface method with two lists of params") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnDef = Defn.Def(
      mods = modifiers,
      name = Term.Name("myMethod"),
      tparams = typeParams,
      paramss = List(methodParams1, methodParams2),
      decltpe = Some(Type.Name("int")),
      body = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))
    )

    when(javaModifiersResolver.resolveForInterfaceMethod(any[List[Mod]], ArgumentMatchers.eq(true)))
      .thenReturn(List(DefaultModifierStr))

    defnDefTraverser.traverse(defnDef)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |default int myMethod(int param1, int param2, int param3, int param4)
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   doSomething(param1)
        |* }
        |*/
        |""".stripMargin

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
    verify(javaModifiersResolver).resolveForInterfaceMethod(modsCaptor.capture, ArgumentMatchers.eq(true))
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
