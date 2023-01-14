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

  /** Override this method if you need to modify an [[scala.meta.Importer]] (import statement).<br>
   * '''NOTE regarding precedence''': This transformer will be invoked after [[importerTransformer]]. This means that if
   * this extension, or any other one, excludes an importer that is modified by this transformer -
   * the modification will be ignored.
   *
   * @see [[ImporterTransformer]] for more information on how the framework will invoke this predicate.
   * @return if overriden - a transformer which modifies importers<br>
   *         otherwise - the default transformer which doesn't change anything<br>
   */
  def importerTransformer(): ImporterTransformer = ImporterTransformer.Identity

  /** Override this method if you need to transform a [[scala.meta.Defn.Class]].<br>
   * NOTE that this transformer intended for manipulating the class declaration (e.g. name, visibility, annotations).<br>
   * For manipulating the template part (parents, body) - override one of the other transformers instead.
   *
   * @return if overriden - a transformer which modifies a given class<br>
   *         otherwise - the default transformer which doesn't change anything<br>
   */
  def classTransformer(): ClassTransformer = ClassTransformer.Identity

  /** Override this method if you need to transform a [[scala.meta.Defn.Val]] (`val`, immutable variable definition).<br>
   *
   * @return if overriden - a transformer which transforms a [[scala.meta.Defn.Val]]<br>
   *         otherwise - the default transformer which doesn't modify anything<br>
   */
  def defnValTransformer(): DefnValTransformer = DefnValTransformer.Identity

  /** Override this method if you need to transform a [[scala.meta.Defn.Val]] (`val` definition) into a
   * [[scala.meta.Decl.Var]] (`var` declaration).<br>
   * @see [[DefnValToDeclVarTransformer]] for a usage example.
   *
   * @return if overriden - a transformer which transforms a [[scala.meta.Defn.Val]] into a [[scala.meta.Decl.Var]] where applicable<br>
   *         otherwise - the default transformer which never transforms (returns `None`)<br>
   */
  def defnValToDeclVarTransformer(): DefnValToDeclVarTransformer = DefnValToDeclVarTransformer.Empty

  /** Override this method if you need to modify a [[scala.meta.Defn.Def]] (method definition)
   *
   * @return if overriden - a transformer which modifies a given [[scala.meta.Defn.Def]]<br>
   *         otherwise - the default transformer which doesn't modify anything<br>
   */
  def defnDefTransformer(): DefnDefTransformer = DefnDefTransformer.Identity

  /** Override this method if you need to transform a [[scala.meta.Term.ApplyType]] (parameterized type application) into a
   * [[scala.meta.Term.Apply]] (method invocation).<br>
   * @see [[TermApplyTypeToTermApplyTransformer]] for a usage example.
   *
   * @return if overriden - a transformer which transforms a [[scala.meta.Term.ApplyType]] into a [[scala.meta.Term.Apply]] where applicable
   *         otherwise - the default transformer which never transforms (returns `None`)
   */
  def termApplyTypeToTermApplyTransformer(): TermApplyTypeToTermApplyTransformer = TermApplyTypeToTermApplyTransformer.Empty

  /** Override this method if you need to transform a [[scala.meta.Term.Apply]] (method invocation) into a [[scala.meta.Defn.Class]] .<br>
   * '''NOTE regarding precedence''': This transformer will be invoked before [[termApplyToDefnDefTransformer()]].
   *
   * @see [[TermApplyToClassTransformer]] for as usage example.
   * @return if overriden - a transformer which transforms a [[scala.meta.Term.Apply]] into a [[scala.meta.Defn.Class]] where applicable<br>
   *         otherwise - the default transformer which never transforms (returns `None`)
   */
  def termApplyToClassTransformer(): TermApplyToClassTransformer = TermApplyToClassTransformer.Empty

  /** Override this method if you need to transform a [[scala.meta.Term.Apply]] (method invocation) into a [[scala.meta.Defn.Def]] (method definition).<br>
   * '''NOTE regarding precedence''': This transformer will be invoked after [[termApplyToClassTransformer]] but before [[termApplyTransformer]].
   *
   * @see [[TermApplyToDefnDefTransformer]] for as usage example.
   * @return if overriden - a transformer which transforms a [[scala.meta.Term.Apply]] into a [[scala.meta.Defn.Def]] where applicable<br>
   *         otherwise - the default transformer which never transforms (returns `None`)
   */
  def termApplyToDefnDefTransformer(): TermApplyToDefnDefTransformer = TermApplyToDefnDefTransformer.Empty

  /** Override this method if you need to modify a [[scala.meta.Term.Apply]] (method invocation).<br>
   * '''NOTE regarding precedence''': This transformer will be invoked after all other transformers that try to change the type of the [[scala.meta.Term.Apply]]
   *
   * @return if overriden - a transformer which modifies a given [[scala.meta.Term.Apply]]<br>
   *         otherwise - the default transformer which doesn't modify anything<br>
   */
  def termApplyTransformer(): TermApplyTransformer = TermApplyTransformer.Identity

  /** Override this method if you need to modify a [[scala.meta.Term.Select]] (qualified name).<br>
   * Note: This can be a multi-purpose transformer as qualified names can appear in various language elements such as:
   *   - imports
   *   - method invocations
   *   - inner member selections
   *   - others...
   *
   * @return if overriden - a transformer which modifies a given [[scala.meta.Term.Select]]<br>
   *         otherwise - the default transformer which doesn't modify anything<br>
   */
  def termSelectTransformer(): TermSelectTransformer = TermSelectTransformer.Identity

  /** Override this method if you need to transform a Scala type name into an equivalent Java one
   *
   * @return if overriden - a transformer which changes the type name<br>
   *         otherwise - the default which leaves the type name unchanged<br>
   */
  def typeNameTransformer(): TypeNameTransformer = TypeNameTransformer.Identity
}