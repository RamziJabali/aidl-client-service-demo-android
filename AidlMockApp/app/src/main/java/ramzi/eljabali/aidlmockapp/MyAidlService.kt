package ramzi.eljabali.aidlmockapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyAidlService : Service() {

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    private val binder = object : IMyAidlInterface.Stub() {
        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {
            Log.i(
                "IMyAidlInterface -> basicTypes()",
                "AIDL supports the following types: Int, Long, Boolean, Float, Double, String."
            )
        }

        override fun add(x: Int, y: Int): Int {
            Log.i("IMyAidlInterface -> add()", "x value $x, y value $y, total ${x + y}")
            return x + y
        }

        override fun greet(name: String?): String {
            Log.i("IMyAidlInterface -> greet()", "hello $name, welcome to AIDL!")
            return "Hello $name, welcome to AIDL!"
        }

    }

}