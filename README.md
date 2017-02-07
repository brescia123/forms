# Form library

[![](https://jitpack.io/v/brescia123/forms.svg)](https://jitpack.io/#brescia123/forms)
[![Build Status](https://travis-ci.org/brescia123/forms.svg?branch=master)](https://travis-ci.org/brescia123/forms)

*Work in progress...*

## Goal

The purpose of this library is to *abstract* the concept of a form composed by fields of various types
(checkboxes, text inputs, pickers etc.) and allow to easily describe their structure and relationships.
The goal is to be able to statically define a form in a single file that contains all the
information needed to build and present it. For this reason the library provides both "model"
components and UI components (Android stuff).

The library is written in [Kotlin](kotlinlang.org) and its only dependency is RxJava.

## Structure

The library is divided into three main logic modules:
- Storage (Kotlin only)
- Model (Kotlin only)
- UI (Android dependencies)

In the future it will be dived in two separated modules to allow the usage of the the Model/Storage layer
regardless of Android, maybe within the web world, thanks to Kotlin's ability to be compiled into
JavaScript.


### Storage
The Storage is the place where all the values, both defaults and entered by the user, are stored. It is
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
- Object - a generic object that should have at least a key and be describable (DescribableWithKey)
- Missing - represents the absence of the value

### Model
The Model is the key of the library. It contains how the model is structured and how it should behave. 

A form could be structured into pages (PageModel), sections (SectionModel), and fields (FieldModel).

#### DSL

The library provides a DSL written thanks to Kotlin Type-safe builders (you can read more about it [here](https://engineering.facile.it/blog/eng/kotlin-dsl)) that allows you to statically define the stucture and the appearance of the form. The syntax is the following:

```kotlin
form {
    page("page_title") {
        section("section_title") {
            field(key = "field_key") {
                [type_of_field]("field_lable") {
                    ...
                    field_configuration_options
                    ...
                }
            }
        }
    }
}
```

And here a more concrete example:

```kotlin
val FORM = form {
    page("Page 1 Title") {
        section("Section 1 Title") {
            field(key = "fieldKey1") {
                checkbox("Checkbox Field Label") {
                    boolToStringConverter = { if (it == true) "Yes" else "No" }
                    rules = { listOf(NotMissing()) }
                }
            }
            field(key = "fieldKey2") {
                picker("Picker Field Label") {
                    placeHolder = "Select a value"
                    possibleValues = Available(listOf(
                            1 keyTo "Value1",
                            2 keyTo "Value2",
                            3 keyTo "Value3"))
                    representation = IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                }
            }
        }
        section("Section 2 Title") {
            field(key = "fieldKey3") {
                picker("Picker Field Label") {
                    placeHolder = "Select a value"
                    possibleValues = ToBeRetrieved(someWebService.getValues())
                    representation = IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                }
            }
            field(key = "fieldKey4") {
                input("Input Text Field Label") {
                    inputTextType = InputTextType.EMAIL
                    rules = { listOf(IsEmail()) }
                }
            }
            field(key = "fieldKey") {
				        empty("Empty Field")
            }
        }
		    section("Section 3 Title") {
            field(key = "fieldKey6") {
                toggle("Toggle Field Label") {
                    boolToStringConverter = { if (it == true) "OK" else "KO" }
                    rules = { listOf(NotMissing()) }
                    representation = ALWAYS representAs SIMPLE_KEY_TO_VALUE
                }
            }
        }
    }
}
```
And here is how it is rendered on the UI:

![Form screenshot](https://github.com/brescia123/forms/blob/master/form_screen.png)

The builder function `form()` accepts also some optional parameters:

- `storage: FormStorageApi` -> a pre-populated storage (default is empty)
- `actions: List<Pair<String, (FieldValue, FormStorageApi) -> Unit>>` -> a list of actions associated with fields key: an action is a function that takes the `FieldValue` that triggered the action, a link to the storage and doesn't return anything. Actions are useful to create dependencies between fields.
- `workScheduler: Scheduler = Schedulers.io()` -> since some fields could depend on some network call that retrieves required informations the form has an associated [RxJava scheduler](https://github.com/ReactiveX/RxJava/wiki/Scheduler) on which it will run the job (default is `Schedulers.io()`)

#### Form state

The FormModel provides an Observable to let the user be notified about state changes. The form could be in four different states:
- `NOT_INITIALIZED // There are some missing informations to complete the form that has to be retrieved`
- `READY // The form is ready and has all the needed informations`
- `LOADING // The form is downloading the needed informations`
- `ERROR // There was some error during the dowload`

#### Fields configurations

TODO

### UI

Work in progress...

TODO:

- [x] Sections RecyclerView
- [ ] Pages ViewAdapter

### Setup

#### Single page form

The library provides a `FormRecyclerView`: it is a simple `RecyclerView` with `LinearLayoutManager` as default layout manager and the `FormItemAnimator` as default item animator. (NB: to use a custom ItemAnimator make sure to extends
`FormDefaultItemAnimator` to prevent problems with Input Text fields)

In order to use the form UI with a single page you have to add `FormRecyclerView` to your layout
```
<it.facile.form.ui.FormRecyclerView
    android:layout_width="match_parent"
    android:layout_height="match_parent">
</it.facile.form.ui.FormRecyclerView>
```
and pass it the `FormModel`:
```kotlin
formRecyclerView.formModel = formModel
```
You also need to provide it with a `SectionsAdapter` constructed with the list of `SectionViewModel` that you can ask to the FormModel.


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
