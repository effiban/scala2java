package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubAnnotListTraverser, StubTemplateTraverser}
import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor

import scala.meta.Term.Block
import scala.meta.{Defn, Init, Mod, Name, Self, Template, Term, Type}

class ObjectTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"
  private val ModifierStr = "public"

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modsCaptor = ArgCaptor[List[Mod]]

  private val objectTraverser = new ObjectTraverserImpl(
    new StubAnnotListTraverser(),
    new StubTemplateTraverser(),
    javaModifiersResolver)


  test("traverse()") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val objectDef = Defn.Object(
      mods = modifiers,
      name = Term.Name("MyObject"),
      templ = Template(
        early = List(),
        inits = List(),
        self = Self(name = Name.Anonymous(), decltpe = None),
        stats = List(
          Defn.Def(
            mods = List(),
            name = Term.Name("MyMethod"),
            tparams = List(),
            paramss = List(List(termParam("myParam1", "Int"), termParam("myParam2", "String"))),
            decltpe = Some(Type.Name("String")),
            body = Block(List())
          )
        )
      )
    )

    when(javaModifiersResolver.resolveForClass(any[List[Mod]])).thenReturn(List(ModifierStr))

    objectTraverser.traverse(objectDef)

    outputWriter.toString shouldBe
      """
        |/* originally a Scala object */
        |@MyAnnotation
        |public class MyObject
        |/**
        |* STUB TEMPLATE
        |* Input ClassInfo: None
        |* Scala Body:
        |* { def MyMethod(myParam1: Int, myParam2: String): String = {} }
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
