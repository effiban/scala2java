package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.{Classifiers, DefnValClassifier, JavaStatClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.factories.{Factories, TemplateChildContextFactory}
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.predicates._
import io.github.effiban.scala2java.core.providers.{CompositeAdditionalImportersProvider, CoreAdditionalImportersProvider}
import io.github.effiban.scala2java.core.renderers.Renderers
import io.github.effiban.scala2java.core.resolvers._
import io.github.effiban.scala2java.core.transformers._
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Assign

class ScalaTreeTraversers(implicit javaWriter: JavaWriter, extensionRegistry: ExtensionRegistry) {

  private implicit lazy val typeInferrers: TypeInferrers = new TypeInferrers(factories, predicates)
  private implicit lazy val classifiers: Classifiers = new Classifiers(typeInferrers)
  private implicit lazy val transformers: Transformers = new Transformers(typeInferrers, predicates)
  private implicit lazy val factories: Factories = new Factories(typeInferrers)
  private lazy val resolvers = new Resolvers()
  private lazy val predicates = new Predicates()
  private lazy val renderers = new Renderers()

  import factories._
  import renderers._
  import resolvers._
  import transformers._
  import typeInferrers._


  private lazy val alternativeTraverser: AlternativeTraverser = new AlternativeTraverserImpl(patTraverser)

  private lazy val annotListTraverser: AnnotListTraverser = new AnnotListTraverserImpl(annotTraverser)

  private lazy val annotTraverser: AnnotTraverser = new AnnotTraverserImpl(initTraverser)

  private lazy val anonymousFunctionTraverser: AnonymousFunctionTraverser = new AnonymousFunctionTraverserImpl(termFunctionTraverser)

  private lazy val applyUnaryTraverser: ApplyUnaryTraverser = new ApplyUnaryTraverserImpl(termNameRenderer, expressionTermTraverser)

  private lazy val argumentListTraverser: ArgumentListTraverser = new ArgumentListTraverserImpl

  private lazy val arrayInitializerTraverser: ArrayInitializerTraverser = new ArrayInitializerTraverserImpl(
    typeTraverser,
    typeRenderer,
    expressionTermTraverser,
    new SimpleArgumentTraverser(expressionTermTraverser),
    argumentListTraverser,
    termTypeInferrer,
    compositeCollectiveTypeInferrer
  )

  private lazy val ascribeTraverser: AscribeTraverser = new AscribeTraverserImpl(
    typeTraverser,
    typeRenderer,
    expressionTermTraverser
  )

  private lazy val assignInvocationArgTraverser: InvocationArgTraverser[Assign] = new AssignInvocationArgTraverser(
    assignLHSTraverser,
    defaultInvocationArgTraverser
  )

  private lazy val assignLHSTraverser: AssignLHSTraverser = new AssignLHSTraverserImpl(expressionTermTraverser)

  private lazy val assignTraverser: AssignTraverser = new AssignTraverserImpl(assignLHSTraverser, expressionTermTraverser)

  private lazy val blockStatTraverser: BlockStatTraverser = new BlockStatTraverserImpl(
    ifTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    statTraverser,
    shouldReturnValueResolver,
    JavaStatClassifier
  )

  private lazy val blockTraverser: BlockTraverser = new BlockTraverserImpl(
    initTraverser,
    blockStatTraverser,
  )

  private lazy val caseClassTraverser: CaseClassTraverser = new CaseClassTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    termParamListTraverser,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val caseTraverser: CaseTraverser = new CaseTraverserImpl(
    patTraverser,
    patRenderer,
    expressionTermTraverser
  )

  private lazy val catchHandlerTraverser: CatchHandlerTraverser = new CatchHandlerTraverserImpl(termParamListTraverser, blockTraverser)

  private lazy val classTraverser: ClassTraverser = new ClassTraverserImpl(
    caseClassTraverser,
    regularClassTraverser,
    new CompositeClassTransformer(),
  )

  private lazy val classOfTraverser: ClassOfTraverser = new ClassOfTraverserImpl(typeTraverser, typeRenderer)

  private lazy val compositeInvocationArgTraverser: InvocationArgTraverser[Term] = new CompositeInvocationArgTraverser(
    assignInvocationArgTraverser,
    defaultInvocationArgTraverser
  )

  private lazy val ctorPrimaryTraverser: CtorPrimaryTraverser = new CtorPrimaryTraverserImpl(CtorPrimaryTransformer, defnDefTraverser)

  private lazy val ctorSecondaryTraverser: CtorSecondaryTraverser = new CtorSecondaryTraverserImpl(CtorSecondaryTransformer, defnDefTraverser)

  private lazy val declDefTraverser: DeclDefTraverser = new DeclDefTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    typeTraverser,
    typeRenderer,
    termNameRenderer,
    termParamListTraverser,
  )

  private lazy val declTraverser: DeclTraverser = new DeclTraverserImpl(
    declValTraverser,
    declVarTraverser,
    declDefTraverser,
    declTypeTraverser)

  private lazy val declTypeTraverser: DeclTypeTraverser = new DeclTypeTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    JavaTreeTypeResolver)

  private lazy val declValTraverser: DeclValTraverser = new DeclValTraverserImpl(
    modListTraverser,
    typeTraverser,
    typeRenderer,
    patTraverser,
    patListRenderer
  )

  private lazy val declVarTraverser: DeclVarTraverser = new DeclVarTraverserImpl(
    modListTraverser,
    typeTraverser,
    typeRenderer,
    patTraverser,
    patListRenderer
  )

  private lazy val defaultInvocationArgTraverser: InvocationArgTraverser[Term] = new DefaultInvocationArgTraverser(
    expressionTermTraverser,
    new CompositeInvocationArgByNamePredicate(CoreInvocationArgByNamePredicate)
  )

  private lazy val funMainApplyTypeTraverser: MainApplyTypeTraverser = mainApplyTypeTraverser(funStandardApplyTypeTraverser)

  private lazy val funStandardApplyTypeTraverser: StandardApplyTypeTraverser = new FunStandardApplyTypeTraverser(
    funTermSelectTraverser,
    typeTraverser,
    typeListRenderer,
    termApplyTypeFunTraverser
  )

  private lazy val defaultTermTraverser: TermTraverser = new DefaultTermTraverser(
    defaultTermRefTraverser,
    defaultTermRefRenderer,
    termApplyTraverser,
    funMainApplyTypeTraverser,
    termApplyInfixTraverser,
    assignTraverser,
    returnTraverser,
    throwTraverser,
    ascribeTraverser,
    termAnnotateTraverser,
    termTupleTraverser,
    blockTraverser,
    ifTraverser,
    termMatchTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    termFunctionTraverser,
    partialFunctionTraverser,
    anonymousFunctionTraverser,
    whileTraverser,
    doTraverser,
    forTraverser,
    forYieldTraverser,
    newTraverser,
    newAnonymousTraverser,
    termPlaceholderTraverser,
    etaTraverser,
    termRepeatedTraverser,
    termInterpolateTraverser,
    litRenderer
  )

  private lazy val defaultTermRefTraverser: DefaultTermRefTraverser = new DefaultTermRefTraverserImpl(
    thisTraverser,
    superTraverser,
    defaultTermSelectTraverser
  )

  private lazy val defaultTermSelectTraverser: DefaultTermSelectTraverser = new DefaultTermSelectTraverserImpl(defaultTermRefTraverser)

  private lazy val defnDefTraverser: DefnDefTraverser = new DefnDefTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    termNameRenderer,
    typeTraverser,
    typeRenderer,
    termParamListTraverser,
    blockTraverser,
    termTypeInferrer,
    new CompositeDefnDefTransformer()
  )

  private lazy val defnTraverser: DefnTraverser = new DefnTraverserImpl(
    defnValTraverser,
    defnVarTraverser,
    defnDefTraverser,
    defnTypeTraverser,
    classTraverser,
    traitTraverser,
    objectTraverser
  )

  private lazy val defnTypeTraverser: DefnTypeTraverser = new DefnTypeTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    typeTraverser,
    typeRenderer,
    typeBoundsTraverser,
    typeBoundsRenderer,
    JavaTreeTypeResolver
  )

  private lazy val defnValOrVarTypeTraverser: DefnValOrVarTypeTraverser = new DefnValOrVarTypeTraverserImpl(
    typeTraverser,
    typeRenderer,
    termTypeInferrer
  )

  private lazy val defnValTraverser: DefnValTraverser = new DefnValTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patTraverser,
    patListRenderer,
    expressionTermTraverser,
    declVarTraverser,
    new CompositeDefnValToDeclVarTransformer,
    new CompositeDefnValTransformer
  )

  private lazy val defnVarTraverser: DefnVarTraverser = new DefnVarTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patTraverser,
    patListRenderer,
    expressionTermTraverser
  )

  private lazy val doTraverser: DoTraverser = new DoTraverserImpl(expressionTermTraverser, blockTraverser)

  private lazy val enumConstantListTraverser: EnumConstantListTraverser = new EnumConstantListTraverserImpl(argumentListTraverser)

  private lazy val etaTraverser: EtaTraverser = new EtaTraverserImpl(expressionTermTraverser)

  private val expressionMainApplyTypeTraverser: MainApplyTypeTraverser =
    mainApplyTypeTraverser(new EvaluatedStandardApplyTypeTraverser(termApplyTraverser))

  private lazy val expressionTermTraverser: TermTraverser = new ExpressionTermTraverser(
    ifTraverser,
    statTraverser,
    termApplyTraverser,
    expressionTermRefTraverser,
    expressionMainApplyTypeTraverser,
    defaultTermTraverser
  )

  private lazy val expressionTermRefTraverser: TermRefTraverser = expressionTermRefTraverser(
    termNameTraverser(expressionTermTraverser, evaluatedInternalTermNameTransformer),
    expressionTermSelectTraverser
  )

  private lazy val expressionTermSelectTraverser: ExpressionTermSelectTraverser = expressionTermSelectTraverser(
    expressionTermTraverser,
    evaluatedInternalTermSelectTransformer
  )

  private lazy val finallyTraverser: FinallyTraverser = new FinallyTraverserImpl(blockTraverser)

  private lazy val forTraverser: ForTraverser = new ForTraverserImpl(termApplyTraverser, ForToTermApplyTransformer)

  private lazy val forYieldTraverser: ForYieldTraverser = new ForYieldTraverserImpl(termApplyTraverser, ForYieldToTermApplyTransformer)

  private lazy val funTermSelectTraverser: FunTermSelectTraverser = new FunTermSelectTraverserImpl(
    defaultTermTraverser,
    termNameRenderer,
    typeTraverser,
    typeListRenderer
  )

  private lazy val funTermRefTraverser: FunTermRefTraverser = new FunTermRefTraverser(
    funTermSelectTraverser,
    defaultTermRefTraverser,
    defaultTermRefRenderer
  )

  private lazy val ifTraverser: IfTraverser = new IfTraverserImpl(expressionTermTraverser, blockTraverser)

  private lazy val importerTraverser: ImporterTraverser = new ImporterTraverserImpl(defaultTermRefTraverser)

  private lazy val importTraverser: ImportTraverser = new ImportTraverserImpl(
    importerTraverser,
    new CompositeImporterExcludedPredicate(CoreImporterExcludedPredicate),
    new CompositeImporterTransformer
  )

  private lazy val initArgTraverserFactory: InitArgTraverserFactory = new InitArgTraverserFactoryImpl(initTraverser)

  private lazy val initListTraverser: InitListTraverser = new InitListTraverserImpl(
    argumentListTraverser,
    initArgTraverserFactory
  )

  private lazy val initTraverser: InitTraverser = new InitTraverserImpl(
    typeTraverser,
    typeRenderer,
    argumentListTraverser,
    compositeInvocationArgTraverser
  )

  private def mainApplyTypeTraverser(standardApplyTypeTraverser: StandardApplyTypeTraverser): MainApplyTypeTraverser =
    new MainApplyTypeTraverserImpl(
      classOfTraverser,
      standardApplyTypeTraverser
    )

  private lazy val modListTraverser: ModListTraverser = new ModListTraverserImpl(annotListTraverser, JavaModifiersResolver)

  private lazy val nameTraverser: NameTraverser = new NameTraverserImpl(typeNameTraverser)

  private lazy val newAnonymousTraverser: NewAnonymousTraverser = new NewAnonymousTraverserImpl(templateTraverser)

  private lazy val newTraverser: NewTraverser = new NewTraverserImpl(
    initTraverser,
    arrayInitializerTraverser,
    ArrayInitializerContextResolver
  )

  private lazy val objectTraverser: ObjectTraverser = new ObjectTraverserImpl(
    modListTraverser,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver)

  private lazy val partialFunctionTraverser: PartialFunctionTraverser = new PartialFunctionTraverserImpl(termFunctionTraverser)

  private lazy val patTraverser: PatTraverser = new PatTraverserImpl(
    PatSeqWildcardTraverser,
    BindTraverser,
    alternativeTraverser,
    PatTupleTraverser,
    PatExtractTraverser,
    PatExtractInfixTraverser,
    PatInterpolateTraverser,
    patTypedTraverser
  )

  private lazy val patTypedTraverser: PatTypedTraverser = new PatTypedTraverserImpl(patTraverser, typeTraverser)

  private lazy val permittedSubTypeNameListTraverser = new PermittedSubTypeNameListTraverserImpl(argumentListTraverser)

  private lazy val pkgStatTraverser: PkgStatTraverser = new PkgStatTraverserImpl(
    classTraverser,
    traitTraverser,
    objectTraverser,
    statTraverser
  )

  private lazy val pkgStatListTraverser: PkgStatListTraverser = new PkgStatListTraverserImpl(
    pkgStatTraverser,
    SealedHierarchiesResolver
  )

  private lazy val pkgTraverser: PkgTraverser = new PkgTraverserImpl(
    defaultTermRefTraverser,
    defaultTermRefRenderer,
    pkgStatListTraverser,
    new CompositeAdditionalImportersProvider(CoreAdditionalImportersProvider)
  )

  private lazy val regularClassTraverser: RegularClassTraverser = new RegularClassTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    templateTraverser,
    ParamToDeclValTransformer,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val returnTraverser: ReturnTraverser = new ReturnTraverserImpl(expressionTermTraverser)

  private lazy val selfTraverser: SelfTraverser = new SelfTraverserImpl(selfRenderer)

  lazy val sourceTraverser: SourceTraverser = new SourceTraverserImpl(statTraverser)

  private lazy val statTraverser: StatTraverser = new StatTraverserImpl(
    statTermTraverser,
    importTraverser,
    importRenderer,
    pkgTraverser,
    defnTraverser,
    declTraverser
  )

  /** When a Stat is a Term.Ref or Term.ApplyType, we need to allow for a possible desugaring into a Term.Apply.
   * Otherwise, the default traversal should be applied.
   */
  private lazy val statTermTraverser: TermTraverser = new FunOverridingTermTraverser(
    expressionTermRefTraverser(
      termNameTraverser(statTermTraverser, evaluatedInternalTermNameTransformer),
      expressionTermSelectTraverser(statTermTraverser, evaluatedInternalTermSelectTransformer)
    ),
    expressionMainApplyTypeTraverser,
    defaultTermTraverser
  )

  private lazy val superTraverser: SuperTraverser = new SuperTraverserImpl(nameTraverser)

  private lazy val templateBodyTraverser: TemplateBodyTraverser = new TemplateBodyTraverserImpl(
    templateChildrenTraverser,
    new TemplateStatTransformerImpl(new CompositeTemplateTermApplyInfixToDefnTransformer, new CompositeTemplateTermApplyToDefnTransformer),
    TemplateChildrenResolver,
    TemplateChildContextFactory
  )

  private lazy val templateChildrenTraverser: TemplateChildrenTraverser = new TemplateChildrenTraverserImpl(
    templateChildTraverser,
    JavaTemplateChildOrdering
  )

  private lazy val templateChildTraverser: TemplateChildTraverser = new TemplateChildTraverserImpl(
    ctorPrimaryTraverser,
    ctorSecondaryTraverser,
    enumConstantListTraverser,
    statTraverser,
    DefnValClassifier,
    JavaStatClassifier
  )

  private lazy val templateTraverser: TemplateTraverser = new TemplateTraverserImpl(
    initListTraverser,
    selfTraverser,
    templateBodyTraverser,
    permittedSubTypeNameListTraverser,
    JavaInheritanceKeywordResolver,
    new CompositeTemplateInitExcludedPredicate(CoreTemplateInitExcludedPredicate)
  )

  private lazy val termAnnotateTraverser: TermAnnotateTraverser = new TermAnnotateTraverserImpl(annotListTraverser, expressionTermTraverser)

  private lazy val termApplyInfixTraverser: TermApplyInfixTraverser = new TermApplyInfixTraverserImpl(
    expressionTermTraverser,
    termApplyTraverser,
    termNameRenderer,
    argumentListTraverser,
    compositeInvocationArgTraverser,
    new CompositeTermApplyInfixToTermApplyTransformer(CoreTermApplyInfixToTermApplyTransformer)
  )

  private lazy val termApplyTraverser: TermApplyTraverser = new TermApplyTraverserImpl(
    termApplyFunTraverser,
    arrayInitializerTraverser,
    argumentListTraverser,
    compositeInvocationArgTraverser,
    termApplyTransformationContextFactory,
    ArrayInitializerContextResolver,
    internalTermApplyTransformer
  )

  /** When the 'fun' term (function name) within a Term.Apply is a Term.Ref or Term.ApplyType -
   * we need to apply the 'fun' traversal, to make sure no desugaring to Term.Apply is performed (which would cause an infinite recursion).
   * In other cases, it should be traversed as an evaluated expression
   */
  private lazy val termApplyFunTraverser: TermTraverser = new FunOverridingTermTraverser(
    funTermRefTraverser,
    funMainApplyTypeTraverser,
    expressionTermTraverser
  )

  /** When the 'fun' term (function name) within a Term.ApplyType, is a Term.Ref -
   * we need to apply the 'fun' traversal, to make sure no desugaring to Term.Apply is performed (which would cause an infinite recursion).
   * In other cases, it should be traversed as an evaluated expression
   */
  private lazy val termApplyTypeFunTraverser: TermTraverser = new FunOverridingTermTraverser(
    funTermRefTraverser,
    expressionMainApplyTypeTraverser,
    expressionTermTraverser
  )

  private lazy val termFunctionTraverser: TermFunctionTraverser = new TermFunctionTraverserImpl(
    termParamTraverser,
    termParamListTraverser,
    statTraverser,
    blockTraverser
  )

  private lazy val termInterpolateTraverser: TermInterpolateTraverser = new TermInterpolateTraverserImpl(TermInterpolateTransformer, termApplyTraverser)

  private lazy val termMatchTraverser: TermMatchTraverser = new TermMatchTraverserImpl(expressionTermTraverser, caseTraverser)

  private def termNameTraverser(termTraverser: => TermTraverser,
                                termNameTransformer: => InternalTermNameTransformer): TermNameTraverser = {
    new TermNameTraverserImpl(
      termNameWithoutRenderTraverser(termTraverser, termNameTransformer),
      termNameRenderer
    )
  }

  private def termNameWithoutRenderTraverser(termTraverser: => TermTraverser,
                                             termNameTransformer: => InternalTermNameTransformer): TermNameWithoutRenderTraverser =
    new TermNameWithoutRenderTraverserImpl(
      termTraverser,
      termNameTransformer,
    )

  private lazy val termParamArgTraverserFactory: TermParamArgTraverserFactory = new TermParamArgTraverserFactoryImpl(termParamTraverser)

  private lazy val termParamListTraverser: TermParamListTraverser = new TermParamListTraverserImpl(
    argumentListTraverser,
    termParamArgTraverserFactory
  )

  private lazy val termParamTraverser: TermParamTraverser = new TermParamTraverserImpl(
    modListTraverser,
    typeTraverser,
    typeRenderer,
    nameTraverser,
    nameRenderer
  )

  private lazy val termPlaceholderTraverser: TermPlaceholderTraverser = new TermPlaceholderTraverserImpl(termPlaceholderRenderer)

  private def expressionTermRefTraverser(termNameTraverser: => TermNameTraverser,
                                         termSelectTraverser: => ExpressionTermSelectTraverser): TermRefTraverser = {
    new ExpressionTermRefTraverser(
      termNameTraverser,
      termSelectTraverser,
      applyUnaryTraverser,
      defaultTermRefTraverser,
      defaultTermRefRenderer
    )
  }

  private lazy val termRepeatedTraverser: TermRepeatedTraverser = new TermRepeatedTraverserImpl(expressionTermTraverser)

  private def expressionTermSelectTraverser(transformedTermTraverser: => TermTraverser,
                                            internalTermSelectTransformer: => InternalTermSelectTransformer): ExpressionTermSelectTraverser =
    new ExpressionTermSelectTraverserImpl(
      qualifierTraverser = expressionTermTraverser,
      transformedTermTraverser = transformedTermTraverser,
      termNameRenderer,
      typeTraverser,
      typeListRenderer,
      qualifierTypeInferrer,
      internalTermSelectTransformer
    )

  private lazy val termTupleTraverser: TermTupleTraverser = new TermTupleTraverserImpl(
    termApplyTraverser,
    TermTupleToTermApplyTransformer
  )

  private lazy val thisTraverser: ThisTraverser = new ThisTraverserImpl(nameTraverser)

  private lazy val throwTraverser: ThrowTraverser = new ThrowTraverserImpl(expressionTermTraverser)

  private lazy val traitTraverser: TraitTraverser = new TraitTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val tryTraverser: TryTraverser = new TryTraverserImpl(
    blockTraverser,
    catchHandlerTraverser,
    finallyTraverser,
    PatToTermParamTransformer
  )

  private lazy val tryWithHandlerTraverser: TryWithHandlerTraverser = new TryWithHandlerTraverserImpl(blockTraverser, finallyTraverser)

  private lazy val typeAnnotateTraverser: TypeAnnotateTraverser = new TypeAnnotateTraverserImpl(typeTraverser)

  private lazy val typeApplyInfixTraverser: TypeApplyInfixTraverser = new TypeApplyInfixTraverserImpl()

  private lazy val typeApplyTraverser: TypeApplyTraverser = new TypeApplyTraverserImpl(typeTraverser)

  private lazy val typeBoundsTraverser: TypeBoundsTraverser = new TypeBoundsTraverserImpl(typeTraverser)

  private lazy val typeByNameTraverser: TypeByNameTraverser = new TypeByNameTraverserImpl(typeApplyTraverser, TypeByNameToSupplierTypeTransformer)

  private lazy val typeExistentialTraverser: TypeExistentialTraverser = new TypeExistentialTraverserImpl(typeTraverser)

  private lazy val typeFunctionTraverser: TypeFunctionTraverser = new TypeFunctionTraverserImpl(typeTraverser, FunctionTypeTransformer)

  private lazy val typeNameTraverser: TypeNameTraverser = new TypeNameTraverserImpl(new CompositeTypeNameTransformer(CoreTypeNameTransformer))

  private lazy val typeParamListTraverser: TypeParamListTraverser = new TypeParamListTraverserImpl(
    argumentListTraverser,
    new SimpleArgumentTraverser(typeParamTraverser)
  )

  private lazy val typeParamTraverser: TypeParamTraverser = new TypeParamTraverserImpl(
    nameTraverser,
    nameRenderer,
    typeParamListTraverser,
    typeBoundsTraverser,
    typeBoundsRenderer
  )

  private lazy val typeProjectTraverser: TypeProjectTraverser = new TypeProjectTraverserImpl(
    typeTraverser,
    typeNameTraverser
  )

  private lazy val typeRefineTraverser: TypeRefineTraverser = new TypeRefineTraverserImpl(typeTraverser)

  private lazy val typeRefTraverser: TypeRefTraverser = new TypeRefTraverserImpl(
    typeNameTraverser,
    typeSelectTraverser,
    typeSingletonTraverser,
    typeProjectTraverser
  )

  private lazy val typeRepeatedTraverser: TypeRepeatedTraverser = new TypeRepeatedTraverserImpl(typeTraverser)

  private lazy val typeSelectTraverser: TypeSelectTraverser = new TypeSelectTraverserImpl(
    defaultTermRefTraverser,
    typeNameTraverser
  )

  private lazy val typeSingletonTraverser: TypeSingletonTraverser = new TypeSingletonTraverserImpl(thisTraverser)

  private lazy val typeTraverser: TypeTraverser = new TypeTraverserImpl(
    typeRefTraverser,
    typeApplyTraverser,
    typeApplyInfixTraverser,
    typeFunctionTraverser,
    typeTupleTraverser,
    typeWithTraverser,
    typeRefineTraverser,
    typeExistentialTraverser,
    typeAnnotateTraverser,
    typeWildcardTraverser,
    typeByNameTraverser,
    typeRepeatedTraverser
  )

  private lazy val typeTupleTraverser: TypeTupleTraverser = new TypeTupleTraverserImpl(
    typeApplyTraverser,
    TypeTupleToTypeApplyTransformer
  )

  private lazy val typeWildcardTraverser: TypeWildcardTraverser = new TypeWildcardTraverserImpl(typeBoundsTraverser)

  private lazy val typeWithTraverser: TypeWithTraverser = new TypeWithTraverserImpl(typeTraverser)

  private lazy val whileTraverser: WhileTraverser = new WhileTraverserImpl(expressionTermTraverser, blockTraverser)
}
