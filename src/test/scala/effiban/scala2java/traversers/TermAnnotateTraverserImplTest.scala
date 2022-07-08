package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Mod.Annot
import scala.meta.{Init, Name, Term, Type}

class TermAnnotateTraverserImplTest extends UnitTestSuite {

  private val annotListTraverser = mock[AnnotListTraverser]
  private val termTraverser = mock[TermTraverser]

  private val termAnnotateTraverser = new TermAnnotateTraverserImpl(annotListTraverser, termTraverser)

  test("traverse") {
    val annots = List(
      Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List())),
      Annot(Init(tpe = Type.Name("MyAnnot2"), name = Name.Anonymous(), argss = List()))
    )

    val termName = Term.Name("myName")

    val termAnnotate = Term.Annotate(expr = termName, annots = annots)

    doWrite("@MyAnnot1 @MyAnnot2 ")
      .when(annotListTraverser).traverseAnnotations(annotations = eqTreeList(annots), onSameLine = ArgumentMatchers.eq(true))

    doWrite("myName").when(termTraverser).traverse(eqTree(termName))

    termAnnotateTraverser.traverse(termAnnotate)

    outputWriter.toString shouldBe "(@MyAnnot1 @MyAnnot2 myName)"
  }

}
