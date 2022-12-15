package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerSizeContext, InitContext}
import io.github.effiban.scala2java.core.matchers.ArrayInitializerSizeContextMockitoMatcher.eqArrayInitializerSizeContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.Term.New
import scala.meta.{Init, Lit, Name, Term, Type}

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
    val init = Init(
      tpe = Type.Name("MyClass"),
      name = Name.Anonymous(),
      argss = List(List(Term.Name("val1"), Term.Name("val2")))
    )

    val `new` = New(init)

    when(arrayInitializerContextResolver.tryResolve(`new`.init)).thenReturn(None)
    doWrite("MyClass(val1, val2)")
      .when(initTraverser).traverse(eqTree(init), ArgumentMatchers.eq(InitContext(traverseEmpty = true, argNameAsComment = true)))

    newTraverser.traverse(`new`)

    outputWriter.toString shouldBe "new MyClass(val1, val2)"
  }

  test("traverse instantiation of 'Array'") {
    val init = Init(
      tpe = Type.Apply(TypeNames.ScalaArray, List(TypeNames.String)),
      name = Name.Anonymous(),
      argss = List(List(Lit.Int(3)))
    )
    val `new` = New(init)

    val expectedContext = ArrayInitializerSizeContext(tpe = TypeNames.String, size = Lit.Int(3))

    when(arrayInitializerContextResolver.tryResolve(`new`.init)).thenReturn(Some(expectedContext))

    newTraverser.traverse(`new`)

    verify(arrayInitializerTraverser).traverseWithSize(eqArrayInitializerSizeContext(expectedContext))
  }
}
