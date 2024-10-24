package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Tree, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermApplyInfixQualifierImplTest extends UnitTestSuite {

  private val treeQualifier = mock[TreeQualifier]

  private val termApplyInfixQualifier = new TermApplyInfixQualifierImpl(treeQualifier)

  private val context = QualificationContext(List(importer"dummy.dummy"))

  test("qualify when fully populated should qualify everything except for the 'op'") {
    val termApplyInfix = q"a plus[T1, T2](b,c)"
    val expectedQualifiedTermApplyInfix = q"quala.a plus[qualT.T1, qualT.T2](qualb.b,qualc.c)"

    doAnswer((tree: Tree) => tree match {
      case q"a" => q"quala.a"
      case q"b" => q"qualb.b"
      case q"c" => q"qualc.c"
      case t"T1" => t"qualT.T1"
      case t"T2" => t"qualT.T2"
      case aTree => aTree
    }).when(treeQualifier).qualify(any[Tree], eqQualificationContext(context))

    val qualifiedTermApplyInfix = termApplyInfixQualifier.qualify(termApplyInfix, context)
    qualifiedTermApplyInfix.structure shouldBe expectedQualifiedTermApplyInfix.structure

    verify(treeQualifier, never).qualify(eqTree(q"plus"), eqQualificationContext(context))
  }

  test("qualify when has no types and one arg, should qualify only lhs and rhs") {
    val termApplyInfix = q"a plus b"
    val expectedQualifiedTermApplyInfix = q"quala.a plus qualb.b"

    doAnswer((tree: Tree) => tree match {
      case q"a" => q"quala.a"
      case q"b" => q"qualb.b"
      case aTree => aTree
    }).when(treeQualifier).qualify(any[Tree], eqQualificationContext(context))

    val qualifiedTermApplyInfix = termApplyInfixQualifier.qualify(termApplyInfix, context)
    qualifiedTermApplyInfix.structure shouldBe expectedQualifiedTermApplyInfix.structure

    verify(treeQualifier, never).qualify(eqTree(q"plus"), eqQualificationContext(context))
  }
}
