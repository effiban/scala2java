package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext
import com.effiban.scala2java.stubs.{StubAnnotListTraverser, StubPatListTraverser, StubTypeTraverser}
import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor

import scala.meta.{Decl, Init, Mod, Name, Pat, Term, Type}

class DeclVarTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"
  private val PrivateModifiers = List("private")

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modsCaptor = ArgCaptor[List[Mod]]

  private val declVarTraverser = new DeclVarTraverserImpl(
    new StubAnnotListTraverser,
    new StubTypeTraverser,
    new StubPatListTraverser,
    javaModifiersResolver)

  test("traverse() when it is a ctor. param") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.VarParam()
    )

    val declVar = Decl.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Type.Name("int")
    )

    when(javaModifiersResolver.resolveForClassDataMember(any[List[Mod]])).thenReturn(PrivateModifiers)

    declVarTraverser.traverse(declVar)

    outputWriter.toString shouldBe "@MyAnnotation private int myVar"

    verifyModifiersResolverInvocationForClassCtorParam()
  }

  test("traverse() when it is a class member") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val declVar = Decl.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Type.Name("int")
    )

    when(javaModifiersResolver.resolveForClassDataMember(any[List[Mod]])).thenReturn(PrivateModifiers)

    declVarTraverser.traverse(declVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar""".stripMargin

    verifyModifiersResolverInvocationForClassDataMember()
  }

  test("traverse() when it is an interface member") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val declVar = Decl.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Type.Name("int")
    )

    declVarTraverser.traverse(declVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  test("traverse() when it is a local variable") {
    javaOwnerContext = Method

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val declVar = Decl.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Type.Name("int")
    )

    declVarTraverser.traverse(declVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  private def verifyModifiersResolverInvocationForClassCtorParam() = {
    verify(javaModifiersResolver).resolveForClassDataMember(modsCaptor.capture)
    val actualMods = modsCaptor.value
    verifyNumModifiersPassedToResolver(actualMods, 2)
    verifyAnnotationPassedToResolver(actualMods.head)
    verifyVarParamPassedToResolverAsSecondModifier(actualMods(1))
  }

  private def verifyModifiersResolverInvocationForClassDataMember() = {
    verify(javaModifiersResolver).resolveForClassDataMember(modsCaptor.capture)
    val actualMods = modsCaptor.value
    verifyNumModifiersPassedToResolver(actualMods, 1)
    verifyAnnotationPassedToResolver(actualMods.head)
  }

  private def verifyNumModifiersPassedToResolver(actualMods: List[Mod], expectedNumMods: Int) = {
    withClue("Incorrect number of modifiers passed to JavaModifiersResolver: ") {
      actualMods.size shouldBe expectedNumMods
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

  private def verifyVarParamPassedToResolverAsSecondModifier(actualSecondMod: Mod) = {
    withClue(s"Incorrect type of second modifier passed to resolver: ") {
      actualSecondMod shouldBe a[Mod.VarParam]
    }
  }
}
