package effiban.scala2java.traversers

import effiban.scala2java.contexts.DefnDefContext
import effiban.scala2java.entities.CtorContext
import effiban.scala2java.entities.JavaTreeType.Unknown
import effiban.scala2java.matchers.CtorContextMatcher.eqCtorContext
import effiban.scala2java.matchers.DefnDefContextMatcher.eqDefnDefContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.transformers.CtorSecondaryTransformer

import scala.meta.{Ctor, Defn, Init, Name, Term, Type}

class CtorSecondaryTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val TheInits = List(
    Init(tpe = Type.Name("Parent1"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Parent2"), name = Name.Anonymous(), argss = List())
  )

  private val TheCtorContext = CtorContext(className = ClassName, inits = TheInits)

  private val CtorArgs = List(
    termParam("arg1", "Int"),
    termParam("arg2", "String")
  )

  private val SecondaryCtor = Ctor.Secondary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(CtorArgs),
    init = Init(tpe = Type.Singleton(Term.This(Name.Anonymous())), name = Name.Anonymous(), argss = List(Nil)),
    stats = Nil
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

  private val ctorSecondaryTransformer = mock[CtorSecondaryTransformer]
  private val defnDefTraverser = mock[DefnDefTraverser]

  private val ctorSecondaryTraverser = new CtorSecondaryTraverserImpl(ctorSecondaryTransformer, defnDefTraverser)

  test("traverse") {
    when(ctorSecondaryTransformer.transform(eqTree(SecondaryCtor), eqCtorContext(TheCtorContext))).thenReturn(ExpectedDefnDef)

    ctorSecondaryTraverser.traverse(SecondaryCtor, TheCtorContext)

    verify(defnDefTraverser).traverse(
      eqTree(ExpectedDefnDef),
      eqDefnDefContext(DefnDefContext(javaScope = Unknown, maybeInit = Some(SecondaryCtor.init)))
    )
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }
}
