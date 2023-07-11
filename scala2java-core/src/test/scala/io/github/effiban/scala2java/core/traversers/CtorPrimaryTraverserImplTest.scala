package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{CtorContext, DefnDefContext}
import io.github.effiban.scala2java.core.matchers.CtorContextMatcher.eqCtorContext
import io.github.effiban.scala2java.core.matchers.DefnDefContextMatcher.eqDefnDefContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.transformers.CtorPrimaryTransformer
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Ctor, Defn, Init, Name, Term, Type}

class CtorPrimaryTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val TheInits = List(
    Init(tpe = Type.Name("Parent1"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Parent2"), name = Name.Anonymous(), argss = List())
  )

  private val TheCtorContext = CtorContext(
    javaScope = JavaScope.Class,
    className = ClassName,
    inits = TheInits
  )

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
  private val defnDefTraverser = mock[DeprecatedDefnDefTraverser]

  private val ctorPrimaryTraverser = new CtorPrimaryTraverserImpl(ctorPrimaryTransformer, defnDefTraverser)

  test("traverse") {
    when(ctorPrimaryTransformer.transform(eqTree(PrimaryCtor), eqCtorContext(TheCtorContext))).thenReturn(ExpectedDefnDef)

    ctorPrimaryTraverser.traverse(PrimaryCtor, TheCtorContext)

    verify(defnDefTraverser).traverse(eqTree(ExpectedDefnDef), eqDefnDefContext(DefnDefContext(javaScope = JavaScope.Class)))
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }
}
