package effiban.scala2java.traversers

import effiban.scala2java.contexts.{CtorContext, DefnDefContext}
import effiban.scala2java.entities.JavaTreeType.Unknown
import effiban.scala2java.matchers.CtorContextMatcher.eqCtorContext
import effiban.scala2java.matchers.DefnDefContextMatcher.eqDefnDefContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.transformers.CtorPrimaryTransformer

import scala.meta.{Ctor, Defn, Init, Name, Term, Type}

class CtorPrimaryTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val TheInits = List(
    Init(tpe = Type.Name("Parent1"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Parent2"), name = Name.Anonymous(), argss = List())
  )

  private val TheCtorContext = CtorContext(className = ClassName, inits = TheInits)

  private val PrimaryCtorArgs = List(
    termParam("arg1", "Int"),
    termParam("arg2", "String")
  )

  private val PrimaryCtor = Ctor.Primary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(PrimaryCtorArgs)
  )

  private val Statement = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))

  private val ExpectedDefnDef = Defn.Def(
    mods = Nil,
    name = Term.Name("myMethod"),
    tparams = Nil,
    paramss = List(List(termParam("param", "Int"))),
    decltpe = Some(TypeNames.Int),
    body = Statement
  )

  private val ctorPrimaryTransformer = mock[CtorPrimaryTransformer]
  private val defnDefTraverser = mock[DefnDefTraverser]

  private val ctorPrimaryTraverser = new CtorPrimaryTraverserImpl(ctorPrimaryTransformer, defnDefTraverser)

  test("traverse") {
    when(ctorPrimaryTransformer.transform(eqTree(PrimaryCtor), eqCtorContext(TheCtorContext))).thenReturn(ExpectedDefnDef)

    ctorPrimaryTraverser.traverse(PrimaryCtor, TheCtorContext)

    verify(defnDefTraverser).traverse(eqTree(ExpectedDefnDef), eqDefnDefContext(DefnDefContext(javaScope = Unknown)))
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }
}
