package it.facile.form

import it.facile.form.storage.FormStorage
import it.facile.form.storage.FieldValue
import org.junit.Assert
import org.junit.Test
import rx.observers.TestSubscriber

class FormStorageKTest {

    val storage: FormStorage = formStorage()

    @Test
    fun testPutValue() {
        // When...
        val testSub: TestSubscriber<Int> = TestSubscriber()

        // Return...
        storage.observe().subscribe(testSub)
        storage.putValue(2, FieldValue.Text())
        storage.putValue(2, FieldValue.Text("ok"))
        storage.putValue(3, FieldValue.Bool())

        // Assert...
        testSub.assertNoErrors()
        testSub.assertValues(2, 2, 3)
        Assert.assertEquals(FieldValue.Bool(), storage.getValue(3))
        Assert.assertEquals(FieldValue.Text("ok"), storage.getValue(2))
    }
}