package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite

import scala.meta.Type.Bounds
import scala.meta.{Decl, Init, Mod, Name, Type}

class DeclTypeTraverserImplTest extends UnitTestSuite {

  private val JavaModifier = "private"

  private val Modifiers: List[Mod] = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )

  private val TypeParams = List(
    Type.Param(
      mods = List(),
      name = Type.Name("T"),
      tparams = List(),
      tbounds = Bounds(lo = None, hi = None),
      vbounds = List(),
      cbounds = List()
    )
  )

  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val declTypeTraverser = new DeclTypeTraverserImpl(
    typeParamListTraverser,
    javaModifiersResolver)


  test("traverse()") {

    val declType = Decl.Type(
      mods = Modifiers,
      name = Type.Name("MyType"),
      tparams = TypeParams,
      bounds = Bounds(lo = None, hi = Some(Type.Name("T")))
    )

    when(javaModifiersResolver.resolveForInterface(eqTreeList(Modifiers))).thenReturn(List(JavaModifier))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))

    declTypeTraverser.traverse(declType)

    outputWriter.toString shouldBe
      """
        |private interface MyType<T> {
        |}
        |""".stripMargin
  }
}
