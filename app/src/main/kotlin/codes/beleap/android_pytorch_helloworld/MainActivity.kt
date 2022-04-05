package codes.beleap.android_pytorch_helloworld

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                Button(onClick = {
                    var bitmap: Bitmap? = null
                    var module: Module? = null
                    try {
                        bitmap = BitmapFactory.decodeStream(assets.open("image.jpg"))
                        val modulePath = assetFilePath(this@MainActivity, "model.ptl")
                        Log.d("PyTorchHelloWorld", modulePath.toString())
                        module = LiteModuleLoader.load(modulePath)
                    } catch (e: IOException) {
                        Log.e("PyTorchHelloWorld", "Error reading assets", e)
                        finish()
                    }
                }) {
                    Text(text = "Test Btn")
                }
            }
        }
    }

    companion object {
        fun assetFilePath(context: Context, assetName: String): String? {
            val file = File(context.filesDir, assetName)
            if (file.exists() && file.length() > 0) {
                return file.absolutePath
            }

            context.assets.open(assetName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(4 * 1024)
                    var read = inputStream.read(buffer)
                    while (read != -1) {
                        outputStream.write(buffer, 0, read)
                        read = inputStream.read(buffer)
                    }
                    outputStream.flush()
                }
                return file.absolutePath
            }
        }
    }
}