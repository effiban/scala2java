package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeSingletonRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Name, Term, Type, XtensionQuasiquoteTerm}

class TypeSingletonTraverserImplTest extends UnitTestSuite {

  private val thisTraverser = mock[ThisTraverser]
  private val typeSingletonRenderer = mock[TypeSingletonRenderer]

  private val typeSingletonTraverser = new TypeSingletonTraverserImpl(thisTraverser, typeSingletonRenderer)

  test("traverse() for 'this'") {
    val `this` = Term.This(Name.Anonymous())
    // It seems like only the anonymous name is valid in this case, but for now keeping the traverser so we need to test an actual transformation
    val traversedThis = Term.This(Name.Indeterminate("bla"))
    val singletonType = Type.Singleton(`this`)
    val traversedSingletonType = Type.Singleton(traversedThis)

    doAnswer(traversedThis).when(thisTraverser).traverse(eqTree(`this`))

    typeSingletonTraverser.traverse(singletonType)

    verify(typeSingletonRenderer).render(eqTree(traversedSingletonType))
  }

  test("traverse() for non-'this'") {
    val singletonType = Type.Singleton(q"x")

    typeSingletonTraverser.traverse(singletonType)

    verify(typeSingletonRenderer).render(eqTree(singletonType))
  }
}
