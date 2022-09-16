package effiban.scala2java.traversers

import effiban.scala2java.contexts.{BlockContext, DefnDefContext, JavaModifiersContext}
import effiban.scala2java.entities.Decision.Yes
import effiban.scala2java.entities.JavaTreeType.Interface
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.matchers.BlockContextMatcher.eqBlockContext
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.typeinference.TermTypeInferrer
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

  private val annotListTraverser = mock[AnnotListTraverser]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val termParamListTraverser = mock[TermParamListTraverser]
  private val blockTraverser = mock[BlockTraverser]
  private val termTypeInferrer = mock[TermTypeInferrer]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val defnDefTraverser = new DefnDefTraverserImpl(
    annotListTraverser,
    typeParamListTraverser,
    termNameTraverser,
    typeTraverser,
    termParamListTraverser,
    blockTraverser,
    termTypeInferrer,
    javaModifiersResolver)

  test("traverse() for class method with one statement returning int") {
    javaScope = JavaTreeType.Class

    val defnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = Some(TypeNames.Int),
      body = Statement1
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(defnDef).thenReturn(List(JavaModifier.Public))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
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

    defnDefTraverser.traverse(defnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with one statement returning Unit") {
    javaScope = JavaTreeType.Class

    val defnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = Some(TypeNames.Unit),
      body = Statement1
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(defnDef).thenReturn(List(JavaModifier.Public))
    doWrite("void").when(typeTraverser).traverse(eqTree(TypeNames.Unit))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement1), context = eqBlockContext(BlockContext())
    )

    defnDefTraverser.traverse(defnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public void myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with type params") {
    javaScope = JavaTreeType.Class

    val defnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = TypeParams,
      paramss = List(MethodParams1),
      decltpe = Some(TypeNames.Unit),
      body = Statement1
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(defnDef).thenReturn(List(JavaModifier.Public))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("void").when(typeTraverser).traverse(eqTree(TypeNames.Unit))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      onSameLine = ArgumentMatchers.eq(false)
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement1), context = eqBlockContext(BlockContext())
    )

    defnDefTraverser.traverse(defnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public <T> void myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for constructor") {
    javaScope = JavaTreeType.Class

    val defnDef = Defn.Def(
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

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(defnDef).thenReturn(List(JavaModifier.Public))
    doWrite("").when(typeTraverser).traverse(eqTree(TypeNames.Unit))
    doWrite("MyClass").when(termNameTraverser).traverse(eqTree(ClassName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
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

    defnDefTraverser.traverse(defnDef = defnDef, DefnDefContext(javaScope = javaScope, maybeInit = Some(init)))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public MyClass(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with one statement missing return type when not inferrable") {
    javaScope = JavaTreeType.Class

    val defnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = None,
      body = Statement1
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(defnDef).thenReturn(List(JavaModifier.Public))
    when(termTypeInferrer.infer(eqTree(Statement1))).thenReturn(None)
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
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

    defnDefTraverser.traverse(defnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public /* UnknownType */ myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with one statement missing return type when inferrable") {
    javaScope = JavaTreeType.Class

    val defnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = None,
      body = Statement1
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(defnDef).thenReturn(List(JavaModifier.Public))
    when(termTypeInferrer.infer(eqTree(Statement1))).thenReturn(Some(TypeNames.String))
    doWrite("String").when(typeTraverser).traverse(eqTree(TypeNames.String))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
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

    defnDefTraverser.traverse(defnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public String myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for class method with block") {
    javaScope = JavaTreeType.Class

    val body = Block(stats = List(Statement1, Statement2))

    val defnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = Some(TypeNames.Int),
      body = body
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(defnDef).thenReturn(List(JavaModifier.Public))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
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

    defnDefTraverser.traverse(defnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public int myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for interface method with one list of params") {
    javaScope = Interface

    val defnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = Some(TypeNames.Int),
      body = Statement1
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(defnDef).thenReturn(List(JavaModifier.Default))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
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

    defnDefTraverser.traverse(defnDef, DefnDefContext(javaScope = javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |default int myMethod(int param1, int param2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for interface method with two lists of params") {
    javaScope = Interface

    val defnDef = Defn.Def(
      mods = Modifiers,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1, MethodParams2),
      decltpe = Some(TypeNames.Int),
      body = Statement1
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(defnDef).thenReturn(List(JavaModifier.Default))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(MethodName))
    doWrite("(int param1, int param2, int param3, int param4)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1 ++ MethodParams2),
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

    defnDefTraverser.traverse(defnDef, DefnDefContext(javaScope = javaScope))

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

  private def whenResolveJavaModifiers(defnDef: Defn.Def) = {
    val expectedJavaModifiersContext = JavaModifiersContext(defnDef, Modifiers, JavaTreeType.Method, javaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedJavaModifiersContext)))
  }
}
