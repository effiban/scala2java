package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeAnonymousParamRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Mod, Type}

class TypeAnonymousParamTraverserImplTest extends UnitTestSuite {

  private val typeAnonymousParamRenderer = mock[TypeAnonymousParamRenderer]
  private val typeAnonymousParamTraverser = new TypeAnonymousParamTraverserImpl(typeAnonymousParamRenderer)

  test("traverse") {
    val typeAnonParam = Type.AnonymousParam(Some(Mod.Covariant()))

    typeAnonymousParamTraverser.traverse(typeAnonParam)

    verify(typeAnonymousParamRenderer).render(eqTree(typeAnonParam))
  }

}
