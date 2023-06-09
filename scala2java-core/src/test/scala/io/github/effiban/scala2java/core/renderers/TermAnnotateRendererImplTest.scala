package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.Mod.Annot
import scala.meta.{Init, Name, Term, Type}

class TermAnnotateRendererImplTest extends UnitTestSuite {

  private val annotListRenderer = mock[AnnotListRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val termAnnotateRenderer = new TermAnnotateRendererImpl(annotListRenderer, expressionTermRenderer)

  test("render") {
    val annots = List(
      Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List())),
      Annot(Init(tpe = Type.Name("MyAnnot2"), name = Name.Anonymous(), argss = List()))
    )

    val termName = Term.Name("myName")

    val termAnnotate = Term.Annotate(expr = termName, annots = annots)

    doWrite("@MyAnnot1 @MyAnnot2 ")
      .when(annotListRenderer).render(annotations = eqTreeList(annots), onSameLine = ArgumentMatchers.eq(true))

    doWrite("myName").when(expressionTermRenderer).render(eqTree(termName))

    termAnnotateRenderer.render(termAnnotate)

    outputWriter.toString shouldBe "(@MyAnnot1 @MyAnnot2 myName)"
  }

}
