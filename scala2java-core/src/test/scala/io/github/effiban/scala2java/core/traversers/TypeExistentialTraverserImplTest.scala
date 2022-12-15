package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Decl, Type}

class TypeExistentialTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeExistentialTraverser = new TypeExistentialTraverserImpl(typeTraverser)

  test("traverse") {
    val typeA = Type.Name("A")
    val typeB = Type.Name("B")

    val stats = List(Decl.Type(mods = Nil, name = typeB, tparams = Nil, bounds = Type.Bounds(None, None)))

    val typeExistential = Type.Existential(tpe = typeA, stats = stats)

    doWrite("A").when(typeTraverser).traverse(eqTree(typeA))

    typeExistentialTraverser.traverse(typeExistential)

    outputWriter.toString shouldBe "A/* forSome List(type B) */"
  }

}
