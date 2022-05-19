package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext
import com.effiban.scala2java.stubs.{StubAnnotListTraverser, StubPatListTraverser, StubTypeTraverser}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor

import scala.meta.Mod.Final
import scala.meta.{Decl, Init, Mod, Name, Pat, Term, Type}

class DeclValTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"
  private val PrivateFinalModifiers = List("private", "final")
  private val FinalModifiers = List("final")

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modsCaptor = ArgCaptor[List[Mod]]

  private val declValTraverser = new DeclValTraverserImpl(
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
      Mod.ValParam()
    )

    val declVal = Decl.Val(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Type.Name("int")
    )

    when(javaModifiersResolver.resolveForClassDataMember(any[List[Mod]])).thenReturn(PrivateFinalModifiers)

    declValTraverser.traverse(declVal)

    outputWriter.toString shouldBe "@MyAnnotation private final int myVal"

    verifyModifiersResolverInvocationForClassCtorParam()
  }

  test("traverse() when it is a class member") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val declVal = Decl.Val(
      mods = modifiers,
      pats = List(Term.Name("myVal")),
      decltpe = Type.Name("int")
    )

    when(javaModifiersResolver.resolveForClassDataMember(any[List[Mod]])).thenReturn(PrivateFinalModifiers)

    declValTraverser.traverse(declVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final int myVal""".stripMargin

    verifyModifiersResolverInvocationForClassDataMember()
  }

  test("traverse() when it is an interface member") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val declVal = Decl.Val(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Type.Name("int")
    )

    declValTraverser.traverse(declVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVal""".stripMargin
  }

  test("traverse() when it is a local variable") {
    javaOwnerContext = Method

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val declVal = Decl.Val(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Type.Name("int")
    )

    when(javaModifiersResolver.resolve(any[List[Mod]], ArgumentMatchers.eq(List(classOf[Final])))).thenReturn(FinalModifiers)

    declValTraverser.traverse(declVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |final int myVal""".stripMargin

    verifyModifiersResolverInvocationForLocalVar()
  }

  private def verifyModifiersResolverInvocationForClassCtorParam() = {
    verify(javaModifiersResolver).resolveForClassDataMember(modsCaptor.capture)
    val actualMods = modsCaptor.value
    verifyNumModifiersPassedToResolver(actualMods, 3)
    verifyAnnotationPassedToResolver(actualMods.head)
    verifyValParamPassedToResolverAsSecondModifier(actualMods(1))
    verifyFinalPassedToResolver(actualMods, 2)
  }

  private def verifyModifiersResolverInvocationForClassDataMember() = {
    verify(javaModifiersResolver).resolveForClassDataMember(modsCaptor.capture)
    val actualMods = modsCaptor.value
    verifyNumModifiersPassedToResolver(actualMods, 2)
    verifyAnnotationPassedToResolver(actualMods.head)
    verifyFinalPassedToResolver(actualMods, 1)
  }

  private def verifyModifiersResolverInvocationForLocalVar() = {
    verify(javaModifiersResolver).resolve(modsCaptor.capture, ArgumentMatchers.eq(List(classOf[Final])))
    val actualMods = modsCaptor.value
    verifyNumModifiersPassedToResolver(actualMods, 2)
    verifyAnnotationPassedToResolver(actualMods.head)
    verifyFinalPassedToResolver(actualMods, 1)
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

  private def verifyValParamPassedToResolverAsSecondModifier(actualSecondMod: Mod) = {
    withClue(s"Incorrect type of second modifier passed to resolver: ") {
      actualSecondMod shouldBe a[Mod.ValParam]
    }
  }

  private def verifyFinalPassedToResolver(actualMods: List[Mod], idx: Int) = {
    withClue(s"Incorrect type of modifier passed to resolver at index $idx: ") {
      actualMods(idx) shouldBe a[Mod.Final]
    }
  }
}
