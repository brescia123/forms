# Form library

*Work in progress...*

## Goal

The purpose of this library is to *abstract* the concept of a form composed by fields of various types
(checkboxes, text inputs, pickers etc.) and allow to easily describe their structure and relationships.
The goal is to be able to statically define a form in a single file that contains all the
information needed to build and present it. For this reason the library provides both "model"
components and UI components (Android stuff).

Another feature is the possibility to define how the form should be "serialized" into a data structure
composed by pair of key-values that could be easily converted into a JSON file or something equivalent.

The library is written using [Kotlin](kotlinlang.org) and its only dependency is RxJava.

## Structure

The library is divided between three main logic modules:
- Storage (Kotlin only)
- Model (Kotlin only)
- UI (Android dependencies)

In the future it will be dived in two separated modules allowing us to use the Model/Storage layer
regardless of Android, maybe within the web world, thanks to Kotlin's ability to be compiled into
JavaScript.



### Storage
The Storage is the place where all the values, defaults or entered by the user, are stored. It is
basically a dictionary of keys (simple strings) and Entry with accessory methods used to manipulate it.

```kotlin
// The definition of the entries stored within the Storage
data class Entry(val value: FieldValue, val hidden: Boolean = false, val disabled: Boolean = false)

```
Every entry contains information about its value, visibility and if it is enabled.
Possible values are:
- Text - a simple string
- Bool - a simple boolean
- DateValue - a simple date
- Object - a generic object that should have a key and be describable (DescribableWithKey)
- Missing - represents the absence of the value

### Model
The Model is the key of the library in the sense that it allows you to define the form model that is
its structure.

A form could be structured into pages (PageModel), sections (SectionModel), and fields (FieldModel).




### UI

## Modules interaction

## Add the UI

### Setup
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

The most important thing is `PageAdapter` used to display pages containing sections and fields using
a `Fragment` for every form page. It should be instantiated with the list of `PageViewModel`
available within `init()` method.

Last but not least you have to instantiate a `FormPresenter` passing it the Model and to **remember to call**
`onAttach()` and `onDetach()` methods typically within `onStop()` and `onStart()`.

### Custom Fields layout

In order to use different layouts for fields you have to pass to `PagesAdapter` (or `SectionsAdapter` if you have
a single page) a `FieldsLayouts`. Using Kotlin's named parameters you will be able to provide a layout
id only for the fields that you want to customize:
```kotlin
val fieldsLayouts = FieldsLayouts(
            text = R.layout.custom_text_layout,
            checkBox= R.layout.custom_checkbox_layout)
val adapter = PagesAdapter(pageViewModels, fieldsLayouts, supportFragmentManager)
```
The only rule to follow when implementing custom layouts is that they should include a predefined set
of views with particular ids.

##### Text Field
- `textLabel`
- `textView`
- `textErrorText`
- `textErrorImage`

##### Input Text Field
The input type is a particular one, in the sense that if you use Android support `TextInputLayout` you have to
specify only its id (the view has all the feature to display label, error and value at the same time):
- `inputValue`

If your are using a normal `EditText` and other views to show label, error text and error icon you have to specify the
following ids:
- `inputLabel`
- `inputEditText`
- `inputErrorText`
- `inputErrorImage`

##### CheckBox Field
- `checkboxLabel`
- `checkboxView`
- `checkboxTextView`
- `checkboxErrorImage`

##### Toggle Field
- `toggleLabel`
- `toggleView`
- `toggleTextView`
- `toggleErrorImage`

##### Exception
- `exceptionLabel`
- `exceptionTextView`
- `exceptionImage`

##### Loading
- `loadingLabel`
- `loadingProgressBar`

##### Section Header
- `sectionTitle`