package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Template, Tree, XtensionQuasiquoteInit, XtensionQuasiquoteSelf, XtensionQuasiquoteTerm}

class TemplateTransformerImplTest extends UnitTestSuite {

  private val treeTransformer = mock[TreeTransformer]

  private val templateTransformer = new TemplateTransformerImpl(treeTransformer)

  test("transform") {

    val init1 = init"A"
    val init2 = init"B"

    val transformedInit1 = init"AA"
    val transformedInit2 = init"BB"

    val self = self"C"

    val transformedSelf = self"CC"

    val stat1 = q"def fun(): scala.Int = 3"
    val stat2 = q"var x: scala.Int = 2"

    val transformedStat1 = q"def fun2(): int = 3"
    val transformedStat2 = q"var xx: int = 2"

    val template = Template(
      early = Nil,
      inits = List(init1, init2),
      self = self,
      stats = List(stat1, stat2)
    )
    val transformedTemplate = Template(
      early = Nil,
      inits = List(transformedInit1, transformedInit2),
      self = transformedSelf,
      stats = List(transformedStat1, transformedStat2)
    )

    doAnswer((tree: Tree) => tree match {
      case aTree if aTree.structure == init1.structure => transformedInit1
      case aTree if aTree.structure == init2.structure => transformedInit2
      case aTree if aTree.structure == self.structure => transformedSelf
      case aTree if aTree.structure == stat1.structure => transformedStat1
      case aTree if aTree.structure == stat2.structure => transformedStat2
      case aTree => aTree
    }).when(treeTransformer).transform(any[Tree])

    templateTransformer.transform(template).structure shouldBe transformedTemplate.structure
  }

}
