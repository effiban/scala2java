package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeBounds

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