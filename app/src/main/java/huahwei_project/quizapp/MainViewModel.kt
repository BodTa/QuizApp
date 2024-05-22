package huahwei_project.quizapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val questionAPI = QuestionAPIService()

    val questionData = MutableLiveData<List<Question>>()
    val questionLoad = MutableLiveData<Boolean>()
    val questionError = MutableLiveData<Boolean>()

    val question = MutableLiveData<Question>()

    init {
        getDataFromAPI()
    }
    fun getDataFromAPI(){
        questionLoad.value = true

        questionAPI.GetQuestions().enqueue(object: Callback<List<Question>>{
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                questionData.value = response.body()
                questionLoad.value = false
                questionError.value = false
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                questionLoad.value = false
                questionError.value = true
                Log.e("RetrofitError",t.message.toString())
            }
        })

    }
}