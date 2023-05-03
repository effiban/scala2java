package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Name, Term, Type, XtensionQuasiquoteTerm}

class TypeSingletonTraverserImplTest extends UnitTestSuite {

  private val thisTraverser = mock[ThisTraverser]

  private val typeSingletonTraverser = new TypeSingletonTraverserImpl(thisTraverser)

  test("traverse() for 'this'") {
    val `this` = Term.This(Name.Anonymous())
    // It seems like only the anonymous name is valid in this case, but for now keeping the traverser so we need to test an actual transformation
    val traversedThis = Term.This(Name.Indeterminate("bla"))
    val singletonType = Type.Singleton(`this`)
    val traversedSingletonType = Type.Singleton(traversedThis)

    doAnswer(traversedThis).when(thisTraverser).traverse(eqTree(`this`))

    typeSingletonTraverser.traverse(singletonType).structure shouldBe traversedSingletonType.structure
  }

  test("traverse() for non-'this'") {
    val singletonType = Type.Singleton(q"x")

    typeSingletonTraverser.traverse(singletonType).structure shouldBe singletonType.structure
  }
}
