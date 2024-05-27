package huahwei_project.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import huahwei_project.quizapp.ui.theme.QuizAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            QuizAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CategoryListScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CategoryListScreen(viewModel: MainViewModel) {
    val categoriesData by viewModel.categoriesData.collectAsStateWithLifecycle(emptyList())
    val categoriesLoad by viewModel.categoriesLoad.collectAsStateWithLifecycle(initial = false)
    val categoriesError by viewModel.categoriesError.collectAsStateWithLifecycle(initial = false)

    when {
        categoriesLoad -> {
            Text(text = "Loading...", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.h6)
        }
        categoriesError -> {
            Text(text = "Error loading categories", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.h6)
        }
        else -> {
            CategoryList(categories = categoriesData)
        }
    }
}

@Composable
fun CategoryList(categories: List<QuestionCategory>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            Text(
                text = category.name,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QuizAppTheme {
        CategoryList(categories = listOf(
            QuestionCategory(id = 9, name = "General Knowledge"),
            QuestionCategory(id = 10, name = "Entertainment: Books"),
            QuestionCategory(id = 11, name = "Entertainment: Film")
        ))
    }
}
