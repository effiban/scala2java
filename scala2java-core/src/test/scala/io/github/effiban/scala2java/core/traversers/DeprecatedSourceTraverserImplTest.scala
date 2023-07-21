package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Ctor, Defn, Mod, Name, Pkg, Self, Source, Template, Term, Type}

@deprecated
class DeprecatedSourceTraverserImplTest extends UnitTestSuite {

  private val statTraverser = mock[DeprecatedStatTraverser]

  private val sourceTraverser = new DeprecatedSourceTraverserImpl(statTraverser)

  test("traverse()") {
    val pkg1 = Pkg(ref = Term.Name("mypackage1"),
      stats = List(
        caseClassDefinition("MyClass1", "myArg1", "String"),
        caseClassDefinition("MyClass2", "myArg2", "Int")
      )
    )
    val pkg2 = Pkg(ref = Term.Name("mypackage2"),
      stats = List(
        caseClassDefinition("MyClass3", "myArg3", "String"),
        caseClassDefinition("MyClass4", "myArg4", "Int")
      )
    )

    doWrite(
      """
        |/*
        |*  PACKAGE 1 DEFINITION
        |*/
        |""".stripMargin)
      .when(statTraverser).traverse(eqTree(pkg1), ArgumentMatchers.eq(StatContext()))

   doWrite(
     """
        |/*
        |*  PACKAGE 2 DEFINITION
        |*/
        |""".stripMargin)
     .when(statTraverser).traverse(eqTree(pkg2), ArgumentMatchers.eq(StatContext()))


    sourceTraverser.traverse(Source(stats = List(pkg1, pkg2)))

    outputWriter.toString shouldBe
      """
        |/*
        |*  PACKAGE 1 DEFINITION
        |*/
        |
        |/*
        |*  PACKAGE 2 DEFINITION
        |*/
        |""".stripMargin
  }

  private def caseClassDefinition(className: String, ctorArgName: String, ctorArgType: String) = {
    Defn.Class(
      mods = List(Mod.Case()),
      name = Type.Name(className),
      tparams = Nil,
      ctor = Ctor.Primary(
        mods = Nil,
        name = Name.Anonymous(),
        paramss = List(
          List(
            Term.Param(mods = Nil, name = Term.Name(ctorArgName), decltpe = Some(Type.Name(ctorArgType)), default = None)
          )
        )
      ),
      templ = Template(
        early = Nil,
        inits = Nil,
        self = Self(name = Name.Anonymous(), decltpe = None),
        stats = Nil
      )
    )
  }
}
