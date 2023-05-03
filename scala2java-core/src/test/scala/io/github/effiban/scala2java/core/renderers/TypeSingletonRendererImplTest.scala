package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Name, Term, Type}

class TypeSingletonRendererImplTest extends UnitTestSuite {

  private val thisRenderer = mock[ThisRenderer]

  private val typeSingletonRenderer = new TypeSingletonRendererImpl(thisRenderer)

  test("render() for 'this'") {
    val `this` = Term.This(Name.Anonymous())
    val singletonType = Type.Singleton(`this`)

    doAnswer(`this`).when(thisRenderer).render(eqTree(`this`))

    typeSingletonRenderer.render(singletonType)

    verify(thisRenderer).render(eqTree(`this`))
  }

}
