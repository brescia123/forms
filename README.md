# Form library

*Work in progress...*

## Model definition

## Add the UI
In order to use the form UI, add `FormRecyclerView` to your layout
```
<it.facile.form.ui.FormRecyclerView
    android:layout_width="match_parent"
    android:layout_height="match_parent">
</it.facile.form.ui.FormRecyclerView>
```
`FormRecyclerView` is a simple `RecyclerView` with `LinearLayoutManager` as default layout manager and the
`FormItemAnimator` as default item animator. (NB: to use a custom ItemAnimator make sure to extends
`FormDefaultItemAnimator` to prevent problems with Input Text fields)

Next you have should have your view (an `Activity`, a `Fragment` or whatever you want to contain the form)
implement `FormView` interface and implement its methods:

```kotlin
// Where your view receives the list of PageViewModel and initalizes the
// components (recyclerview, adapters..) used to display them
fun init(pageViewModels: List<PageViewModel>)
// Where you should update a field with new informations
fun updateField(path: FieldPath,
                viewModel: FieldViewModel,
                sectionViewModel: SectionViewModel)
// With this method your should expose an Observable to that emits value entered by the user
fun observeValueChanges(): Observable<FieldPathWithValue>
```

The most important thing is `SectionAdapter` (?`PageAdapter`?) used to display sections and fields within
a single page of the form. It should be instantiated with the list of `SectionViewModel` available trough
`PageViewModel` within `init()` method.

Last but not least you have to instantiate a `FormPresenter` passing it the Model and to **remember to call**
`attach()` and `detach()` methods typically within `onStop()` and `onStart()`.

