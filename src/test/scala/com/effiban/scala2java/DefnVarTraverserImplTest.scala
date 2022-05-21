package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext
import com.effiban.scala2java.stubs.{StubAnnotListTraverser, StubPatListTraverser, StubTermTraverser, StubTypeTraverser}
import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor

import scala.meta.{Defn, Init, Lit, Mod, Name, Pat, Term, Type}

class DefnVarTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"
  private val PrivateModifiers = List("private")

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modsCaptor = ArgCaptor[List[Mod]]

  private val defnVarTraverser = new DefnVarTraverserImpl(
    new StubAnnotListTraverser,
    new StubTypeTraverser,
    new StubPatListTraverser,
    new StubTermTraverser,
    javaModifiersResolver)


  test("traverse() when it is a class member - typed with value") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVar = Defn.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Some(Type.Name("int")),
      rhs = Some(Lit.Int(3))
    )

    when(javaModifiersResolver.resolveForClassDataMember(any[List[Mod]])).thenReturn(PrivateModifiers)

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar = 3""".stripMargin

    verifyModifiersResolverInvocationForClassDataMember()
  }

  test("traverse() when it is a class member - typed without value") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVar = Defn.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Some(Type.Name("int")),
      rhs = None
    )

    when(javaModifiersResolver.resolveForClassDataMember(any[List[Mod]])).thenReturn(PrivateModifiers)

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar""".stripMargin

    verifyModifiersResolverInvocationForClassDataMember()
  }

  test("traverse() when it is a class member - untyped with value") {
    javaOwnerContext = Class

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVar = Defn.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = None,
      rhs = Some(Lit.Int(3))
    )

    when(javaModifiersResolver.resolveForClassDataMember(any[List[Mod]])).thenReturn(PrivateModifiers)

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private /* UnknownType */ myVar = 3""".stripMargin

    verifyModifiersResolverInvocationForClassDataMember()
  }

  test("traverse() when it is an interface member - typed with value") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVar = Defn.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Some(Type.Name("int")),
      rhs = Some(Lit.Int(3))
    )

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar = 3""".stripMargin
  }

  test("traverse() when it is an interface member - typed without value") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVar = Defn.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Some(Type.Name("int")),
      rhs = None
    )

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  test("traverse() when it is an interface member - untyped with value") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVar = Defn.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = None,
      rhs = Some(Lit.Int(3))
    )

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |/* UnknownType */ myVar = 3""".stripMargin
  }

  test("traverse() when it is a local variable - typed with value") {
    javaOwnerContext = Method

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVar = Defn.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Some(Type.Name("int")),
      rhs = Some(Lit.Int(3))
    )

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar = 3""".stripMargin
  }

  test("traverse() when it is a local variable - typed without value") {
    javaOwnerContext = Method

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVar = Defn.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Some(Type.Name("int")),
      rhs = None
    )

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  test("traverse() when it is a local variable - untyped with value") {
    javaOwnerContext = Method

    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val defnVar = Defn.Var(
      mods = modifiers,
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = None,
      rhs = Some(Lit.Int(3))
    )

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |var myVar = 3""".stripMargin
  }

  private def verifyModifiersResolverInvocationForClassDataMember() = {
    verify(javaModifiersResolver).resolveForClassDataMember(modsCaptor.capture)
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
