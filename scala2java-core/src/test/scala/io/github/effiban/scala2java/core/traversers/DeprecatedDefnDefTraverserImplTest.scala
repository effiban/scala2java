package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.Decision.{Uncertain, Yes}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.matchers.ModListTraversalResultMockitoMatcher.eqModListTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers._
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.traversers.results.{ModListTraversalResult, TermParamTraversalResult}
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnDefTransformer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.Term.Block
import scala.meta.{Defn, Init, Mod, Name, Term, Type, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class DeprecatedDefnDefTraverserImplTest extends UnitTestSuite {

  private val MethodName = Term.Name("myMethod")

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val ScalaMods = List(TheAnnot)

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val TraversedTypeParam1 = tparam"T11"
  private val TraversedTypeParam2 = tparam"T22"
  private val TraversedTypeParams = List(TraversedTypeParam1, TraversedTypeParam2)

  private val MethodParam1 = param"param1: Int"
  private val MethodParam2 = param"param2: Int"
  private val MethodParam3 = param"param3: Int"
  private val MethodParam4 = param"param4: Int"

  private val MethodParamList1 = List(MethodParam1, MethodParam2)
  private val MethodParamList2 = List(MethodParam3, MethodParam4)

  private val TraversedMethodParam1 = param"param11: Int"
  private val TraversedMethodParam2 = param"param22: Int"
  private val TraversedMethodParam3 = param"param33: Int"
  private val TraversedMethodParam4 = param"param44: Int"

  private val TraversedMethodParamList1 = List(TraversedMethodParam1, TraversedMethodParam2)
  private val TraversedMethodParamList2 = List(TraversedMethodParam3, TraversedMethodParam4)

  private val Statement1 = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))
  private val Statement2 = Term.Apply(fun = Term.Name("doSomethingElse"), args = List(Term.Name("param2")))

  private val TraversedStatement1 = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param11")))
  private val TraversedStatement2 = Term.Apply(fun = Term.Name("doSomethingElse"), args = List(Term.Name("param22")))

  private val InitialDefnDef = Defn.Def(Nil, Term.Name("foo"), Nil, List(Nil), Some(TypeNames.Int), Term.Apply(Term.Name("bar"), Nil))

  private val modListTraverser = mock[ModListTraverser]
  private val modifiersRenderContextFactory = mock[ModifiersRenderContextFactory]
  private val modListRenderer = mock[ModListRenderer]
  private val typeParamTraverser = mock[TypeParamTraverser]
  private val typeParamListRenderer = mock[TypeParamListRenderer]
  private val termNameRenderer = mock[TermNameRenderer]
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val termParamTraverser = mock[TermParamTraverser]
  private val termParamListRenderer = mock[TermParamListRenderer]
  private val blockWrappingTermTraverser = mock[BlockWrappingTermTraverser]
  private val blockRenderer = mock[BlockRenderer]
  private val termTypeInferrer = mock[TermTypeInferrer]
  private val defnDefTransformer = mock[DefnDefTransformer]

  private val defnDefTraverser = new DeprecatedDefnDefTraverserImpl(
    modListTraverser,
    modifiersRenderContextFactory,
    modListRenderer,
    typeParamTraverser,
    typeParamListRenderer,
    termNameRenderer,
    typeTraverser,
    typeRenderer,
    termParamTraverser,
    termParamListRenderer,
    blockWrappingTermTraverser,
    blockRenderer,
    termTypeInferrer,
    defnDefTransformer)


  test("traverse() for class method with one statement returning int") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = Some(TypeNames.Int),
      body = Statement1
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(transformedDefnDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(t"int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("int").when(typeRenderer).render(eqTree(t"int"))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
        case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      }
      TermParamTraversalResult(traversedParam, List(JavaModifier.Final))
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1),
      context = eqTo(TermParamListRenderContext(List(JavaModifier.Final)))
    )
    val traversedBody = Block(List(TraversedStatement1))
    doReturn(traversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(Statement1),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(traversedBody),
      context = eqTo(BlockRenderContext())
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(final int param11, final int param22) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with one statement returning Unit") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = Some(TypeNames.Unit),
      body = Statement1
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(transformedDefnDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(t"void").when(typeTraverser).traverse(eqTree(TypeNames.Unit))
    doWrite("void").when(typeRenderer).render(eqTree(t"void"))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
        case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      }
      TermParamTraversalResult(traversedParam, List(JavaModifier.Final))
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1),
      context = eqTo(TermParamListRenderContext(List(JavaModifier.Final)))
    )
    val traversedBody = Block(List(TraversedStatement1))
    doReturn(traversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(Statement1),
      context = eqBlockContext(BlockContext())
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(traversedBody),
      context = eqTo(BlockRenderContext())
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public void myMethod(final int param11, final int param22) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with type params") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = TypeParams,
      paramss = List(MethodParamList1),
      decltpe = Some(TypeNames.Unit),
      body = Statement1
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(transformedDefnDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doAnswer((tparam: Type.Param) => tparam match {
      case aTypeParam if aTypeParam.structure == TypeParam1.structure => TraversedTypeParam1
      case aTypeParam if aTypeParam.structure == TypeParam2.structure => TraversedTypeParam2
      case aTypeParam => aTypeParam
    }).when(typeParamTraverser).traverse(any[Type.Param])
    doWrite("<T11, T22>").when(typeParamListRenderer).render(eqTreeList(TraversedTypeParams))
    doReturn(t"void").when(typeTraverser).traverse(eqTree(TypeNames.Unit))
    doWrite("void").when(typeRenderer).render(eqTree(t"void"))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
        case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      }
      TermParamTraversalResult(traversedParam, List(JavaModifier.Final))
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1),
      context = eqTo(TermParamListRenderContext(List(JavaModifier.Final)))
    )
    val traversedBody = Block(List(TraversedStatement1))
    doReturn(traversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(Statement1),
      context = eqBlockContext(BlockContext())
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(traversedBody),
      context = eqTo(BlockRenderContext())
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public <T11, T22> void myMethod(final int param11, final int param22) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with one statement missing return type when not inferrable") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = None,
      body = Statement1
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(transformedDefnDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    when(termTypeInferrer.infer(eqTree(Statement1))).thenReturn(None)
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
        case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      }
      TermParamTraversalResult(traversedParam, List(JavaModifier.Final))
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1),
      context = eqTo(TermParamListRenderContext(List(JavaModifier.Final)))
    )
    val traversedBody = Block(List(TraversedStatement1))
    doReturn(traversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(Statement1),
      context = eqBlockContext(BlockContext(shouldReturnValue = Uncertain))
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(traversedBody),
      context = eqTo(BlockRenderContext(uncertainReturn = true))
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public /* UnknownType */ myMethod(final int param11, final int param22) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with one statement missing return type when inferrable") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = None,
      body = Statement1
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(transformedDefnDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    when(termTypeInferrer.infer(eqTree(Statement1))).thenReturn(Some(TypeNames.String))
    doReturn(TypeNames.String).when(typeTraverser).traverse(eqTree(TypeNames.String))
    doWrite("String").when(typeRenderer).render(eqTree(TypeNames.String))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
        case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      }
      TermParamTraversalResult(traversedParam, List(JavaModifier.Final))
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1),
      context = eqTo(TermParamListRenderContext(List(JavaModifier.Final)))
    )
    val traversedBody = Block(List(TraversedStatement1))
    doReturn(traversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(Statement1),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(traversedBody),
      context = eqTo(BlockRenderContext())
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public String myMethod(final int param11, final int param22) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with block") {
    val javaScope = JavaScope.Class

    val body = Block(stats = List(Statement1, Statement2))

    val transformedDefnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = Some(TypeNames.Int),
      body = body
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(transformedDefnDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(t"int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("int").when(typeRenderer).render(eqTree(t"int"))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
        case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      }
      TermParamTraversalResult(traversedParam, List(JavaModifier.Final))
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1),
      context = eqTo(TermParamListRenderContext(List(JavaModifier.Final)))
    )
    val traversedBody = Block(List(TraversedStatement1, TraversedStatement2))
    doReturn(traversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(body),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(traversedBody),
      context = eqTo(BlockRenderContext())
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(final int param11, final int param22) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for interface method with one list of params") {
    val javaScope = JavaScope.Interface

    val transformedDefnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = Some(TypeNames.Int),
      body = Statement1
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Default))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Default))

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(transformedDefnDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |default """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(t"int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("int").when(typeRenderer).render(eqTree(t"int"))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
        case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      }
      TermParamTraversalResult(traversedParam)
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(int param11, int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1),
      context = eqTo(TermParamListRenderContext())
    )
    val traversedBody = Block(List(TraversedStatement1))
    doReturn(traversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(Statement1),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(traversedBody),
      context = eqTo(BlockRenderContext())
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |default int myMethod(int param11, int param22) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for interface method with two lists of params") {
    val javaScope = JavaScope.Interface

    val transformedDefnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1, MethodParamList2),
      decltpe = Some(TypeNames.Int),
      body = Statement1
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Default))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Default))

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(transformedDefnDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |default """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(t"int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("int").when(typeRenderer).render(eqTree(t"int"))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
        case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
        case aParam if aParam.structure == MethodParam3.structure => TraversedMethodParam3
        case aParam if aParam.structure == MethodParam4.structure => TraversedMethodParam4
      }
      TermParamTraversalResult(traversedParam)
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(int param11, int param22, int param33, int param44)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1 ++ TraversedMethodParamList2),
      context = eqTo(TermParamListRenderContext())
    )
    val traversedBody = Block(List(Statement1))
    doReturn(traversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(Statement1),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(traversedBody),
      context = eqTo(BlockRenderContext())
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |default int myMethod(int param11, int param22, int param33, int param44) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  private def eqExpectedScalaMods(defnDef: Defn.Def, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(defnDef, JavaTreeType.Method, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
