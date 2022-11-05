package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.ParamToDeclValTransformer
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

  private val modListTraverser = mock[ModListTraverser]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val templateTraverser = mock[TemplateTraverser]
  private val paramToDeclValTransformer = mock[ParamToDeclValTransformer]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]

  private val classTraverser = new RegularClassTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    templateTraverser,
    paramToDeclValTransformer,
    javaTreeTypeResolver,
    javaChildScopeResolver
  )


  test("traverse() for one list of ctor args") {
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

    whenResolveJavaTreeTypeThenReturnClass(cls)
    doWrite(
    """@MyAnnotation
      |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(cls), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(cls, JavaTreeType.Class)))).thenReturn(JavaScope.Class)

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

    classTraverser.traverse(cls, ClassOrTraitContext(JavaScope.Package))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public class MyClass<T> {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for one list of ctor args with permitted sub-type names") {
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

    val permittedSubTypeNames = List(Type.Name("A"), Term.Name("B"))

    whenResolveJavaTreeTypeThenReturnClass(cls)
    doWrite(
    """@MyAnnotation
      |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(cls), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(cls, JavaTreeType.Class)))).thenReturn(JavaScope.Class)

    when(paramToDeclValTransformer.transform(any[Term.Param])).thenAnswer((ctorArg: Term.Param) => ctorArg match {
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
      eqTemplateContext(TemplateContext(
        javaScope = JavaScope.Class,
        maybeClassName = Some(ClassName),
        maybePrimaryCtor = Some(primaryCtor),
        permittedSubTypeNames = permittedSubTypeNames)
      )
    )

    val context = ClassOrTraitContext(javaScope = JavaScope.Package, permittedSubTypeNames = permittedSubTypeNames)
    classTraverser.traverse(cls, context)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public class MyClass<T> {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() for two lists of ctor args") {
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

    whenResolveJavaTreeTypeThenReturnClass(cls)
    doWrite(
    """@MyAnnotation
        |public """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(cls), annotsOnSameLine = ArgumentMatchers.eq(false))
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

    classTraverser.traverse(cls, ClassOrTraitContext(JavaScope.Package))

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

  private def eqExpectedModifiers(classDef: Defn.Class) = {
    val expectedModifiersContext = ModifiersContext(classDef, JavaTreeType.Class, JavaScope.Package)
    eqModifiersContext(expectedModifiersContext)
  }
}
