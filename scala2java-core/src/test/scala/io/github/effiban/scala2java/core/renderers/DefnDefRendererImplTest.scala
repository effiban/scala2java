package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{BlockRenderContext, DefRenderContext, ModifiersRenderContext, TermParamListRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.ModifiersRenderContextMockitoMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.Block
import scala.meta.{Defn, Init, Mod, Name, Term, Type, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class DefnDefRendererImplTest extends UnitTestSuite {

  private val MethodName = Term.Name("myMethod")

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val ScalaMods = List(TheAnnot)

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val MethodParam1 = param"param1: Int"
  private val MethodParam2 = param"param2: Int"
  private val MethodParam3 = param"param3: Int"
  private val MethodParam4 = param"param4: Int"

  private val MethodParamList1 = List(MethodParam1, MethodParam2)
  private val MethodParamList2 = List(MethodParam3, MethodParam4)

  private val Statement1 = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))
  private val Statement2 = Term.Apply(fun = Term.Name("doSomethingElse"), args = List(Term.Name("param2")))
  private val Body = Block(List(Statement1, Statement2))

  private val modListRenderer = mock[ModListRenderer]
  private val typeParamListRenderer = mock[TypeParamListRenderer]
  private val termNameRenderer = mock[TermNameRenderer]
  private val typeRenderer = mock[TypeRenderer]
  private val termParamListRenderer = mock[TermParamListRenderer]
  private val blockCoercingTermRenderer = mock[BlockCoercingTermRenderer]

  private val defnDefRenderer = new DefnDefRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    termNameRenderer,
    typeRenderer,
    termParamListRenderer,
    blockCoercingTermRenderer
  )


  test("render() for class method returning int") {
    val defnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = Some(t"int"),
      body = Body
    )
    val defRenderContext = DefRenderContext(javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int").when(typeRenderer).render(eqTree(t"int"))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doWrite("(final int param1, final int param2)").when(termParamListRenderer).render(
      termParams = eqTreeList(MethodParamList1),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockCoercingTermRenderer).render(term = eqTree(Body),
      context = eqTo(BlockRenderContext())
    )

    defnDefRenderer.render(defnDef, defRenderContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(final int param1, final int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render() for class method with no return type at all (Java primary ctor) ") {
    val defnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = Some(Type.AnonymousName()),
      body = Body
    )
    val defRenderContext = DefRenderContext(javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doWrite("(final int param1, final int param2)").when(termParamListRenderer).render(
      termParams = eqTreeList(MethodParamList1),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockCoercingTermRenderer).render(term = eqTree(Body),
      context = eqTo(BlockRenderContext())
    )

    defnDefRenderer.render(defnDef, defRenderContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public myMethod(final int param1, final int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render() for class method with unknown return type") {
    val defnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = None,
      body = Body
    )
    val defRenderContext = DefRenderContext(javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doWrite("(final int param1, final int param2)").when(termParamListRenderer).render(
      termParams = eqTreeList(MethodParamList1),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockCoercingTermRenderer).render(term = eqTree(Body),
      context = eqTo(BlockRenderContext(uncertainReturn = true))
    )

    defnDefRenderer.render(defnDef, defRenderContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public /* UnknownType */ myMethod(final int param1, final int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render() for class method with type params") {
    val defnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = TypeParams,
      paramss = List(MethodParamList1),
      decltpe = Some(t"void"),
      body = Body
    )
    val defRenderContext = DefRenderContext(javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("<T11, T22>").when(typeParamListRenderer).render(eqTreeList(TypeParams))
    doWrite("void").when(typeRenderer).render(eqTree(t"void"))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doWrite("(final int param1, final int param2)").when(termParamListRenderer).render(
      termParams = eqTreeList(MethodParamList1),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockCoercingTermRenderer).render(term = eqTree(Body),
      context = eqTo(BlockRenderContext())
    )

    defnDefRenderer.render(defnDef, defRenderContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public <T11, T22> void myMethod(final int param1, final int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render() for interface method with one list of params") {
    val defnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = Some(t"int"),
      body = Body
    )
    val defRenderContext = DefRenderContext(javaModifiers = List(JavaModifier.Default))

    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Default))
    doWrite(
      """@MyAnnotation
        |default """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int").when(typeRenderer).render(eqTree(t"int"))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListRenderer).render(
      termParams = eqTreeList(MethodParamList1),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockCoercingTermRenderer).render(term = eqTree(Body),
      context = eqTo(BlockRenderContext())
    )

    defnDefRenderer.render(defnDef, defRenderContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |default int myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render() for interface method with two lists of params") {
    val defnDef = Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1, MethodParamList2),
      decltpe = Some(t"int"),
      body = Body
    )
    val defRenderContext = DefRenderContext(javaModifiers = List(JavaModifier.Default))

    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Default))
    doWrite(
      """@MyAnnotation
        |default """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int").when(typeRenderer).render(eqTree(t"int"))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doWrite("(int param1, int param2, int param3, int param4)").when(termParamListRenderer).render(
      termParams = eqTreeList(MethodParamList1 ++ MethodParamList2),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockCoercingTermRenderer).render(term = eqTree(Body),
      context = eqTo(BlockRenderContext())
    )

    defnDefRenderer.render(defnDef, defRenderContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |default int myMethod(int param1, int param2, int param3, int param4) {
        |  /* BODY */
        |}
        |""".stripMargin
  }
}
