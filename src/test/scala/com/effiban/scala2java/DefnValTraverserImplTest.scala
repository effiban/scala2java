package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext
import com.effiban.scala2java.stubs.{StubAnnotListTraverser, StubPatListTraverser, StubTermTraverser, StubTypeTraverser}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor

import scala.meta.Mod.Final
import scala.meta.{Defn, Init, Lit, Mod, Name, Pat, Term, Type}

class DefnValTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"
  private val PrivateFinalModifiers = List("private", "final")
  private val FinalModifiers = List("final")

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modsCaptor = ArgCaptor[List[Mod]]

  private val defnValTraverser = new DefnValTraverserImpl(
    new StubAnnotListTraverser,
    new StubTypeTraverser,
    new StubPatListTraverser,
    new StubTermTraverser,
    javaModifiersResolver)


  test("traverse() when it is a class member - typed") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVal = Defn.Val(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Some(Type.Name("int")),
      rhs = Lit.Int(3)
    )

    when(javaModifiersResolver.resolveForClassDataMember(any[List[Mod]])).thenReturn(PrivateFinalModifiers)

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final int myVal = 3""".stripMargin

    verifyModifiersResolverInvocationForClassDataMember()
  }

  test("traverse() when it is a class member - untyped") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVal = Defn.Val(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = None,
      rhs = Lit.Int(3)
    )

    when(javaModifiersResolver.resolveForClassDataMember(any[List[Mod]])).thenReturn(PrivateFinalModifiers)

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final /* UnknownType */ myVal = 3""".stripMargin

    verifyModifiersResolverInvocationForClassDataMember()
  }

  test("traverse() when it is an interface member - typed") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVal = Defn.Val(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Some(Type.Name("int")),
      rhs = Lit.Int(3)
    )

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVal = 3""".stripMargin
  }

  test("traverse() when it is an interface member - untyped") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVal = Defn.Val(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = None,
      rhs = Lit.Int(3)
    )

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |/* UnknownType */ myVal = 3""".stripMargin
  }

  test("traverse() when it is a local variable - typed") {
    javaOwnerContext = Method

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVal = Defn.Val(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Some(Type.Name("int")),
      rhs = Lit.Int(3)
    )

    when(javaModifiersResolver.resolve(any[List[Mod]], ArgumentMatchers.eq(List(classOf[Final])))).thenReturn(FinalModifiers)

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |final int myVal = 3""".stripMargin

    verifyModifiersResolverInvocationForLocalVar()
  }

  test("traverse() when it is a local variable - untyped") {
    javaOwnerContext = Method

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVal = Defn.Val(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = None,
      rhs = Lit.Int(3)
    )

    when(javaModifiersResolver.resolve(any[List[Mod]], ArgumentMatchers.eq(List(classOf[Final])))).thenReturn(FinalModifiers)

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |final var myVal = 3""".stripMargin

    verifyModifiersResolverInvocationForLocalVar()
  }

  private def verifyModifiersResolverInvocationForClassDataMember() = {
    verify(javaModifiersResolver).resolveForClassDataMember(modsCaptor.capture)
    val actualMods = modsCaptor.value
    verifyTwoModifiersPassedToResolver(actualMods)
    verifyAnnotationPassedToResolver(actualMods.head)
    verifyFinalPassedToResolverAsSecondModifier(actualMods(1))
  }

  private def verifyModifiersResolverInvocationForLocalVar() = {
    verify(javaModifiersResolver).resolve(modsCaptor.capture, ArgumentMatchers.eq(List(classOf[Final])))
    val actualMods = modsCaptor.value
    verifyTwoModifiersPassedToResolver(actualMods)
    verifyAnnotationPassedToResolver(actualMods.head)
    verifyFinalPassedToResolverAsSecondModifier(actualMods(1))
  }

  private def verifyTwoModifiersPassedToResolver(actualMods: List[Mod]) = {
    withClue("Incorrect number of modifiers passed to JavaModifiersResolver: ") {
      actualMods.size shouldBe 2
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

  private def verifyFinalPassedToResolverAsSecondModifier(actualSecondMod: Mod) = {
    withClue(s"Incorrect type of second modifier passed to resolver: ") {
      actualSecondMod shouldBe a[Mod.Final]
    }
  }
}
