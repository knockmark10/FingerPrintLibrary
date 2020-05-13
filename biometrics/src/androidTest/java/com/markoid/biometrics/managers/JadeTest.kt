package com.markoid.biometrics.managers

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.markoid.biometrics.callbacks.BiometricValidationCallback
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class JadeTest {

    // -----------START HELPER---------------
    @MockK
    lateinit var mValidationListenerMock: BiometricValidationCallback

    // -----------END HELPER-----------------

    private lateinit var SUT: Jade

    private lateinit var mContext: Context

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        this.mContext = InstrumentationRegistry.getInstrumentation().context
        this.SUT = Jade.Builder.build(this.mContext)
    }

    @Test
    fun validate_mustCallAnyMethod() {
        // Arrange
        this.SUT.registerValidationListener(mValidationListenerMock)
        // Act
        this.SUT.validate()
        // Assert
        verify { mValidationListenerMock.onBiometricResult(any()) }
    }

}