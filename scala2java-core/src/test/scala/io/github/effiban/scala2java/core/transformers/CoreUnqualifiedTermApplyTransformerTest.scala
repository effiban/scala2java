package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TypeClassifier
import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.matchers.UnqualifiedTermApplyScalatestMatcher.equalUnqualifiedTermApply
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.contexts.UnqualifiedTermApplyTransformationContext
import io.github.effiban.scala2java.spi.entities.UnqualifiedTermApply
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteTerm}

class CoreUnqualifiedTermApplyTransformerTest extends UnitTestSuite {

  private val typeClassifier = mock[TypeClassifier[Type]]

  private val unqualifiedTermApplyTransformer = new CoreUnqualifiedTermApplyTransformer(typeClassifier)

  test("transform 'take(2)' with a JavaList-like qualifier, should return 'subList(0, 2)'") {
    val arg = q"2"
    val unqualifiedTermApply = UnqualifiedTermApply(q"take", List(arg))
    val qualifierType = TypeSelects.ScalaList
    val context = UnqualifiedTermApplyTransformationContext(maybeQualifierType = Some(qualifierType))
    val expectedJavaUnqualifiedTermApply = UnqualifiedTermApply(q"subList", List(q"0", arg))

    when(typeClassifier.isJavaListLike(eqTree(qualifierType))).thenReturn(true)

    unqualifiedTermApplyTransformer.transform(unqualifiedTermApply, context).value should
      equalUnqualifiedTermApply(expectedJavaUnqualifiedTermApply)
  }

  test("transform 'length()' with a JavaList-like qualifier, should return 'size()'") {
    val unqualifiedTermApply = UnqualifiedTermApply(q"length")
    val qualifierType = TypeSelects.ScalaList
    val context = UnqualifiedTermApplyTransformationContext(maybeQualifierType = Some(qualifierType))
    val expectedJavaUnqualifiedTermApply = UnqualifiedTermApply(q"size")

    when(typeClassifier.isJavaListLike(eqTree(qualifierType))).thenReturn(true)

    unqualifiedTermApplyTransformer.transform(unqualifiedTermApply, context).value should
      equalUnqualifiedTermApply(expectedJavaUnqualifiedTermApply)
  }

  test("transform 'foreach(print(_))' should return 'forEach(print(_))'") {
    val unqualifiedTermApply = UnqualifiedTermApply(q"foreach", List(q"print(_)"))
    val expectedJavaUnqualifiedTermApply = UnqualifiedTermApply(q"forEach", List(q"(print(_))"))

    unqualifiedTermApplyTransformer.transform(unqualifiedTermApply, UnqualifiedTermApplyTransformationContext()).value should
      equalUnqualifiedTermApply(expectedJavaUnqualifiedTermApply)
  }
}
