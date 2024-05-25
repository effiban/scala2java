package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.TypeTupleToTypeApplyTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeTupleTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeTupleTraverser = new TypeTupleTraverserImpl(typeTraverser)

  test("traverse") {
    val typeTuple = t"(T1, T2, T3)"
    val traversedTypeTuple = t"(T11, T22, T33)"

    doAnswer((arg: Type) => arg match {
      case t"T1" => t"T11"
      case t"T2" => t"T22"
      case t"T3" => t"T33"
      case other => other
    }).when(typeTraverser).traverse(any[Type])

    typeTupleTraverser.traverse(typeTuple).structure shouldBe traversedTypeTuple.structure
  }

}
