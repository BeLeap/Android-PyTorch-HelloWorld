package codes.beleap.android_pytorch_helloworld

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.ui.tooling.preview.Preview
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.File
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var bitmap: Bitmap? = null
        var module: Module? = null
        try {
            bitmap = BitmapFactory.decodeStream(assets.open("image.jpg"))
            module = LiteModuleLoader.load(assetFilePath(this, "model.ptl"))
        } catch (e: IOException) {
            Log.e("PyTorchHelloWorld", "Error reading assets", e)
            finish()
        }

        setContent {
            Text("asdfa")
        }
    }

    @Preview()
    @Composable()
    fun MainApp() {}

    companion object {
        fun assetFilePath(context: Context, assetName: String): String? = File(context.filesDir, assetName).absolutePath
    }
}