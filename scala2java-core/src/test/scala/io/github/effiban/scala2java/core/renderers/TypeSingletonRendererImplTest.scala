package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Name, Term, Type, XtensionQuasiquoteTerm}

class TypeSingletonRendererImplTest extends UnitTestSuite {

  private val termRefRenderer = mock[TermRefRenderer]

  private val typeSingletonRenderer = new TypeSingletonRendererImpl(termRefRenderer)

  test("render() for 'this'") {
    val `this` = Term.This(Name.Anonymous())
    val singletonType = Type.Singleton(`this`)

    typeSingletonRenderer.render(singletonType)

    verify(termRefRenderer).render(eqTree(`this`))
  }

  test("render() for a non-'this' singleton type") {
    val termSelect = q"x.Y"
    val singletonType = Type.Singleton(termSelect)

    typeSingletonRenderer.render(singletonType)

    verify(termRefRenderer).render(eqTree(termSelect))
  }
}
