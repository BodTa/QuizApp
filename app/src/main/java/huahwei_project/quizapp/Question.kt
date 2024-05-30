package huahwei_project.quizapp

data class Question(val question:String,val correct_answer:String,val incorrect_answers:List<String>)


data class QuestionResponse(val results:List<Question>)