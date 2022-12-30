package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.typeinferrers.ApplyTypeTypeInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteTerm}

class CompositeApplyTypeTypeInferrerTest extends UnitTestSuite {

  private val TheTermApplyType = q"myTerm[D]"
  private val TheType = Type.Name("MyType")

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  private val coreInferrer = mock[ApplyTypeTypeInferrer]
  private val extensionInferrer1 = mock[ApplyTypeTypeInferrer]
  private val extensionInferrer2 = mock[ApplyTypeTypeInferrer]

  private val compositeInferrer = new CompositeApplyTypeTypeInferrer(coreInferrer)

  test("infer when there are no extension inferrers and core inferrer returns None - should return None") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(Nil)
    when(coreInferrer.infer(TheTermApplyType)).thenReturn(None)

    compositeInferrer.infer(TheTermApplyType) shouldBe None
  }

  test("infer when there are no extension inferrers and core inferrer returns a type - should return it") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(Nil)
    when(coreInferrer.infer(TheTermApplyType)).thenReturn(Some(TheType))

    compositeInferrer.infer(TheTermApplyType).value.structure shouldBe TheType.structure
  }

  test("infer when there is one extension inferrer returning None, and core returns a type - should return it") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(List(extensionInferrer1))
    when(extensionInferrer1.infer(eqTree(TheTermApplyType))).thenReturn(None)
    when(coreInferrer.infer(eqTree(TheTermApplyType))).thenReturn(Some(TheType))

    compositeInferrer.infer(TheTermApplyType).value.structure shouldBe TheType.structure
  }

  test("infer when there is one extension inferrer returning a type - should return it") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(List(extensionInferrer1))
    when(extensionInferrer1.infer(eqTree(TheTermApplyType))).thenReturn(Some(TheType))

    compositeInferrer.infer(TheTermApplyType).value.structure shouldBe TheType.structure
  }

  test("infer when there is one extension inferrer returning a type - should not invoke the core inferrer") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(List(extensionInferrer1))
    when(extensionInferrer1.infer(eqTree(TheTermApplyType))).thenReturn(Some(TheType))

    compositeInferrer.infer(TheTermApplyType)

    verifyNoMoreInteractions(coreInferrer)
  }

  test("infer when there are two extension inferrers, both returning None, and the core also returns None - should return None") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(List(extensionInferrer1, extensionInferrer2))
    when(extensionInferrer1.infer(eqTree(TheTermApplyType))).thenReturn(None)
    when(extensionInferrer2.infer(eqTree(TheTermApplyType))).thenReturn(None)
    when(coreInferrer.infer(eqTree(TheTermApplyType))).thenReturn(None)

    compositeInferrer.infer(TheTermApplyType) shouldBe None
  }

  test("infer when there are two extension inferrers, both returning None, and the core returns a type - should return None") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(List(extensionInferrer1, extensionInferrer2))
    when(extensionInferrer1.infer(eqTree(TheTermApplyType))).thenReturn(None)
    when(extensionInferrer2.infer(eqTree(TheTermApplyType))).thenReturn(None)
    when(coreInferrer.infer(eqTree(TheTermApplyType))).thenReturn(Some(TheType))

    compositeInferrer.infer(TheTermApplyType).value.structure shouldBe TheType.structure
  }

  test("infer when there are two extension inferrers, first returns None and second returns a type - should return it") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(List(extensionInferrer1, extensionInferrer2))
    when(extensionInferrer1.infer(eqTree(TheTermApplyType))).thenReturn(None)
    when(extensionInferrer2.infer(eqTree(TheTermApplyType))).thenReturn(Some(TheType))

    compositeInferrer.infer(TheTermApplyType).value.structure shouldBe TheType.structure
  }

  test("infer when there are two extension inferrers, first returns None and second returns a type - should not invoke the core inferrer") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(List(extensionInferrer1, extensionInferrer2))
    when(extensionInferrer1.infer(eqTree(TheTermApplyType))).thenReturn(None)
    when(extensionInferrer2.infer(eqTree(TheTermApplyType))).thenReturn(Some(TheType))

    compositeInferrer.infer(TheTermApplyType)

    verifyNoMoreInteractions(coreInferrer)
  }

  test("infer when there are two extension inferrers and first returns a type - should return it") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(List(extensionInferrer1, extensionInferrer2))
    when(extensionInferrer1.infer(eqTree(TheTermApplyType))).thenReturn(Some(TheType))

    compositeInferrer.infer(TheTermApplyType).value.structure shouldBe TheType.structure
  }

  test("infer when there are two extension inferrers and first returns a type - should not invoke any other inferrer") {
    when(extensionRegistry.applyTypeTypeInferrers).thenReturn(List(extensionInferrer1, extensionInferrer2))
    when(extensionInferrer1.infer(eqTree(TheTermApplyType))).thenReturn(Some(TheType))

    compositeInferrer.infer(TheTermApplyType)

    verifyNoMoreInteractions(extensionInferrer2, coreInferrer)
  }
}
