package effiban.scala2java.traversers

import effiban.scala2java.contexts.{ArrayInitializerSizeContext, InitContext}
import effiban.scala2java.matchers.ArrayInitializerSizeContextMockitoMatcher.eqArrayInitializerSizeContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.ArrayInitializerContextResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
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
