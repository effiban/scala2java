package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.Decision.{Uncertain, Yes}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnDefTransformer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.Term.Block
import scala.meta.Type.Bounds
import scala.meta.{Defn, Init, Mod, Name, Term, Type}

class DefnDefTraverserImplTest extends UnitTestSuite {

  private val MethodName = Term.Name("myMethod")
  private val ClassName = Term.Name("MyClass")

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val Modifiers = List(TheAnnot)

  private val TypeParams = List(
    Type.Param(
      mods = List(),
      name = Type.Name("T"),
      tparams = List(),
      tbounds = Bounds(lo = None, hi = None),
      vbounds = List(),
      cbounds = List()
    )
  )

  private val MethodParams1 = List(
    termParamInt("param1"),
    termParamInt("param2")
  )
  private val MethodParams2 = List(
    termParamInt("param3"),
    termParamInt("param4")
  )

  private val Statement1 = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))
  private val Statement2 = Term.Apply(fun = Term.Name("doSomethingElse"), args = List(Term.Name("param2")))

  private val InitialDefnDef = Defn.Def(Nil, Term.Name("foo"), Nil, List(Nil), Some(TypeNames.Int), Term.Apply(Term.Name("bar"), Nil))

  private val modListTraverser = mock[ModListTraverser]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val termParamListTraverser = mock[TermParamListTraverser]
  private val blockTraverser = mock[BlockTraverser]
  private val termTypeInferrer = mock[TermTypeInferrer]
  private val defnDefTransformer = mock[DefnDefTransformer]

  private val defnDefTraverser = new DefnDefTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    termNameTraverser,
    typeTraverser,
    termParamListTraverser,
    blockTraverser,
    termTypeInferrer,
    defnDefTransformer)


  test("traverse() for class method with one statement returning int") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = Some(TypeNames.Int),
      body = Statement1
    )

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement1),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with one statement returning Unit") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = Some(TypeNames.Unit),
      body = Statement1
    )

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("void").when(typeTraverser).traverse(eqTree(TypeNames.Unit))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement1), context = eqBlockContext(BlockContext())
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public void myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with type params") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = TypeParams,
      paramss = List(MethodParams1),
      decltpe = Some(TypeNames.Unit),
      body = Statement1
    )

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("void").when(typeTraverser).traverse(eqTree(TypeNames.Unit))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement1), context = eqBlockContext(BlockContext())
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public <T> void myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for constructor") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = Modifiers,
      name = ClassName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = Some(Type.AnonymousName()),
      body = Statement1
    )

    val init = Init(
      tpe = Type.Singleton(Term.This(qual = Name.Anonymous())),
      name = Name.Anonymous(),
      argss = List(List(Term.Name("superParam1")))
    )

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("").when(typeTraverser).traverse(eqTree(TypeNames.Unit))
    doWrite("MyClass").when(termNameTraverser).traverse(eqTree(ClassName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement1),
      context = eqBlockContext(BlockContext(maybeInit = Some(init)))
    )

    defnDefTraverser.traverse(defnDef = InitialDefnDef, DefnDefContext(javaScope = javaScope, maybeInit = Some(init)))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public MyClass(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with one statement missing return type when not inferrable") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = None,
      body = Statement1
    )

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    when(termTypeInferrer.infer(eqTree(Statement1))).thenReturn(None)
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement1),
      context = eqBlockContext(BlockContext(shouldReturnValue = Uncertain))
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public /* UnknownType */ myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with one statement missing return type when inferrable") {
    val javaScope = JavaScope.Class

    val transformedDefnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = None,
      body = Statement1
    )

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    when(termTypeInferrer.infer(eqTree(Statement1))).thenReturn(Some(TypeNames.String))
    doWrite("String").when(typeTraverser).traverse(eqTree(TypeNames.String))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement1),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public String myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with block") {
    val javaScope = JavaScope.Class

    val body = Block(stats = List(Statement1, Statement2))

    val transformedDefnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = Some(TypeNames.Int),
      body = body
    )

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(body),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for interface method with one list of params") {
    val javaScope = JavaScope.Interface

    val transformedDefnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = Some(TypeNames.Int),
      body = Statement1
    )

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doWrite(
      """@MyAnnotation
        |default """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement1),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |default int myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for interface method with two lists of params") {
    val javaScope = JavaScope.Interface

    val transformedDefnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1, MethodParams2),
      decltpe = Some(TypeNames.Int),
      body = Statement1
    )

    when(defnDefTransformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)
    doWrite(
      """@MyAnnotation
        |default """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnDef, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2, int param3, int param4)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1 ++ MethodParams2),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement1),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    defnDefTraverser.traverse(InitialDefnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |default int myMethod(int param1, int param2, int param3, int param4) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  private def termParamInt(name: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(TypeNames.Int), default = None)
  }

  private def eqExpectedModifiers(defnDef: Defn.Def, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(defnDef, JavaTreeType.Method, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
