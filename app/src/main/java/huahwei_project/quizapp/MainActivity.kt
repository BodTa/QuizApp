package huahwei_project.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import huahwei_project.quizapp.ui.theme.QuizAppTheme
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        fetchQuizData().start()
        super.onCreate(savedInstanceState)
        setContent {
            QuizAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                }
            }
        }
    }

    private  fun fetchQuizData():Thread{
        return Thread(){
            val url = URL("https://opentdb.com/api.php?amount=20")
            val connection = url.openConnection() as HttpURLConnection
            if(connection.responseCode == 200){
                val inputSystem = connection.inputStream
                println(inputSystem.toString())
            }
            else{
                println("Cannot connect")
            }
        }
    }
}
