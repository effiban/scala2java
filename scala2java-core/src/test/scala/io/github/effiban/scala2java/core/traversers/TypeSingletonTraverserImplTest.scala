package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ThisRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Name, Term, Type}

class TypeSingletonTraverserImplTest extends UnitTestSuite {

  private val thisTraverser = mock[ThisTraverser]
  private val thisRenderer = mock[ThisRenderer]

  private val typeSingletonTraverser = new TypeSingletonTraverserImpl(thisTraverser, thisRenderer)

  test("traverse() for 'this'") {
    val `this` = Term.This(Name.Anonymous())
    val singletonType = Type.Singleton(`this`)

    doAnswer(`this`).when(thisTraverser).traverse(eqTree(`this`))

    typeSingletonTraverser.traverse(singletonType)

    verify(thisRenderer).render(eqTree(`this`))
  }

}
