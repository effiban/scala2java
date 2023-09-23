package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.Scala2JavaExtension

/** A container for all extension provider hooks which are transformers.
 *
 * @see [[Scala2JavaExtension]]
 */
trait ExtendedTransformers {

  /** Override this method if you need to produce an output Java file with a different name than the input Scala file.
   *
   * @return if overriden - a transformer which changes the file name<br>
   *         otherwise - the default which leaves the name unchanged<br>
   */
  def fileNameTransformer(): FileNameTransformer = FileNameTransformer.Identity

  /** Override this method if you need to transform a [[scala.meta.Defn.Class]].<br>
   * NOTE that this transformer intended for manipulating the class declaration (e.g. name, visibility, annotations).<br>
   * For manipulating the template part (parents, body) - override one of the other transformers instead.
   *
   * @return if overriden - a transformer which modifies a given class<br>
   *         otherwise - the default transformer which doesn't change anything<br>
   */
  def classTransformer(): ClassTransformer = ClassTransformer.Identity

  /** Override this method if you need to transform a [[scala.meta.Term.ApplyInfix]] (infix-style method invocation),
   * appearing in a Scala template (class/trait/object) body, into a [[scala.meta.Defn]] (variable/method/class etc. definition).<br>
   *
   * @see [[TemplateTermApplyInfixToDefnTransformer]] for as usage example.
   * @return if overriden - a transformer which transforms a [[scala.meta.Term.ApplyInfix]] appearing in a template body,
   *         into a [[scala.meta.Defn.Def]] - where applicable<br>
   *         otherwise - the default transformer which never transforms (returns `None`)
   */
  def templateTermApplyInfixToDefnTransformer(): TemplateTermApplyInfixToDefnTransformer = TemplateTermApplyInfixToDefnTransformer.Empty

  /** Override this method if you need to transform a [[scala.meta.Term.Apply]] (method invocation) appearing in a Scala template body
   * (class/trait/object body), into a [[scala.meta.Defn]] (variable/method/class etc. definition).<br>
   * '''NOTE regarding precedence''': This transformer will be applied before [[termApplyTransformer()]] (if needed).
   *
   * @see [[TemplateTermApplyToDefnTransformer]] for as usage example.
   * @return if overriden - a transformer which transforms a [[scala.meta.Term.Apply]] appearing in a template body,
   *         into a into a [[scala.meta.Defn.Def]] - where applicable<br>
   *         otherwise - the default transformer which never transforms (returns `None`)
   */
  def templateTermApplyToDefnTransformer(): TemplateTermApplyToDefnTransformer = TemplateTermApplyToDefnTransformer.Empty

  /** Override this method if you need to transform a [[scala.meta.Defn.Var]] (variable definition).<br>
   *
   * @return if overriden - a transformer which transforms a [[scala.meta.Defn.Var]]<br>
   *         otherwise - the default transformer which doesn't modify anything<br>
   */
  def defnVarTransformer(): DefnVarTransformer = DefnVarTransformer.Identity

  /** Override this method if you need to transform a [[scala.meta.Defn.Var]] (variable definition) into a
   * [[scala.meta.Decl.Var]] (variable declaration).<br>
   * @see [[DefnVarToDeclVarTransformer]] for a usage example.
   *
   * @return if overriden - a transformer which transforms a [[scala.meta.Defn.Var]] into a [[scala.meta.Decl.Var]] where applicable<br>
   *         otherwise - the default transformer which never transforms (returns `None`)<br>
   */
  def defnVarToDeclVarTransformer(): DefnVarToDeclVarTransformer = DefnVarToDeclVarTransformer.Empty

  /** Override this method if you need to modify a [[scala.meta.Defn.Def]] (method definition)
   *
   * @return if overriden - a transformer which modifies a given [[scala.meta.Defn.Def]]<br>
   *         otherwise - the default transformer which doesn't modify anything<br>
   */
  def defnDefTransformer(): DefnDefTransformer = DefnDefTransformer.Identity

  /** Override this method if you need to transform a [[scala.meta.Term.ApplyInfix]] (infix method invocation) into a
   * [[scala.meta.Term.Apply]] (regular method invocation).<br>
   * '''NOTE regarding precedence''': The output of this transformer, if not empty, will be passed to [[termApplyTransformer]] for additional processing
   *
   * @see [[TermApplyInfixToTermApplyTransformer]] for a usage example.
   * @return if overriden - a transformer which transforms a [[scala.meta.Term.ApplyInfix]] into a [[scala.meta.Term.Apply]] where applicable.<br>
   *         otherwise - the default transformer which never transforms (returns `None`)
   */
  def termApplyInfixToTermApplyTransformer(): TermApplyInfixToTermApplyTransformer = TermApplyInfixToTermApplyTransformer.Empty

  /** Override this method if you need to modify a [[scala.meta.Term.Apply]] (method invocation).<br>
   * '''NOTE regarding precedence''': In the scope of a template body, this transformer will be invoked after [[templateTermApplyToDefnTransformer()]]
   *
   * @return if overriden - a transformer which modifies a given [[scala.meta.Term.Apply]]<br>
   *         otherwise - the default transformer which doesn't modify anything<br>
   */
  def termApplyTransformer(): TermApplyTransformer = TermApplyTransformer.Identity

  /** Override this method if you need to modify a [[scala.meta.Term.Select]] (qualified name).<br>
   * '''NOTE''': This transformer will only be called for qualified names that are __not__ method invocations.<br>
   * If some method invocation is a qualified name with no args and no parentheses,
   * it will be automatically 'desugared' into a method invocation and eventually passed to [[termApplyTransformer]].<br>
   * The framework will identify such cases by calling [[io.github.effiban.scala2java.spi.predicates.ExtendedPredicates.termSelectSupportsNoArgInvocation]]
   *
   * @return if overriden - a transformer which modifies a given [[scala.meta.Term.Select]]<br>
   *         otherwise - the default transformer which doesn't modify anything<br>
   */
  def termSelectTransformer(): TermSelectTransformer = TermSelectTransformer.Empty

  /** Override this method if you need to modify a [[scala.meta.Term.Name]] (identifier) appearing in the context
   * of an evaluated expression (see [[TermApplyTransformer]] for examples).<br>
   * '''NOTE''': This transformer will only be called for identifiers that are __not__ method invocations.<br>
   * If some method invocation is an identifier with no args and no parentheses,
   * it will be automatically 'desugared' into a method invocation and eventually passed to [[termApplyTransformer]].<br>
   * The framework will identify such cases by calling [[io.github.effiban.scala2java.spi.predicates.ExtendedPredicates.termNameSupportsNoArgInvocation]]
   *
   * @return if overriden - a transformer which modifies a given [[scala.meta.Term.Name]]<br>
   *         otherwise - the default transformer which doesn't modify anything<br>
   */
  def termNameTransformer(): TermNameTransformer = TermNameTransformer.Empty

  /** Override this method if you need to transform a Scala qualified type into an equivalent Java type
   *
   * @return if overriden - a transformer which changes the qualified type<br>
   *         otherwise - the empty transformer which leaves the qualified type unchanged<br>
   */
  def typeSelectTransformer(): TypeSelectTransformer = TypeSelectTransformer.Empty
}
