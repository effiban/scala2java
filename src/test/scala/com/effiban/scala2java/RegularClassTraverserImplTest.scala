package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubAnnotListTraverser, StubTemplateTraverser, StubTermParamListTraverser, StubTypeParamListTraverser}
import com.effiban.scala2java.transformers.ParamToDeclValTransformer
import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor

import scala.meta.Term.Block
import scala.meta.Type.Bounds
import scala.meta.{Ctor, Defn, Init, Mod, Name, Self, Template, Term, Type}

class RegularClassTraverserImplTest extends UnitTestSuite {

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

  private val ctorArgs1 = List(
    termParam("arg1", "Int"),
    termParam("arg2", "Int")
  )
  private val ctorArgs2 = List(
    termParam("arg3", "Int"),
    termParam("arg4", "Int")
  )

  private val TemplateWithOneMethod =
    Template(
      early = List(),
      inits = List(),
      self = Self(name = Name.Anonymous(), decltpe = None),
      stats = List(
        Defn.Def(
          mods = List(),
          name = Term.Name("myMethod"),
          tparams = List(),
          paramss = List(List(termParam("myParam", "String"))),
          decltpe = Some(Type.Name("String")),
          body = Block(List())
        )
      )
    )

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modsCaptor = ArgCaptor[List[Mod]]

  private val classTraverser = new RegularClassTraverserImpl(
    new StubAnnotListTraverser,
    new StubTypeParamListTraverser,
    new StubTermParamListTraverser,
    new StubTemplateTraverser,
    ParamToDeclValTransformer,
    javaModifiersResolver)


  test("traverse() for one list of ctor args") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val cls = Defn.Class(
      mods = modifiers,
      name = Type.Name("MyClass"),
      tparams = typeParams,
      ctor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(ctorArgs1)),
      templ = TemplateWithOneMethod
    )

    when(javaModifiersResolver.resolveForClass(any[List[Mod]])).thenReturn(List(ModifierStr))

    classTraverser.traverse(cls)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public class MyClass<T>
        |/**
        |* STUB TEMPLATE
        |* Input ClassInfo: ClassInfo(MyClass,Some(def this(arg1: Int, arg2: Int)))
        |* Scala Body:
        |* {
        |*   private final val arg1: Int
        |*   private final val arg2: Int
        |*   def myMethod(myParam: String): String = {}
        |* }
        |*/
        |""".stripMargin

    verifyModifiersResolverInvocation()
  }

  test("traverse() for two lists of ctor args") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val cls = Defn.Class(
      mods = modifiers,
      name = Type.Name("MyClass"),
      tparams = typeParams,
      ctor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(ctorArgs1, ctorArgs2)),
      templ = TemplateWithOneMethod
    )

    when(javaModifiersResolver.resolveForClass(any[List[Mod]])).thenReturn(List(ModifierStr))

    classTraverser.traverse(cls)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public class MyClass<T>
        |/**
        |* STUB TEMPLATE
        |* Input ClassInfo: ClassInfo(MyClass,Some(def this(arg1: Int, arg2: Int)(arg3: Int, arg4: Int)))
        |* Scala Body:
        |* {
        |*   private final val arg1: Int
        |*   private final val arg2: Int
        |*   private final val arg3: Int
        |*   private final val arg4: Int
        |*   def myMethod(myParam: String): String = {}
        |* }
        |*/
        |""".stripMargin

    verifyModifiersResolverInvocation()
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }

  private def verifyModifiersResolverInvocation() = {
    verify(javaModifiersResolver).resolveForClass(modsCaptor.capture)
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
