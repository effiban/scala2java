package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Mod.Annot
import scala.meta.{Term, XtensionQuasiquoteMod, XtensionQuasiquoteTerm}

class TermAnnotateTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val annotTraverser = mock[AnnotTraverser]

  private val termAnnotateTraverser = new TermAnnotateTraverserImpl(expressionTermTraverser, annotTraverser)

  test("traverse") {
    val expr = q"myName"
    val annot1 = mod"@MyAnnot1"
    val annot2 = mod"@MyAnnot2"

    val traversedExpr = q"myTraversedName"
    val traversedAnnot1 = mod"@MyTraversedAnnot1"
    val traversedAnnot2 = mod"@MyTraversedAnnot2"

    val termAnnotate = Term.Annotate(expr, List(annot1, annot2))
    val traversedTermAnnotate = Term.Annotate(traversedExpr, List(traversedAnnot1, traversedAnnot2))

    doReturn(traversedExpr).when(expressionTermTraverser).traverse(eqTree(expr))
    doAnswer((annot: Annot) => annot match {
      case anAnnot if anAnnot.structure == annot1.structure => traversedAnnot1
      case anAnnot if anAnnot.structure == annot2.structure => traversedAnnot2
      case anAnnot => anAnnot
    }).when(annotTraverser).traverse(any[Annot])

    termAnnotateTraverser.traverse(termAnnotate).structure shouldBe traversedTermAnnotate.structure
  }

}
