package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.Mod.Annot
import scala.meta.{Init, Name, Term, Type}

class TermAnnotateTraverserImplTest extends UnitTestSuite {

  private val annotListTraverser = mock[AnnotListTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val termAnnotateTraverser = new TermAnnotateTraverserImpl(annotListTraverser, expressionTermTraverser)

  test("traverse") {
    val annots = List(
      Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List())),
      Annot(Init(tpe = Type.Name("MyAnnot2"), name = Name.Anonymous(), argss = List()))
    )

    val termName = Term.Name("myName")

    val termAnnotate = Term.Annotate(expr = termName, annots = annots)

    doWrite("@MyAnnot1 @MyAnnot2 ")
      .when(annotListTraverser).traverseAnnotations(annotations = eqTreeList(annots), onSameLine = ArgumentMatchers.eq(true))

    doWrite("myName").when(expressionTermTraverser).traverse(eqTree(termName))

    termAnnotateTraverser.traverse(termAnnotate)

    outputWriter.toString shouldBe "(@MyAnnot1 @MyAnnot2 myName)"
  }

}
