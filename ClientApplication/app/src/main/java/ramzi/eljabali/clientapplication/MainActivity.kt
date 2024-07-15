package ramzi.eljabali.clientapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ramzi.eljabali.aidlmockapp.IMyAidlInterface
import ramzi.eljabali.clientapplication.ui.theme.ClientApplicationTheme

class MainActivity : ComponentActivity() {
    private var iMyAidlInterface: IMyAidlInterface? = null
    private var isBound = false
    private var didComeFromService = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.i("Client-MainActivity", "Connection to service established")
            iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service)
            isBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e("Client-MainActivity", "Service has unexpectedly disconnected")
            iMyAidlInterface = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if the activity was launched from a deep link from the service
        handleIntent(intent)

        // If not coming from service, initiate deep linking to the service
        if (!didComeFromService) {
            val deepLinkUri = Uri.parse("example://content/path/to/content")
            val deepLinkIntent = Intent(Intent.ACTION_VIEW, deepLinkUri)

            val packageManager = packageManager
            val resolveInfo = packageManager.resolveActivity(deepLinkIntent, 0)
            if (resolveInfo != null) {
                startActivity(deepLinkIntent)
            } else {
                // Handle case where no app can handle the intent
                // Optionally, redirect to Play Store or show an error
                Log.e("Client-MainActivity", "No activity found to handle deep link intent")
            }
        }

        setContent {
            var greetingText by remember { mutableStateOf("Hello!") }
            var mathText by remember { mutableStateOf("") }
            ClientApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    padding
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = greetingText)
                        Button(onClick = {
                            if (isBound) {
                                greetingText = iMyAidlInterface?.greet("Ramzi") ?: "Couldn't call service"
                                mathText = iMyAidlInterface?.add(35, 7).toString()
                            } else {
                                greetingText = "Service not bound"
                            }
                        }) {
                            Text("Click me")
                        }
                        Text(text = mathText)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(mConnection)
            Log.i("Client-MainActivity", "Service unbound")
            isBound = false
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        val data: Uri? = intent.data

        if (Intent.ACTION_VIEW == action && data != null) {
            val path = data.path
            if ("/path/to/client" == path) {
                // Handle deep link from service
                didComeFromService = true
                Log.i("Client-MainActivity", "Deep link received from service")
                // Start and bind to the service
                val serviceIntent = Intent("ramzi.eljabali.aidlmockapp.MyAidlService")
                serviceIntent.setPackage("ramzi.eljabali.aidlmockapp")
                startService(serviceIntent)
                bindService(serviceIntent, mConnection, BIND_AUTO_CREATE)
            } else {
                // Handle other paths if necessary
                Log.i("Client-MainActivity", "Unknown path: $path")
            }
        }
    }
}
