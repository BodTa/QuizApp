package huahwei_project.quizapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val questionAPI = QuestionAPIService()

    private val _categoriesData = MutableStateFlow<List<QuestionCategory>>(emptyList())
    val categoriesData: StateFlow<List<QuestionCategory>> = _categoriesData

    private val _categoriesLoad = MutableStateFlow<Boolean>(false)
    val categoriesLoad: StateFlow<Boolean> = _categoriesLoad

    private val _categoriesError = MutableStateFlow<Boolean>(false)
    val categoriesError: StateFlow<Boolean> = _categoriesError

    private val _questionsData = MutableStateFlow<List<Question>>(emptyList())
    val questionsData: StateFlow<List<Question>> = _questionsData

    private val _questionsLoad = MutableStateFlow<Boolean>(false)
    val questionsLoad: StateFlow<Boolean> = _questionsLoad

    private val _questionsError = MutableStateFlow<Boolean>(false)
    val questionsError: StateFlow<Boolean> = _questionsError

    init {
        getCategoriesFromAPI()
    }

    fun getCategoriesFromAPI() {
        _categoriesLoad.value = true

        questionAPI.getCategories().enqueue(object : Callback<QuestionCategoryResponse> {
            override fun onResponse(call: Call<QuestionCategoryResponse>, response: Response<QuestionCategoryResponse>) {
                if (response.isSuccessful) {
                    println(response.body()?.trivia_categories)
                    _categoriesData.value = response.body()?.trivia_categories ?: emptyList()
                    _categoriesError.value = false
                } else {
                    _categoriesError.value = true
                }
                _categoriesLoad.value = false
            }

            override fun onFailure(call: Call<QuestionCategoryResponse>, t: Throwable) {
                _categoriesLoad.value = false
                _categoriesError.value = true
                Log.e("RetrofitError", t.message.toString())
            }
        })
    }

    fun getCategoryQuestions(categoryId: Int, amount: Int = 10) {
        _questionsLoad.value = true

        questionAPI.getQuestions(categoryId = categoryId, amount = amount).enqueue(object : Callback<QuestionResponse> {
            override fun onResponse(call: Call<QuestionResponse>, response: Response<QuestionResponse>) {
                println(response)
                if (response.isSuccessful) {
                    _questionsData.value = response.body()?.results ?: emptyList()
                    _questionsError.value = false
                } else {
                    _questionsError.value = true
                }
                _questionsLoad.value = false
            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                _questionsLoad.value = false
                _questionsError.value = true
                Log.e("RetrofitError", t.message.toString())
            }
        })
    }

}
