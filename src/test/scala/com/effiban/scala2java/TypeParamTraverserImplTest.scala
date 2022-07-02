package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import com.effiban.scala2java.testtrees.TypeBounds

import scala.meta.Type

class TypeParamTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val typeBoundsTraverser = mock[TypeBoundsTraverser]

  private val typeParamTraverser = new TypeParamTraverserImpl(
    nameTraverser,
    typeParamListTraverser,
    typeBoundsTraverser
  )


  test("testTraverse") {

    val nestedTypeParams = List(
      Type.Param(
        mods = Nil,
        name = Type.Name("K"),
        tparams = Nil,
        tbounds = TypeBounds.Empty,
        vbounds = Nil,
        cbounds = Nil
      ),
      Type.Param(
        mods = Nil,
        name = Type.Name("V"),
        tparams = Nil,
        tbounds = TypeBounds.Empty,
        vbounds = Nil,
        cbounds = Nil
      )
    )

    val mainTypeParamName = Type.Name("T")
    val mainTypeParamBounds = Type.Bounds(
      lo = None,
      hi = Some(Type.Name("Sortable"))
    )

    val mainTypeParam = Type.Param(
      mods = Nil,
      name = mainTypeParamName,
      tparams = nestedTypeParams,
      tbounds = mainTypeParamBounds,
      vbounds = Nil,
      cbounds = Nil
    )

    doWrite("T").when(nameTraverser).traverse(eqTree(mainTypeParamName))
    doWrite("<K, V>").when(typeParamListTraverser).traverse(eqTreeList(nestedTypeParams))
    doWrite(" extends Sortable").when(typeBoundsTraverser).traverse(eqTree(mainTypeParamBounds))

    typeParamTraverser.traverse(mainTypeParam)

    outputWriter.toString shouldBe "T<K, V> extends Sortable"
  }

}
