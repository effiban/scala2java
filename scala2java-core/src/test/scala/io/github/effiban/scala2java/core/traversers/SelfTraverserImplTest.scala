package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.SelfRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Name, Self, Type}

class SelfTraverserImplTest extends UnitTestSuite {

  private val selfRenderer = mock[SelfRenderer]

  private val selfTraverser = new SelfTraverserImpl(selfRenderer)

  test("traverse") {
    val `self` = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(Type.Name("SelfType")))

    selfTraverser.traverse(`self`)

    verify(selfRenderer).render(`self`)
  }
}
