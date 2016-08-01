package it.facile.form.model

import it.facile.form.FormStorageK
import it.facile.form.formModel
import it.facile.form.formStorage
import it.facile.form.viewmodel.FieldPathK
import it.facile.form.viewmodel.FieldValueK
import it.facile.form.viewmodel.FieldViewModelK
import org.junit.Assert.assertEquals
import org.junit.Test
import rx.observers.TestSubscriber

class FormModelKTest {

    val storage: FormStorageK = formStorage()
    val model: FormModelK = formModel(storage)

    @Test
    fun testObserve_noNotification_ifNotPresentInModel() {
        // Return...
        val testSubscriber = TestSubscriber<Pair<FieldPathK, FieldViewModelK>>()
        model.observeChanges().subscribe(testSubscriber)
        storage.putValue(44, FieldValueK.Bool(true))

        // Assert...
        testSubscriber.assertNoValues()
    }

    @Test
    fun testNotifyValueChanged() {
        // Return...
        val testSubscriber = TestSubscriber<Pair<FieldPathK, FieldViewModelK>>()
        model.observeChanges().subscribe(testSubscriber)
        storage.putValue(7, FieldValueK.Bool(true))

        // Assert...
        testSubscriber.assertValueCount(1)
        val onNextEvents = testSubscriber.onNextEvents
        assertEquals(0, onNextEvents[0].first.pageIndex)
        assertEquals(1, onNextEvents[0].first.sectionIndex)
        assertEquals(2, onNextEvents[0].first.fieldIndex)
    }
}