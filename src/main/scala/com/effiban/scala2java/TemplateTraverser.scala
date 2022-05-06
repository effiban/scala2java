package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Ctor.Secondary
import scala.meta.Term.{Assign, Block, Select, This}
import scala.meta.{Ctor, Decl, Defn, Init, Name, Stat, Template, Term, Type}

trait TemplateTraverser extends ScalaTreeTraverser[Template] {

  def traverse(template: Template,
               maybeClassInfo: Option[ClassInfo] = None): Unit
}

private[scala2java] class TemplateTraverserImpl(initListTraverser: => InitListTraverser,
                                                declTypeTraverser: => DeclTypeTraverser,
                                                defnTypeTraverser: => DefnTypeTraverser,
                                                declValTraverser: => DeclValTraverser,
                                                declVarTraverser: => DeclVarTraverser,
                                                defnValTraverser: => DefnValTraverser,
                                                defnVarTraverser: => DefnVarTraverser,
                                                annotListTraverser: => AnnotListTraverser,
                                                typeNameTraverser: => TypeNameTraverser,
                                                termParamListTraverser: => TermParamListTraverser,
                                                blockTraverser: => BlockTraverser,
                                                declDefTraverser: => DeclDefTraverser,
                                                defnDefTraverser: => DefnDefTraverser,
                                                statTraverser: => StatTraverser,
                                                javaModifiersResolver: JavaModifiersResolver)
                                               (implicit javaEmitter: JavaEmitter) extends TemplateTraverser {

  import javaEmitter._

  override def traverse(template: Template): Unit = {
    traverse(template, None)
  }

  override def traverse(template: Template,
                        maybeClassInfo: Option[ClassInfo] = None): Unit = {
    traverseTemplateInits(template.inits)
    template.self.decltpe.foreach(_ => {
      // TODO - consider translating the 'self' type into a Java parent
      emitComment(template.self.toString)
    })
    traverseTemplateBody(statements = template.stats,
      maybeClassInfo = maybeClassInfo)
  }

  private def traverseTemplateInits(inits: List[Init]): Unit = {
    val relevantInits = inits.filterNot(init => shouldSkipParent(init.name))
    if (relevantInits.nonEmpty) {
      emitParentNamesPrefix()
      initListTraverser.traverse(relevantInits)
    }
  }

  private def shouldSkipParent(parent: Name): Boolean = {
    parent match {
      case Term.Name("AnyRef") => true
      case Term.Name("Product") => true
      case Term.Name("Serializable") => true
      case _ => false
    }
  }

  private def traverseTemplateBody(statements: List[Stat],
                                   maybeClassInfo: Option[ClassInfo] = None): Unit = {
    // Traversing in parts to fit Java conventions for order of members in a type
    emitBlockStart()
    traverseTypeMembers(statements)
    traverseDataMembers(statements)
    maybeClassInfo.foreach(classInfo => {
      classInfo.maybeExplicitPrimaryCtor.foreach(primaryCtor => traverseExplicitPrimaryCtor(primaryCtor, classInfo.className))
      traverseSecondaryCtors(statements, classInfo.className)
    })
    traverseMethods(statements)
    traverseOtherMembers(statements)
    emitBlockEnd()
  }

  private def traverseTypeMembers(statements: List[Stat]): Unit = {
    statements.foreach {
      case typeDecl: Decl.Type =>
        declTypeTraverser.traverse(typeDecl)
        emitStatementEnd()
      case typeDefn: Defn.Type =>
        defnTypeTraverser.traverse(typeDefn)
        emitStatementEnd()
      case _ =>
    }
  }

  private def traverseDataMembers(statements: List[Stat]): Unit = {
    statements.foreach {
      case valDecl: Decl.Val =>
        declValTraverser.traverse(valDecl)
        emitStatementEnd()
      case varDecl: Decl.Var =>
        declVarTraverser.traverse(varDecl)
        emitStatementEnd()
      case valDefn: Defn.Val =>
        defnValTraverser.traverse(valDefn)
        emitStatementEnd()
      case varDefn: Defn.Var =>
        defnVarTraverser.traverse(varDefn)
        emitStatementEnd()
      case _ =>
    }
  }

  private def traverseExplicitPrimaryCtor(primaryCtor: Ctor.Primary, className: Type.Name): Unit = {
    emitLine()
    annotListTraverser.traverseMods(primaryCtor.mods)
    emitModifiers(javaModifiersResolver.resolveForClassMethod(primaryCtor.mods))
    typeNameTraverser.traverse(className)
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    termParamListTraverser.traverse(primaryCtor.paramss.flatten)

    // Initialize members explicitly (what is done implicitly for Java records and Scala classes)
    val assignments = primaryCtor.paramss.flatten.map(param => {
      val paramName = Term.Name(param.name.toString())
      Assign(Select(This(Name.Anonymous()), paramName), paramName)
    })
    blockTraverser.traverse(block = Block(assignments), shouldReturnValue = false)

    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseSecondaryCtors(statements: List[Stat], className: Type.Name): Unit = {
    statements.collect { case secondaryCtor: Secondary => secondaryCtor }
      .foreach(secondaryCtor => traverseSecondaryCtor(secondaryCtor, className))
  }

  private def traverseSecondaryCtor(secondaryCtor: Secondary, className: Type.Name): Unit = {
    annotListTraverser.traverseMods(secondaryCtor.mods)
    emitModifiers(javaModifiersResolver.resolveForClassMethod(secondaryCtor.mods))
    typeNameTraverser.traverse(className)
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    termParamListTraverser.traverse(secondaryCtor.paramss.flatten)
    blockTraverser.traverse(block = Block(secondaryCtor.stats), shouldReturnValue = false, maybeInit = Some(secondaryCtor.init))
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseMethods(statements: List[Stat]): Unit = {
    statements.foreach {
      case defDecl: Decl.Def =>
        declDefTraverser.traverse(defDecl)
        emitStatementEnd()
      case defDefn: Defn.Def =>
        defnDefTraverser.traverse(defDefn)
      case _ =>
    }
  }

  private def traverseOtherMembers(statements: List[Stat]): Unit = {
    statements.foreach {
      case _: Decl.Type =>
      case _: Decl.Val =>
      case _: Decl.Var =>
      case _: Defn.Type =>
      case _: Defn.Val =>
      case _: Defn.Var =>
      case _: Decl.Def =>
      case _: Defn.Def =>
      case _: Ctor =>
      case other => StatTraverser.traverse(other)
    }
  }
}

object TemplateTraverser extends TemplateTraverserImpl(
  InitListTraverser,
  DeclTypeTraverser,
  DefnTypeTraverser,
  DeclValTraverser,
  DeclVarTraverser,
  DefnValTraverser,
  DefnVarTraverser,
  AnnotListTraverser,
  TypeNameTraverser,
  TermParamListTraverser,
  BlockTraverser,
  DeclDefTraverser,
  DefnDefTraverser,
  StatTraverser,
  JavaModifiersResolver
)
