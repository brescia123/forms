package it.facile.form

import it.facile.form.viewmodel.FieldValueK
import org.junit.Assert
import org.junit.Test
import rx.observers.TestSubscriber

class FormStorageKTest {

    val storage: FormStorageK = formStorage()

    @Test
    fun testPutValue() {
        // When...
        val testSub: TestSubscriber<Int> = TestSubscriber()

        // Return...
        storage.observe().subscribe(testSub)
        storage.putValue(2, FieldValueK.Text())
        storage.putValue(2, FieldValueK.Text("ok"))
        storage.putValue(3, FieldValueK.Bool())

        // Assert...
        testSub.assertNoErrors()
        testSub.assertValues(2, 2, 3)
        Assert.assertEquals(FieldValueK.Bool(), storage.getValue(3))
        Assert.assertEquals(FieldValueK.Text("ok"), storage.getValue(2))
    }
}