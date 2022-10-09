package io.github.effiban.scala2java.classifiers

import io.github.effiban.scala2java.classifiers.JavaStatClassifier.requiresEndDelimiter
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.{PrimaryCtors, Selfs, Templates, TypeNames}

import scala.meta.Enumerator.Generator
import scala.meta.Mod.Annot
import scala.meta.Term.{Apply, ApplyType, ApplyUnary, Assign, Block, Eta, For, ForYield, If, NewAnonymous, Super, This}
import scala.meta.{Case, Decl, Defn, Import, Importee, Importer, Init, Lit, Name, Pat, Stat, Template, Term, Type}

class JavaStatClassifierTest extends UnitTestSuite {

  private val TheThis = This(Name.Indeterminate("MyName"))
  private val TheSuper = Super(thisp = Name.Indeterminate("superName"), superp = Name.Anonymous())
  private val TheName = Term.Name("x")
  private val TheSelect = Term.Select(Term.Name("X"), Term.Name("x"))
  private val TheApply = Term.Apply(Term.Name("myFunc"), List(Term.Name("x"), Term.Name("y")))
  private val TheApplyInfix = Term.ApplyInfix(Term.Name("x"), Term.Name("+"), Nil, List(Term.Name("y")))
  private val TheApplyUnary = ApplyUnary(Term.Name("!"), Term.Name("x"))
  private val TheApplyType = ApplyType(Term.Name("myFunc"), List(Type.Name("T"), Type.Name("U")))
  private val TheAssign = Term.Assign(Term.Name("x"), Lit.Int(3))
  private val TheReturn = Term.Return(Term.Name("x"))
  private val TheAscribe = Term.Ascribe(Term.Name("myVar"), Type.Name("T"))
  private val TheTuple = Term.Tuple(List(Term.Name("x"), Term.Name("y")))
  private val TheFor = For(List(Generator(Pat.Var(Term.Name("x")), Term.Name("xs"))), Term.Name("doSomething"))
  private val TheForYield = ForYield(List(Generator(Pat.Var(Term.Name("x")), Term.Name("xs"))), Term.Name("result"))
  private val TheDo = Term.Do(Term.Name("doSomething"), Term.ApplyInfix(Term.Name("x"), Term.Name("<"), Nil, List(Lit.Int(3))))
  private val TheNew = Term.New(Init(Type.Name("MyClass"), Name.Anonymous(), Nil))
  private val TheNewAnonymous = NewAnonymous(Template(Nil, List(Init(Type.Name("Parent"), Name.Anonymous(), Nil)), Selfs.Empty, Nil))
  private val ThePlaceholder = Term.Placeholder()
  private val TheEta = Eta(Term.Name("myFunc"))
  private val TheRepeated = Term.Repeated(Term.Name("x"))
  private val TheInterpolate = Term.Interpolate(Term.Name("s"), List(Lit.String("start-"), Lit.String("-end")), List(Term.Name("myVal")))
  private val TheThrow = Term.Throw(Term.Name("IllegalStateException"))
  private val TheAnnotate = Term.Annotate(Term.Name("myName"), List(Annot(Init(Type.Name("MyAnnot1"), Name.Anonymous(), List()))))
  private val TheBlock = Block(List(Assign(Term.Name("x"), Lit.Int(3)), Assign(Term.Name("y"), Lit.Int(4))))
  private val TheIf = If(Term.ApplyInfix(Term.Name("x"), Term.Name("<"), Nil, List(Lit.Int(3))), Term.Name("doSomething"), Lit.Unit())
  private val TheMatch = Term.Match(Term.Name("x"), List(Case(Lit.Int(1), None, Lit.String("one"))), Nil)
  private val TheTry = Term.Try(Term.Apply(Term.Name("foo"), Nil), List(Case(Pat.Var(Term.Name("x")), None, Lit.Int(5))), None)
  private val TheTryWithHandler = Term.TryWithHandler(Term.Apply(Term.Name("foo"), Nil), Term.Apply(Term.Name("bar"), Nil), None)
  private val TheWhile = Term.While(Term.ApplyInfix(Term.Name("x"), Term.Name("<"), Nil, List(Lit.Int(3))), Term.Name("doSomething"))
  private val FunctionWithStatement = Term.Function(List(Term.Param(Nil, Term.Name("x"), None, None)), Apply(Term.Name("foo"), Nil))
  private val FunctionWithBlockOfOne = Term.Function(List(Term.Param(Nil, Term.Name("x"), None, None)), Block(List(Apply(Term.Name("foo"), Nil))))
  private val FunctionWithBlockOfTwo = Term.Function(List(Term.Param(Nil, Term.Name("x"), None, None)), Block(List(Apply(Term.Name("foo1"), Nil), Apply(Term.Name("foo2"), Nil))))
  private val AnonymousFunctionWithStatement = Term.AnonymousFunction(Apply(Term.Name("foo"), Nil))
  private val AnonymousFunctionWithBlockOfOne = Term.AnonymousFunction(Block(List(Apply(Term.Name("foo"), Nil))))
  private val AnonymousFunctionWithBlockOfTwo = Term.AnonymousFunction(Block(List(Apply(Term.Name("foo1"), Nil), Apply(Term.Name("foo2"), Nil))))
  private val ThePartialFunction = Term.PartialFunction(List(Case(Lit.Int(1), None, Lit.String("one"))))
  private val TheImport = Import(List(Importer(Term.Name("A"), List(Importee.Name(Name.Indeterminate("B"))))))
  private val TheDeclVal = Decl.Val(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)
  private val TheDefnVal = Defn.Val(Nil, List(Pat.Var(Term.Name("x"))), None, Lit.Int(3))
  private val TheDeclVar = Decl.Var(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)
  private val TheDefnVar = Defn.Var(Nil, List(Pat.Var(Term.Name("x"))), None, Some(Lit.Int(3)))
  private val TheDeclDef = Decl.Def(Nil, Term.Name("foo"), Nil, List(Nil), TypeNames.Int)
  private val TheDefnDef = Defn.Def(Nil, Term.Name("foo"), Nil, List(Nil), Some(TypeNames.Int), Term.Apply(Term.Name("bar"), Nil))
  private val TheDefnClass = Defn.Class(Nil, Type.Name("A"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private val TheDefnTrait = Defn.Trait(Nil, Type.Name("A"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private val TheDefnObject = Defn.Object(Nil, Term.Name("A"), Templates.Empty)


  private val RequiresEndDelimiterScenarios = Table(
    ("Description", "Term", "ExpectedResult"),
    ("This", TheThis, true),
    ("Super", TheSuper, true),
    ("Name", TheName, true),
    ("Select", TheSelect, true),
    ("Apply", TheApply, true),
    ("ApplyUnary", TheApplyUnary, true),
    ("ApplyType", TheApplyType, true),
    ("ApplyInfix", TheApplyInfix, true),
    ("Assign", TheAssign, true),
    ("Return", TheReturn, true),
    ("Ascribe", TheAscribe, true),
    ("Tuple", TheTuple, true),
    ("For", TheFor, true),
    ("ForYield", TheForYield, true),
    ("Do", TheDo, true),
    ("New", TheNew, true),
    ("NewAnonymous", TheNewAnonymous, false),
    ("Placeholder", ThePlaceholder, true),
    ("Eta", TheEta, true),
    ("Repeated", TheRepeated, true),
    ("Interpolate", TheInterpolate, true),
    ("Throw", TheThrow, true),
    ("Annotate", TheAnnotate, false),
    ("Block", TheBlock, false),
    ("If", TheIf, false),
    ("Match", TheMatch, false),
    ("Try", TheTry, false),
    ("TryWithHandler", TheTryWithHandler, false),
    ("While", TheWhile, false),
    ("FunctionWithStatement", FunctionWithStatement, true),
    ("FunctionWithBlockOfOne", FunctionWithBlockOfOne, true),
    ("FunctionWithBlockOfTwo", FunctionWithBlockOfTwo, false),
    ("AnonymousFunctionWithStatement", AnonymousFunctionWithStatement, true),
    ("AnonymousFunctionWithBlockOfOne", AnonymousFunctionWithBlockOfOne, true),
    ("AnonymousFunctionWithBlockOfTwo", AnonymousFunctionWithBlockOfTwo, false),
    ("PartialFunction", ThePartialFunction, false),
    ("Import", TheImport, true),
    ("DeclVal", TheDeclVal, true),
    ("DefnVal", TheDefnVal, true),
    ("DeclVar", TheDeclVar, true),
    ("DefnVar", TheDefnVar, true),
    ("DeclDef", TheDeclDef, true),
    ("DefnDef", TheDefnDef, false),
    ("Class", TheDefnClass, false),
    ("Trait", TheDefnTrait, false),
    ("Object", TheDefnObject, false)
  )


  forAll(RequiresEndDelimiterScenarios) { case (desc: String, stat: Stat, expectedResult: Boolean) =>
    test(s"requiresEndDelimiter($desc) should be $expectedResult") {
      requiresEndDelimiter(stat) shouldBe expectedResult
    }
  }
}
