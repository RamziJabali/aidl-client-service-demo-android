package ramzi.eljabali.aidlmockapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ramzi.eljabali.aidlmockapp.ui.theme.AidlMockAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Start the service
        val serviceIntent = Intent(this, MyAidlService::class.java)
        startService(serviceIntent)
        // Create an intent with the deep link URI
        val deepLinkUri = Uri.parse("client-example://client/path/to/client")
        val deepLinkIntent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Check if there is an app that can handle this intent
        val packageManager = packageManager
        val resolveInfo = packageManager.resolveActivity(intent, 0)
        if (resolveInfo != null) {
            startActivity(deepLinkIntent)
        } else {
            // Handle the case where no app can handle the intent
            // Optionally, redirect to the Play Store or show an error message
        }
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        val data: Uri? = intent.data

        if (Intent.ACTION_VIEW == action && data != null) {
            // Parse the URI and navigate to the appropriate content in your app
            when (val path = data.path) {
                "/path/to/content" -> {
                    // Navigate to the specific content
                    Log.i("MainActivity", "Deep link received with path: $path")
                }

                else -> {
                    // Handle other paths
                    Log.i("MainActivity", "Unknown path: $path")
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AidlMockAppTheme {
        Greeting("Android")
    }
}