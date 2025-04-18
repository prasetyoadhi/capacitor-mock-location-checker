package io.github.asephermann.plugins.mocklocationchecker

import android.util.Log
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "MockLocationChecker")
class MockLocationCheckerPlugin : Plugin() {
    private val implementation = MockLocationChecker()

    @PluginMethod
    fun checkMock(call: PluginCall)  {
        val whiteList = call.getArray("whiteList")
        val ret = JSObject()

        val result = implementation.checkMock(activity, whiteList.toList())

        ret.put("isMock", result.isMock)
        ret.put("messages", result.messages)
        ret.put("indicated", result.indicated)
        call.resolve(ret)
    }

    @PluginMethod
    fun isLocationFromMockProvider(call: PluginCall)  {
        val ret = JSObject()

        val result = implementation.isLocationFromMockProvider(activity)

        ret.put("value", result)
        call.resolve(ret)
    }
}