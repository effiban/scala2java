package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubAnnotListTraverser, StubTemplateTraverser, StubTermParamListTraverser, StubTypeParamListTraverser}
import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor

import scala.meta.Term.Block
import scala.meta.Type.Bounds
import scala.meta.{Ctor, Defn, Init, Mod, Name, Self, Template, Term, Type}

class CaseClassTraverserImplTest extends UnitTestSuite {

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

  private val emptyTemplate = Template(
    early = List(),
    inits = List(),
    self = Self(Name.Anonymous(), None),
    stats = List()
  )

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modsCaptor = ArgCaptor[List[Mod]]

  private val classTraverser = new CaseClassTraverserImpl(
    new StubAnnotListTraverser,
    new StubTypeParamListTraverser,
    new StubTermParamListTraverser,
    new StubTemplateTraverser,
    javaModifiersResolver)


  test("traverse() for one list of ctor args and no body") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.Case()
    )

    val cls = Defn.Class(
      mods = modifiers,
      name = Type.Name("MyRecord"),
      tparams = typeParams,
      ctor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(ctorArgs1)),
      templ = emptyTemplate
    )

    when(javaModifiersResolver.resolveForClass(any[List[Mod]])).thenReturn(List(ModifierStr))

    classTraverser.traverse(cls)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public record MyRecord<T>(int arg1, int arg2)
        |/**
        |* STUB TEMPLATE
        |* Input ClassInfo: ClassInfo(MyRecord,None)
        |* Scala Body: None
        |*/
        |""".stripMargin

    verifyModifiersResolverInvocation()
  }

  test("traverse() for two lists of ctor args and no body") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.Case()
    )

    val cls = Defn.Class(
      mods = modifiers,
      name = Type.Name("MyRecord"),
      tparams = typeParams,
      ctor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(ctorArgs1, ctorArgs2)),
      templ = emptyTemplate
    )

    when(javaModifiersResolver.resolveForClass(any[List[Mod]])).thenReturn(List(ModifierStr))

    classTraverser.traverse(cls)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public record MyRecord<T>(int arg1, int arg2, int arg3, int arg4)
        |/**
        |* STUB TEMPLATE
        |* Input ClassInfo: ClassInfo(MyRecord,None)
        |* Scala Body: None
        |*/
        |""".stripMargin

    verifyModifiersResolverInvocation()
  }

  test("traverse() for one list of ctor args and a body") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      ),
      Mod.Case()
    )

    val cls = Defn.Class(
      mods = modifiers,
      name = Type.Name("MyRecord"),
      tparams = typeParams,
      ctor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(ctorArgs1)),
      templ = Template(
        early = List(),
        inits = List(),
        self = Self(name = Name.Anonymous(), decltpe = None),
        stats = List(
          Defn.Def(
            mods = List(),
            name = Term.Name("MyMethod"),
            tparams = List(),
            paramss = List(List(termParam("myParam", "String"))),
            decltpe = Some(Type.Name("String")),
            body = Block(List())
          )
        )
      )
    )

    when(javaModifiersResolver.resolveForClass(any[List[Mod]])).thenReturn(List(ModifierStr))

    classTraverser.traverse(cls)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public record MyRecord<T>(int arg1, int arg2)
        |/**
        |* STUB TEMPLATE
        |* Input ClassInfo: ClassInfo(MyRecord,None)
        |* Scala Body:
        |* { def MyMethod(myParam: String): String = {} }
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
    verifyTwoModifiersPassedToResolver(actualMods)
    verifyAnnotationPassedToResolver(actualMods.head)
    withClue("Incorrect second modifier passed to resolver: ") {
      actualMods(1) shouldBe a[Mod.Case]
    }
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
}
