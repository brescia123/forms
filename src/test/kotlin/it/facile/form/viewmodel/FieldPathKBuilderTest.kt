package it.facile.form.viewmodel

import it.facile.form.formStorage
import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Test

class FieldPathKBuilderTest {

    val formModel = it.facile.form.formModel(formStorage())

    @Test
    fun testBuildForKey() {
        // Return...
        val fieldPath = FieldPath.Builder.buildForKey(3, formModel)
        val fieldPath2 = FieldPath.Builder.buildForKey(10, formModel)
        val fieldPath3 = FieldPath.Builder.buildForKey(13, formModel)

        // Assert...
        Assert.assertEquals(0, fieldPath!!.pageIndex)
        Assert.assertEquals(0, fieldPath.sectionIndex)
        Assert.assertEquals(1, fieldPath.fieldIndex)
        Assert.assertEquals(0, fieldPath2!!.pageIndex)
        Assert.assertEquals(2, fieldPath2.sectionIndex)
        Assert.assertEquals(2, fieldPath2.fieldIndex)
        Assert.assertEquals(1, fieldPath3!!.pageIndex)
        Assert.assertEquals(0, fieldPath3.sectionIndex)
        Assert.assertEquals(1, fieldPath3.fieldIndex)
    }

    @Test
    fun testBuildForKey_whenNotInFormModel() {
        // Return...
        val fieldPath = FieldPath.Builder.buildForKey(33, formModel)

        // Assert...
        assertNull(fieldPath)
    }
}