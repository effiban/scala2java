package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import org.mockito.ArgumentMatchers

import scala.meta.Term.Block
import scala.meta.{Defn, Init, Mod, Name, Self, Template, Term, Type}

class ObjectTraverserImplTest extends UnitTestSuite {

  private val AnnotationName = "MyAnnotation"
  private val ModifierStr = "public"

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val annotListTraverser = mock[AnnotListTraverser]
  private val templateTraverser = mock[TemplateTraverser]

  private val objectTraverser = new ObjectTraverserImpl(
    annotListTraverser,
    templateTraverser,
    javaModifiersResolver)


  test("traverse()") {
    val modifiers: List[Mod] = List(
      Mod.Annot(
        Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
      )
    )

    val template = Template(
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

    val objectDef = Defn.Object(
      mods = modifiers,
      name = Term.Name("MyObject"),
      templ = template
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolveForClass(eqTreeList(modifiers))).thenReturn(List(ModifierStr))
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(eqTree(template))

    objectTraverser.traverse(objectDef)

    outputWriter.toString shouldBe
      """
        |/* originally a Scala object */
        |@MyAnnotation
        |public class MyObject {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }
}
