package codes.beleap.android_pytorch_helloworld

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.MemoryFormat
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
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
                var result by remember {
                    mutableStateOf("")
                }
                Button(onClick = {
                    var module: Module? = null
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = BitmapFactory.decodeStream(assets.open("image.jpg"))
                        val modulePath = assetFilePath(this@MainActivity, "model.ptl")
                        module = LiteModuleLoader.load(modulePath)
                    } catch (e: IOException) {
                        Log.e("PyTorchHelloWorld", "Error reading assets", e)
                        finish()
                    }

                    val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                        bitmap,
                        TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                        TensorImageUtils.TORCHVISION_NORM_STD_RGB,
                        MemoryFormat.CHANNELS_LAST,
                    )
                    module?.let {
                        val outputTensor = it.forward(IValue.from(inputTensor)).toTensor()
                        val scores = outputTensor.dataAsFloatArray

                        var maxScore = -Float.MAX_VALUE
                        var maxScoreIdx = -1
                        for ((idx, score) in scores.withIndex()) {
                            if (score > maxScore) {
                                maxScore = score
                                maxScoreIdx = idx
                            }
                        }

                        val className = ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx]

                        Log.i("PyTorchHelloWorld", className)
                        result = className
                    }
                }, modifier = Modifier.padding(16.dp)) {
                    Text(text = "Infer")
                }

                if (result.isNotEmpty()) {
                    Text(text = result, modifier = Modifier.padding(16.dp))
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