package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Term.{Assign, Block, Select, This}
import scala.meta.{Ctor, Decl, Defn, Init, Name, Stat, Template, Term, Type}

object TemplateTraverser extends ScalaTreeTraverser[Template] {

  override def traverse(template: Template): Unit = {
    traverseTemplate(template, None, None)
  }

  def traverseTemplate(template: Template,
                       maybeExplicitPrimaryCtor: Option[Ctor.Primary] = None,
                       maybeClassName: Option[Type.Name] = None): Unit = {
    traverseTemplateInits(template.inits)
    template.self.decltpe.foreach(_ => {
      // TODO - consider translating the 'self' type into a Java parent
      emitComment(template.self.toString)
    })
    traverseTemplateBody(template.stats, maybeExplicitPrimaryCtor, maybeClassName)
  }

  private def traverseTemplateInits(inits: List[Init]): Unit = {
    val relevantInits = inits.filterNot(init => shouldSkipParent(init.name))
    if (relevantInits.nonEmpty) {
      emitParentNamesPrefix()
      InitListTraverser.traverse(relevantInits)
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
                                   maybeExplicitPrimaryCtor: Option[Ctor.Primary] = None,
                                   maybeClassName: Option[Type.Name] = None): Unit = {
    emitBlockStart()
    traverseTypeMembers(statements)
    traverseDataMembers(statements)
    (maybeExplicitPrimaryCtor, maybeClassName) match {
      case (Some(primaryCtor), Some(className)) => traverseExplicitPrimaryCtor(primaryCtor, className)
      case _ =>
    }
    traverseMethods(statements)
    traverseOtherMembers(statements)
    emitBlockEnd()
  }

  private def traverseTypeMembers(statements: List[Stat]): Unit = {
    statements.foreach {
      case typeDecl: Decl.Type =>
        DeclTypeTraverser.traverse(typeDecl)
        emitStatementEnd()
      case typeDefn: Defn.Type =>
        DefnTypeTraverser.traverse(typeDefn)
        emitStatementEnd()
      case _ =>
    }
  }

  private def traverseDataMembers(statements: List[Stat]): Unit = {
    statements.foreach {
      case valDecl: Decl.Val =>
        DeclValTraverser.traverse(valDecl)
        emitStatementEnd()
      case varDecl: Decl.Var =>
        DeclVarTraverser.traverse(varDecl)
        emitStatementEnd()
      case valDefn: Defn.Val =>
        DefnValTraverser.traverse(valDefn)
        emitStatementEnd()
      case varDefn: Defn.Var =>
        DefnVarTraverser.traverse(varDefn)
        emitStatementEnd()
      case _ =>
    }
  }

  // Render a Java explicit primary class constructor
  private def traverseExplicitPrimaryCtor(primaryCtor: Ctor.Primary, className: Type.Name): Unit = {
    emitLine()
    AnnotListTraverser.traverseMods(primaryCtor.mods)
    emitModifiers(JavaModifiersResolver.resolveForClassMethod(primaryCtor.mods))
    TypeNameTraverser.traverse(className)
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    TermParamListTraverser.traverse(primaryCtor.paramss.flatten)

    // Initialize members explicitly (what is done implicitly for Java records and Scala classes)
    val assignments = primaryCtor.paramss.flatten.map(param => {
      val paramName = Term.Name(param.name.toString())
      Assign(Select(This(Name.Anonymous()), paramName), paramName)
    })
    BlockTraverser.traverse(block = Block(assignments), shouldReturnValue = false)

    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseMethods(statements: List[Stat]): Unit = {
    statements.foreach {
      case defDecl: Decl.Def =>
        DeclDefTraverser.traverse(defDecl)
        emitStatementEnd()
      case defDefn: Defn.Def =>
        DefnDefTraverser.traverse(defDefn)
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
      case other => StatTraverser.traverse(other)
    }
  }
}
