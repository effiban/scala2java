package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArrayInitializerSizeContext
import io.github.effiban.scala2java.core.matchers.ArrayInitializerSizeContextMockitoMatcher.eqArrayInitializerSizeContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.New
import scala.meta.{XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class NewTraverserImplTest extends UnitTestSuite {

  private val initTraverser = mock[InitTraverser]
  private val arrayInitializerTraverser = mock[ArrayInitializerTraverser]
  private val arrayInitializerContextResolver = mock[ArrayInitializerContextResolver]

  private val newTraverser = new NewTraverserImpl(
    initTraverser,
    arrayInitializerTraverser,
    arrayInitializerContextResolver
  )


  test("traverse instantiation of 'MyClass'") {
    val init = init"MyClass(val1, val2)"
    val traversedInit = init"MyTraversedClass(val11, val22)"

    val `new` = New(init)
    val traversedNew = New(traversedInit)

    when(arrayInitializerContextResolver.tryResolve(`new`.init)).thenReturn(None)
    doReturn(traversedInit).when(initTraverser).traverse(eqTree(init))

    newTraverser.traverse(`new`).structure shouldBe traversedNew.structure
  }

  test("traverse instantiation of 'scala.Array[MyClass]'") {
    val init = init"scala.Array[MyClass](3)"
    val traversedInit = init"scala.Array[MyTraversedClass](33)"

    val `new` = New(init)
    val traversedNew = New(traversedInit)

    val expectedContext = ArrayInitializerSizeContext(tpe = t"MyClass", size = q"3")
    val expectedTraversedContext = ArrayInitializerSizeContext(tpe = t"MyTraversedClass", size = q"33")

    when(arrayInitializerContextResolver.tryResolve(`new`.init)).thenReturn(Some(expectedContext))
    doReturn(expectedTraversedContext).when(arrayInitializerTraverser).traverseWithSize(eqArrayInitializerSizeContext(expectedContext))

    newTraverser.traverse(`new`).structure shouldBe traversedNew.structure

  }
}
