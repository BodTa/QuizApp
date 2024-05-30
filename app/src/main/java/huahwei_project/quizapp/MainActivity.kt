package huahwei_project.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import huahwei_project.quizapp.ui.theme.QuizAppTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment


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

    NavHost(navController = navController, startDestination = "home") {

        composable("home"){
            HomeScreen(onNavigateToCategories = {
                navController.navigate("categories")
            })
        }

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
fun HomeScreen(onNavigateToCategories:()->Unit){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Quiz App", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(Dp(16.toFloat())))
            Button(onClick = onNavigateToCategories) {
                Text(text = "Start Quiz")
            }
        }
    }
}
@Composable
fun CategoriesScreen(viewModel: MainViewModel, onCategorySelected: (Int) -> Unit) {
    val categoriesData: List<QuestionCategory> by viewModel.categoriesData.collectAsState(emptyList())
    val categoriesLoad: Boolean by viewModel.categoriesLoad.collectAsState(false)
    val categoriesError: Boolean by viewModel.categoriesError.collectAsState(false)

    when {
        categoriesLoad -> {
            Text(text = "Loading categories...", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.headlineMedium)
        }
        categoriesError -> {
            Text(text = "Error loading categories", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.headlineMedium)
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(categoriesData) { category ->
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(Dp(16.toFloat()))
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
        viewModel.getCategoryQuestions(categoryId)
    }
    val questionsData: List<Question> by viewModel.questionsData.collectAsState(emptyList())
    val questionsLoad: Boolean by viewModel.questionsLoad.collectAsState(false)
    val questionsError: Boolean by viewModel.questionsError.collectAsState(false)

    when {
        questionsLoad -> {
            Text(text = "Loading questions...", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.headlineMedium)
        }
        questionsError -> {
            Text(text = "Error loading questions", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.headlineMedium)
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(questionsData) { question ->
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(Dp(16.toFloat()))
                    )
                }
            }
        }
    }
}
