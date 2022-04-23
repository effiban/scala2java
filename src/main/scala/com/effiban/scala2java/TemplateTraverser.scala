package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Term.{Assign, Select, This}
import scala.meta.{Ctor, Decl, Defn, Init, Name, Stat, Template, Term, Type}

object TemplateTraverser extends ScalaTreeTraverser[Template] {

  override def traverse(template: Template): Unit = {
    traverseTemplate(template, None, None)
  }

  def traverseTemplate(template: Template,
                       maybeExplicitPrimaryCtor: Option[Ctor.Primary] = None,
                       maybeClassName: Option[Type.Name] = None): Unit = {
    traverseTemplateInits(template.inits)
    template.self.decltpe.foreach(declType => {
      // TODO - consider translating the 'self' type into a Java parent
      emitComment(template.self.toString)
    })
    traverseTemplateBody(template.stats, maybeExplicitPrimaryCtor, maybeClassName)
  }

  private def traverseTemplateInits(inits: List[Init]): Unit = {
    val relevantInits = inits.filterNot(init => shouldSkipParent(init.name))
    if (relevantInits.nonEmpty) {
      emitParentNamesPrefix()
      ArgumentListTraverser.traverse(relevantInits)
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
    statements.collect {
      case typeDecl: Decl.Type => typeDecl
      case typeDefn: Defn.Type => typeDefn
    }.foreach(typeStat => {
      GenericTreeTraverser.traverse(typeStat)
      emitStatementEnd()
    })
  }

  private def traverseDataMembers(statements: List[Stat]): Unit = {
    statements.collect {
      case valDecl: Decl.Val => valDecl
      case varDecl: Decl.Var => varDecl
      case valDefn: Defn.Val => valDefn
      case varDefn: Defn.Var => varDefn
    }.foreach(dataMember => {
      GenericTreeTraverser.traverse(dataMember)
      emitStatementEnd()
    })
  }

  // Render a Java explicit primary class constructor
  private def traverseExplicitPrimaryCtor(primaryCtor: Ctor.Primary, className: Type.Name): Unit = {
    AnnotListTraverser.traverseMods(primaryCtor.mods)
    emitModifiers(JavaModifiersResolver.resolveForClassMethod(primaryCtor.mods))
    GenericTreeTraverser.traverse(className)
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    ArgumentListTraverser.traverse(primaryCtor.paramss.flatten, maybeDelimiterType = Some(Parentheses))
    emitBlockStart()
    // Initialize members explicitly (what is done implicitly for Java records and Scala classes)
    primaryCtor.paramss.flatten.foreach(param => {
      val paramName = Term.Name(param.name.toString())
      GenericTreeTraverser.traverse(Assign(Select(This(Name.Anonymous()), paramName), paramName))
      emitStatementEnd()
    })
    emitBlockEnd()
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseMethods(statements: List[Stat]): Unit = {
    statements.collect {
      case defDecl: Decl.Def => defDecl
      case defDefn: Defn.Def => defDefn
    }.foreach(elem => GenericTreeTraverser.traverse(elem))
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
      case other => GenericTreeTraverser.traverse(other)
    }
  }
}
