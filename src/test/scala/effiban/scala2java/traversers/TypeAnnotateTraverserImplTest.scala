package effiban.scala2java.traversers

import effiban.scala2java.UnitTestSuite
import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import org.mockito.ArgumentMatchers

import scala.meta.Mod.Annot
import scala.meta.{Init, Name, Type}

class TypeAnnotateTraverserImplTest extends UnitTestSuite {

  private val Annot1 = Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List()))
  private val Annot2 = Annot(Init(tpe = Type.Name("MyAnnot2"), name = Name.Anonymous(), argss = List()))
  private val Annots = List(Annot1, Annot2)

  private val TheType = Type.Name("T")

  private val annotListTraverser = mock[AnnotListTraverser]
  private val typeTraverser = mock[TypeTraverser]

  private val typeAnnotateTraverser = new TypeAnnotateTraverserImpl(annotListTraverser, typeTraverser)

  test("traverse") {
    val typeAnnotate = Type.Annotate(tpe = TheType, annots = List(Annot1, Annot2))

    doWrite("@MyAnnot1 @MyAnnot2")
      .when(annotListTraverser).traverseAnnotations(annotations = eqTreeList(Annots), onSameLine = ArgumentMatchers.eq(true))
    doWrite("T").when(typeTraverser).traverse(eqTree(TheType))

    typeAnnotateTraverser.traverse(typeAnnotate)

    outputWriter.toString shouldBe "@MyAnnot1 @MyAnnot2 T"
  }

}
