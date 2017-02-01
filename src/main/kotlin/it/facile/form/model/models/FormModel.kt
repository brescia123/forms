package it.facile.form.model.models

import it.facile.form.model.CouldHaveLoadingError
import it.facile.form.model.FieldConfig
import it.facile.form.model.FieldRulesValidator
import it.facile.form.model.FieldsContainer
import it.facile.form.model.configurations.FieldConfigDeferred
import it.facile.form.model.configurations.FieldConfigPicker
import it.facile.form.not
import it.facile.form.storage.FieldPossibleValues.Available
import it.facile.form.storage.FieldPossibleValues.ToBeRetrieved
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorageApi
import it.facile.form.ui.viewmodel.FieldPath
import it.gbresciani.jsonnode.Node.ObjectNode
import rx.Observable
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject

data class FormModel(val storage: FormStorageApi,
                     val pages: MutableList<PageModel> = arrayListOf<PageModel>(),
                     private val actions: MutableList<Pair<String, (FieldValue, FormStorageApi) -> Unit>>,
                     val workScheduler: Scheduler = Schedulers.io(),
                     val resultScheduler: Scheduler = AndroidSchedulers.mainThread()) : FieldsContainer {

    var state: FormState = FormState.LOADING

    /** Enum representing the possible form state:
     * - READY: all the dynamic values have been loaded successfully
     * - LOADING: there is some dynamic value still loading
     * - ERROR: some load error occurred
     */
    enum class FormState {
        READY,
        LOADING,
        ERROR
    }

    private val formStateSubject = PublishSubject.create<FormState>()
    private val interestedKeys: MutableMap<String, MutableList<String>> by lazy { observeActionsKeys() }

    override fun fields(): List<FieldModel> = pages.fold(mutableListOf<FieldModel>(), { models, page ->
        models.addAll(page.fields())
        models
    })

    fun getPage(path: FieldPath): PageModel = pages[path.pageIndex]
    fun getSection(path: FieldPath): SectionModel = pages[path.pageIndex].sections[path.sectionIndex]
    fun getField(path: FieldPath): FieldModel = pages[path.pageIndex].sections[path.sectionIndex].fields[path.fieldIndex]

    /** Observable that emit [FieldPath] associated to fields that need to be updated */
    fun observeChanges(): Observable<FieldPath> = storage.observe()
            .doOnNext { if (it.second) executeFieldAction(it.first) } // Execute all side effects actions related to the key if user made
            .map { it.first } // Get rid of userMade boolean information
            .flatMap { Observable.just(it).mergeWith(Observable.from(interestedKeys[it] ?: emptyList())) } // Merge with interested keys
            .flatMap { Observable.from(findFieldPathByKey(it)) } // Emit for every FieldPath associated to the field key
            .retry() // Resubscribe if some errors occurs to continue the flow of notifications

    /** Observable that emit [FormState] every time it changes */
    fun observeFormState(): Observable<FormState> = formStateSubject.asObservable().distinctUntilChanged()

    /** Notify the model of a field value change generated from the outside (that is a user made
     * change and not for the example one result of an field Action) */
    fun notifyValueChanged(path: FieldPath, value: FieldValue): Unit {
        if (not(contains(path))) return // The model does not contain the given path
        val key = findFieldModelByFieldPath(path).key
        storage.putValue(key, value, true)
    }

    /** Return the representation of this form */
    fun getNodeRepresentation(): ObjectNode = fields()
            .map { it.buildRepresentation(storage) } // Build representation for every field
            .filter { it != null }.map { it!! } // Filter fields without representation
            .reduce(ObjectNode::merge)

    /** Load all the [FieldConfigDeferred] and [ToBeRetrieved] that has to be loaded  */
    fun loadDynamicValues() {
        changeState(FormState.LOADING)
        fields().forEach {
            val config = it.configuration
            if (config is CouldHaveLoadingError) {
                config.hasErrors = false
                storage.ping(it.key)
            }
        }
        Observable
                .mergeDelayError(possibleValues(), deferredConfigs())
                .subscribeOn(workScheduler)
                .observeOn(resultScheduler)
                .subscribe(
                        {},
                        { changeState(FormState.ERROR) },
                        { changeState(FormState.READY) })
    }

    fun hasFormError() = fields()
            .filter {
                val hasError = (it.configuration as? FieldRulesValidator)?.isValid(storage.getValue(it.key), storage) != null
                hasError and not(storage.isHidden(it.key))
            }
            .isNotEmpty()

    fun addAction(pair: Pair<String, (FieldValue, FormStorageApi) -> Unit>) {
        actions.add(pair)
    }

    private fun changeState(newState: FormState) {
        state = newState
        formStateSubject.onNext(state)
    }

    private fun executeFieldAction(key: String) =
            actions.filter { it.first == key }.forEach { it.second(storage.getValue(key), storage) }

    private fun contains(path: FieldPath): Boolean = path.pageIndex < pages.size &&
            path.sectionIndex < pages[path.pageIndex].sections.size &&
            path.fieldIndex < pages[path.pageIndex].sections[path.sectionIndex].fields.size

    private fun contains(key: String): Boolean = findFieldPathByKey(key).isNotEmpty()

    private fun findFieldModelByFieldPath(fieldPath: FieldPath): FieldModel =
            pages[fieldPath.pageIndex].sections[fieldPath.sectionIndex].fields[fieldPath.fieldIndex]

    private fun findFieldPathByKey(key: String): List<FieldPath> = FieldPath.buildForKey(key, this)

    private fun observeActionsKeys(): MutableMap<String, MutableList<String>> {
        val interested: MutableMap<String, MutableList<String>> = mutableMapOf()
        for ((toBeNotifiedKey, _ignored, config) in fields()) {
            if (config is FieldRulesValidator) {
                config.rules(storage).map {
                    it.observedKeys()
                            .map { it.key }
                            .map {
                                interested[it]?.add(toBeNotifiedKey) ?: interested.put(it, mutableListOf(toBeNotifiedKey))
                            }
                }
            }
        }
        return interested
    }

    private fun replaceConfig(key: String, newConfig: FieldConfig) {
        val paths = findFieldPathByKey(key)
        paths.map {
            pages[it.pageIndex]
                    .sections[it.sectionIndex]
                    .fields[it.fieldIndex] = FieldModel(key, findFieldModelByFieldPath(it).representation, newConfig)
        }
    }

    private fun possibleValues() = Observable.mergeDelayError(
            fields().filter {
                it.configuration is FieldConfigPicker
                        // Check if PossibleValues is ToBeRetrieved (look first into storage and then into configuration
                        && (storage.getPossibleValues(it.key) ?: it.configuration.possibleValues) is ToBeRetrieved
            }
                    .map {
                        val key = it.key
                        val config = it.configuration as FieldConfigPicker
                        val possibleValues = config.possibleValues as ToBeRetrieved
                        possibleValues.possibleValuesSingle
                                .doOnSuccess {
                                    config.hasErrors = false
                                    storage.putPossibleValues(key, Available(it))
                                    // If there is a match between preselectKey and possible values keys put the selected value into the storage
                                    it.find { it.key == possibleValues.preselectKey }?.let {
                                        storage.putValue(key, FieldValue.Object(it))
                                    }
                                }
                                .doOnError {
                                    config.hasErrors = true
                                    storage.ping(key)
                                }
                    }
                    .map { it.toObservable() })


    private fun deferredConfigs() = Observable.mergeDelayError(
            fields().filter { it.configuration is FieldConfigDeferred }
                    .map {
                        val key = it.key
                        val config = it.configuration as FieldConfigDeferred
                        config.deferredConfig
                                .doOnSuccess { // Replace config with the loaded one and notify
                                    replaceConfig(key, it)
                                    storage.ping(key)
                                }
                                .doOnError { // Make config show the error and notify
                                    config.hasErrors = true
                                    storage.ping(key)
                                }
                    }
                    .map { it.toObservable() })

}