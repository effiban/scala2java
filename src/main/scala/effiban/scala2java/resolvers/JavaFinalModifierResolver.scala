package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.{JavaModifier, JavaScope}

import scala.meta.{Decl, Defn, Term}

private[resolvers] object JavaFinalModifierResolver extends JavaExtraModifierResolver {

  override def resolve(context: JavaModifiersContext): Option[JavaModifier] = {
    import context._

    (scalaTree, javaScope) match {
      case (_: Decl.Val | _: Defn.Val, JavaScope.Class | JavaScope.Enum | JavaScope.Block) => Some(JavaModifier.Final)
      // Can't add final in a Lambda param because it might not have an explicit type,
      // and we are not adding 'var' there either at this point since it has complicated rules
      case (_: Term.Param, JavaScope.LambdaSignature) => None
      case (_: Term.Param, _) => Some(JavaModifier.Final)
      case _ => None
    }
  }
}
