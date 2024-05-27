package huahwei_project.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import huahwei_project.quizapp.ui.theme.QuizAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = "categories") {
        composable("categories") {
            CategoriesScreen(
                viewModel = mainViewModel,
                onCategorySelected = { categoryId ->
                    navController.navigate("questions/$categoryId")
                }
            )
        }
        composable("questions/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toInt() ?: 0
            QuestionsOfCategoryScreen(
                viewModel = mainViewModel,
                categoryId = categoryId
            )
        }
    }
}

@Composable
fun CategoriesScreen(viewModel: MainViewModel, onCategorySelected: (Int) -> Unit) {
    val categoriesData by viewModel.categoriesData.collectAsStateWithLifecycle(emptyList())
    val categoriesLoad by viewModel.categoriesLoad.collectAsStateWithLifecycle(initial = false)
    val categoriesError by viewModel.categoriesError.collectAsStateWithLifecycle(initial = false)

    when {
        categoriesLoad -> {
            Text(text = "Loading categories...", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.h6)
        }
        categoriesError -> {
            Text(text = "Error loading categories", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.h6)
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(categoriesData) { category ->
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { onCategorySelected(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionsOfCategoryScreen(viewModel: MainViewModel, categoryId: Int) {
    LaunchedEffect(categoryId) {
        viewModel.getQuestionsForCategory(categoryId)
    }

    val questionsData by viewModel.questionsData.collectAsStateWithLifecycle(emptyList())
    val questionsLoad by viewModel.questionsLoad.collectAsStateWithLifecycle(initial = false)
    val questionsError by viewModel.questionsError.collectAsStateWithLifecycle(initial = false)

    when {
        questionsLoad -> {
            Text(text = "Loading questions...", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.h6)
        }
        questionsError -> {
            Text(text = "Error loading questions", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.h6)
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(questionsData) { question ->
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
