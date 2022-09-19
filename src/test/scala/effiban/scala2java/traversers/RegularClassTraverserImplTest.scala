package effiban.scala2java.traversers

import effiban.scala2java.contexts._
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import effiban.scala2java.matchers.TemplateContextMatcher.eqTemplateContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.transformers.ParamToDeclValTransformer
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any

import scala.meta.Mod.{Final, Private}
import scala.meta.Term.Block
import scala.meta.Type.Bounds
import scala.meta.{Ctor, Decl, Defn, Init, Mod, Name, Pat, Self, Template, Term, Type}

class RegularClassTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val Modifiers: List[Mod.Annot] = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )

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

  private val Arg1Name = "arg1"
  private val Arg2Name = "arg2"
  private val Arg3Name = "arg3"
  private val Arg4Name = "arg4"

  private val IntTypeName = "Int"

  private val CtorArg1 = termParam(Arg1Name, IntTypeName)
  private val CtorArg2 = termParam(Arg2Name, IntTypeName)
  private val CtorArg3 = termParam(Arg3Name, IntTypeName)
  private val CtorArg4 = termParam(Arg4Name, IntTypeName)

  private val ExpectedMemberDecl1 = declVal(Arg1Name, IntTypeName)
  private val ExpectedMemberDecl2 = declVal(Arg2Name, IntTypeName)
  private val ExpectedMemberDecl3 = declVal(Arg3Name, IntTypeName)
  private val ExpectedMemberDecl4 = declVal(Arg4Name, IntTypeName)

  private val InitialTemplate =
    Template(
      early = List(),
      inits = List(),
      self = Self(name = Name.Anonymous(), decltpe = None),
      stats = List(
        Defn.Def(
          mods = List(),
          name = Term.Name("myMethod"),
          tparams = List(),
          paramss = List(List(termParam("myParam", "String"))),
          decltpe = Some(Type.Name("String")),
          body = Block(List())
        )
      )
    )

  private val annotListTraverser = mock[AnnotListTraverser]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val templateTraverser = mock[TemplateTraverser]
  private val paramToDeclValTransformer = mock[ParamToDeclValTransformer]
  private val javaModifiersResolver = mock[JavaModifiersResolver]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]

  private val classTraverser = new RegularClassTraverserImpl(
    annotListTraverser,
    typeParamListTraverser,
    templateTraverser,
    paramToDeclValTransformer,
    javaModifiersResolver,
    javaTreeTypeResolver,
    javaChildScopeResolver
  )


  test("traverse() for one list of ctor args") {
    val parentJavaScope = JavaScope.Package

    val primaryCtor = Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = List(List(CtorArg1, CtorArg2))
    )

    val cls = Defn.Class(
      mods = Modifiers,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = InitialTemplate
    )

    val expectedMemberDecls = List(ExpectedMemberDecl1, ExpectedMemberDecl2)
    val expectedAdjustedTemplate = InitialTemplate.copy(stats = expectedMemberDecls ++ InitialTemplate.stats)

    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(cls, JavaTreeType.Class)))).thenReturn(JavaScope.Class)

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaTreeTypeThenReturnClass(cls)
    whenResolveJavaModifiersThenReturnPublic(cls, parentJavaScope)
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))

    when(paramToDeclValTransformer.transform(any[Term.Param])).thenAnswer( (ctorArg: Term.Param) => ctorArg match {
      case arg1 if arg1.structure == CtorArg1.structure => ExpectedMemberDecl1
      case arg2 if arg2.structure == CtorArg2.structure => ExpectedMemberDecl2
    })

    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
      eqTree(expectedAdjustedTemplate),
      eqTemplateContext(TemplateContext(javaScope = JavaScope.Class, maybeClassName = Some(ClassName), maybePrimaryCtor = Some(primaryCtor)))
    )

    classTraverser.traverse(cls, StatContext(parentJavaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public class MyClass<T> {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for two lists of ctor args") {
    val parentJavaScope = JavaScope.Package

    val primaryCtor = Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = List(
        List(CtorArg1, CtorArg2),
        List(CtorArg3, CtorArg4)
      )
    )

    val cls = Defn.Class(
      mods = Modifiers,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = InitialTemplate
    )

    val expectedMemberDecls = List(
      ExpectedMemberDecl1,
      ExpectedMemberDecl2,
      ExpectedMemberDecl3,
      ExpectedMemberDecl4
    )
    val expectedAdjustedTemplate = InitialTemplate.copy(stats = expectedMemberDecls ++ InitialTemplate.stats)

    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(cls, JavaTreeType.Class)))).thenReturn(JavaScope.Class)

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaTreeTypeThenReturnClass(cls)
    whenResolveJavaModifiersThenReturnPublic(cls, parentJavaScope)
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))

    when(paramToDeclValTransformer.transform(any[Term.Param])).thenAnswer( (ctorArg: Term.Param) => ctorArg match {
      case arg if arg.structure == CtorArg1.structure => ExpectedMemberDecl1
      case arg if arg.structure == CtorArg2.structure => ExpectedMemberDecl2
      case arg if arg.structure == CtorArg3.structure => ExpectedMemberDecl3
      case arg if arg.structure == CtorArg4.structure => ExpectedMemberDecl4
    })

    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateTraverser).traverse(
      eqTree(expectedAdjustedTemplate),
      eqTemplateContext(TemplateContext(javaScope = JavaScope.Class, maybeClassName = Some(ClassName), maybePrimaryCtor = Some(primaryCtor)))
    )

    classTraverser.traverse(cls, StatContext(parentJavaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public class MyClass<T> {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(
      mods = Nil,
      name = Term.Name(name),
      decltpe = Some(Type.Name(typeName)),
      default = None
    )
  }

  private def declVal(name: String, typeName: String) = {
    Decl.Val(
      mods = List(Private(within = Name.Anonymous()), Final()),
      pats = List(Pat.Var(Term.Name(name))),
      decltpe = Type.Name(typeName)
    )
  }

  private def whenResolveJavaTreeTypeThenReturnClass(cls: Defn.Class): Unit = {
    val expectedContext = JavaTreeTypeContext(cls, Modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedContext))).thenReturn(JavaTreeType.Class)
  }

  private def whenResolveJavaModifiersThenReturnPublic(cls: Defn.Class, parentJavaScope: JavaScope): Unit = {
    val expectedContext = JavaModifiersContext(cls, Modifiers, JavaTreeType.Class, parentJavaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedContext))).thenReturn(List(JavaModifier.Public))
  }
}
