package io.github.effiban.scala2java.core.extractors

import scala.annotation.tailrec
import scala.meta.{Decl, Member, Name, Term, Tree, Type}

object TreeNameExtractor {

  @tailrec
  def extract(tree: Tree): Name = tree match {
    case typeName: Type.Name => typeName
    case typeSelect: Type.Select => typeSelect.name
    case typeProject: Type.Project => typeProject.name
    case typeApply: Type.Apply => extract(typeApply.tpe)
    case termName: Term.Name => termName
    case termSelect: Term.Select => termSelect.name
    case termApply: Term.Apply => extract(termApply.fun)
    case termApplyType: Term.ApplyType => extract(termApplyType.fun)
    case member: Member => member.name
    case _ => Name.Anonymous()
  }

  /**
   * Some names such as the 'superp' part of a Term.Super are strictly considered 'indeterminate'
   * because they are not fully-qualified at any stage, and they are defined only in reference to another name
   * (in this case, a Template Init type name).
   * This distinction is important because we don't want the code to try and generate or find an import
   * for this name, since it cannot be the trigger for an import by itself.
   */
  def extractIndeterminate(tree: Tree): Name = extract(tree) match {
    case Name.Anonymous() => Name.Anonymous()
    case name => Name.Indeterminate(name.value)
  }
}
