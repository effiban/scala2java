package io.github.effiban.scala2java.core.cleanup

import scala.meta.Defn.Trait
import scala.meta.Term.NewAnonymous
import scala.meta.{Defn, Mod, Template, Type}

object IsTemplateAncestorUsedByAbstractEnclosingType extends IsTemplateAncestorUsed {

  /**
   * If a template's enclosing type is abstract, we will assume that all its ancestor types are being used.
   * That's because even if an ancestor is not referenced in the template, a subclass of the enclosing
   * type might reference it - and we do not have the ability to scan and check subclasses in the classpath
   * (at least yet...)
   */
  def apply(template: Template, ancestorType: Type.Ref): Boolean = template.parent match {
    case Some(_: NewAnonymous | _: Trait) => true
    case Some(cls: Defn.Class) if isAbstract(cls) => true
    case _ => false;
  }

  private def isAbstract(cls: Defn.Class) = {
    cls.mods.exists {
      case Mod.Abstract() => true
      case _ => false
    }
  }
}
