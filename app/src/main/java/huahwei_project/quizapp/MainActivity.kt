package huahwei_project.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random


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
            HomeScreen(onNavigateToCategories = {amount->
                navController.navigate("categories/$amount")
            })
        }

        composable("categories/{amount}") {backStackEntry->
            val amount = backStackEntry.arguments?.getString("amount")?.toInt() ?:10
            CategoriesScreen(
                viewModel = mainViewModel,
                onCategorySelected = { categoryId ->
                    navController.navigate("questions/$categoryId/$amount")
                }
            )
        }
        composable("questions/{categoryId}/{amount}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toInt() ?: 0
            val amount = backStackEntry.arguments?.getString("amount")?.toInt() ?:10
            QuizScreen(
                viewModel = mainViewModel,
                categoryId = categoryId,
                questionAmount = amount,
                onQuizEnd = { score ->
                    navController.navigate("result/$score")
                }
            )
        }
        composable("result/{score}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
            ResultScreen(score = score, onRestart = {
                navController.navigate("categories") {
                    popUpTo("home") { inclusive = true }
                }
            })
        }
    }
}

@Composable
fun HomeScreen(onNavigateToCategories:(amount:Int)->Unit){
    var selectedAmount by remember {
        (mutableStateOf(10))
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Quiz App", color=Color.Magenta,style=MaterialTheme.typography.headlineLarge)
        QuestionAmountSelector(selectedAmount) { amount ->
            selectedAmount = amount
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onNavigateToCategories(selectedAmount) }) {
            Text("Go to Categories")
        }
    }
}

@Composable
fun QuestionAmountSelector(selectedAmount: Int, onAmountSelected: (Int) -> Unit) {
    val questionAmounts = listOf(10, 20, 30, 40, 50)

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(8.dp)) {
        TextButton(onClick = { expanded = true }) {
            Text("Questions: $selectedAmount", color = Color.Black)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            questionAmounts.forEach { amount ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onAmountSelected(amount)
                }) {
                    Text("Questions: $amount")
                }
            }
        }
    }
}
@Composable
fun CategoriesScreen(viewModel: MainViewModel, onCategorySelected: (Int) -> Unit) {
    val categoriesData: List<QuestionCategory> by viewModel.categoriesData.collectAsState(emptyList())
    val categoriesLoad: Boolean by viewModel.categoriesLoad.collectAsState(false)
    val categoriesError: Boolean by viewModel.categoriesError.collectAsState(false)
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Select a Category",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dp(8.toFloat()))
                                .clickable { onCategorySelected(category.id) },
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun QuizScreen(viewModel: MainViewModel, categoryId: Int,questionAmount:Int,onQuizEnd: (Int) -> Unit) {
    LaunchedEffect(categoryId) {
        viewModel.getCategoryQuestions(categoryId,questionAmount)
    }
    val questions by viewModel.questionsData.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val score by viewModel.score.collectAsState()

    if (questions.isNotEmpty() && currentQuestionIndex < questions.size) {
        val question = questions[currentQuestionIndex]
        val allAnswers = remember(question) {
            (question.incorrect_answers + question.correct_answer).shuffled(Random(currentQuestionIndex))
        }
        var selectedAnswer by remember { mutableStateOf<String?>(null) }
        var isAnswered by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = question.question, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            allAnswers.forEach { answer ->
                Button(
                    onClick = {
                        if (!isAnswered) {
                            selectedAnswer = answer
                            isAnswered = true
                            if (answer == question.correct_answer) {
                                viewModel.addScore()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            when {
                                isAnswered && answer == question.correct_answer -> Color.Green
                                isAnswered && answer != question.correct_answer -> Color.Red
                                else -> Color.Transparent
                            }
                        )
                ) {
                    Text(text = answer)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (currentQuestionIndex < questions.size - 1) {
                        viewModel.incrementQuestionIndex()
                        selectedAnswer = null
                        isAnswered = false
                    } else {
                        onQuizEnd(score)
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = if (currentQuestionIndex < questions.size - 1) "Next" else "Finish")
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
@Composable
fun ResultScreen(score: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Your Score: $score", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRestart) {
            Text("Back to Categories")
        }
    }
}
