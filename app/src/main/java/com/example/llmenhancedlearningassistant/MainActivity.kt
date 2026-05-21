package com.example.llmenhancedlearningassistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LLMLearningAssistantApp()
        }
    }
}

enum class Screen {
    LOGIN, SETUP, INTERESTS, HOME, TASK, RESULTS
}

data class StudentProfile(
    var name: String = "",
    var email: String = "",
    var interests: List<String> = emptyList()
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

val quizQuestions = listOf(
    QuizQuestion(
        question = "Which statement correctly describes a stack data structure?",
        options = listOf(
            "A stack is Last In First Out",
            "A stack stores only images",
            "A stack is used only in databases"
        ),
        correctAnswer = "A stack is Last In First Out"
    ),
    QuizQuestion(
        question = "Which statement correctly describes a queue data structure?",
        options = listOf(
            "A queue is First In First Out",
            "A queue always removes the newest item first",
            "A queue can only store numbers"
        ),
        correctAnswer = "A queue is First In First Out"
    ),
    QuizQuestion(
        question = "What is an algorithm?",
        options = listOf(
            "A step-by-step method to solve a problem",
            "A type of computer screen",
            "A storage device"
        ),
        correctAnswer = "A step-by-step method to solve a problem"
    )
)

@Composable
fun LLMLearningAssistantApp() {
    val context = LocalContext.current

    var screen by remember { mutableStateOf(Screen.LOGIN) }
    var profile by remember { mutableStateOf(StudentProfile()) }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf("") }
    var userAnswers by remember { mutableStateOf(mutableMapOf<Int, String>()) }

    MaterialTheme {
        AnimatedContent(
            targetState = screen,
            label = "screen_animation"
        ) { currentScreen ->
            when (currentScreen) {
                Screen.LOGIN -> LoginScreen(
                    onLogin = { username, password, showError ->
                        val prefs = context.getSharedPreferences("UserAccount", Context.MODE_PRIVATE)
                        val savedUsername = prefs.getString("username", "")
                        val savedPassword = prefs.getString("password", "")
                        val savedEmail = prefs.getString("email", "")

                        if (username == savedUsername && password == savedPassword) {
                            profile = StudentProfile(
                                name = savedUsername ?: "Student",
                                email = savedEmail ?: "",
                                interests = profile.interests
                            )
                            screen = Screen.HOME
                        } else {
                            showError()
                        }
                    },
                    onCreateAccount = { screen = Screen.SETUP }
                )

                Screen.SETUP -> SetupScreen(
                    onAccountCreated = { name, email, password ->
                        val prefs = context.getSharedPreferences("UserAccount", Context.MODE_PRIVATE)
                        prefs.edit()
                            .putString("username", name)
                            .putString("email", email)
                            .putString("password", password)
                            .apply()

                        profile = StudentProfile(name = name, email = email)
                        screen = Screen.INTERESTS
                    }
                )

                Screen.INTERESTS -> InterestsScreen(
                    onNext = { interests ->
                        profile = profile.copy(interests = interests)
                        screen = Screen.HOME
                    }
                )

                Screen.HOME -> HomeScreen(
                    profile = profile,
                    onStartTask = {
                        currentQuestionIndex = 0
                        selectedAnswer = ""
                        userAnswers = mutableMapOf()
                        screen = Screen.TASK
                    },
                    onProfile = {
                        context.startActivity(Intent(context, ProfileActivity::class.java))
                    },
                    onHistory = {
                        context.startActivity(Intent(context, HistoryActivity::class.java))
                    },
                    onUpgrade = {
                        context.startActivity(Intent(context, UpgradeActivity::class.java))
                    },
                    onBack = { screen = Screen.LOGIN }
                )

                Screen.TASK -> TaskScreen(
                    profile = profile,
                    currentQuestionIndex = currentQuestionIndex,
                    selectedAnswer = selectedAnswer,
                    onAnswerSelected = { selectedAnswer = it },
                    onNext = {
                        userAnswers[currentQuestionIndex] = selectedAnswer
                        if (currentQuestionIndex < quizQuestions.size - 1) {
                            currentQuestionIndex++
                            selectedAnswer = userAnswers[currentQuestionIndex] ?: ""
                        } else {
                            saveQuizHistory(context, userAnswers)
                            screen = Screen.RESULTS
                        }
                    },
                    onBack = {
                        if (currentQuestionIndex > 0) {
                            userAnswers[currentQuestionIndex] = selectedAnswer
                            currentQuestionIndex--
                            selectedAnswer = userAnswers[currentQuestionIndex] ?: ""
                        } else {
                            screen = Screen.HOME
                        }
                    }
                )

                Screen.RESULTS -> ResultsScreen(
                    userAnswers = userAnswers,
                    onContinue = { screen = Screen.HOME },
                    onBack = { screen = Screen.TASK }
                )
            }
        }
    }
}

fun saveQuizHistory(context: Context, userAnswers: Map<Int, String>) {
    val preferences = context.getSharedPreferences("QuizHistory", Context.MODE_PRIVATE)
    val oldHistory = preferences.getString("history", "") ?: ""

    var score = 0

    val attemptText = buildString {
        append("New Test Attempt\n\n")

        quizQuestions.forEachIndexed { index, quiz ->
            val userAnswer = userAnswers[index] ?: "Not answered"
            val isCorrect = userAnswer == quiz.correctAnswer

            if (isCorrect) score++

            append("Question ${index + 1}: ${quiz.question}\n")
            append("Your Answer: $userAnswer\n")
            append("Correct Answer: ${quiz.correctAnswer}\n")
            append("Result: ${if (isCorrect) "Correct" else "Incorrect"}\n\n")
        }

        append("Final Score: $score/${quizQuestions.size}\n")
        append("------------------------------")
    }

    val updatedHistory =
        if (oldHistory.isBlank()) attemptText else "$oldHistory\n\n$attemptText"

    preferences.edit()
        .putString("history", updatedHistory)
        .apply()
}

@Composable
fun AppBackground(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF00D4FF), Color(0xFF0074D9))
                )
            )
            .padding(22.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
fun LoginScreen(
    onLogin: (String, String, () -> Unit) -> Unit,
    onCreateAccount: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AppBackground {
        Spacer(modifier = Modifier.height(80.dp))

        Text("Welcome,", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Student!", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text("Let’s Start Learning!", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessage = ""
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(errorMessage, color = Color.Red, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onLogin(username, password) {
                    errorMessage = "Invalid username or password."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66))
        ) {
            Text("Login", color = Color.Black)
        }

        Text(
            text = "Need an Account?",
            modifier = Modifier
                .padding(top = 18.dp)
                .clickable { onCreateAccount() },
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SetupScreen(onAccountCreated: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AppBackground {
        Spacer(modifier = Modifier.height(35.dp))

        Text("Lets get you", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Setup!", fontSize = 36.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        SimpleField("Username", name) { name = it }
        SimpleField("Email", email) { email = it }
        SimpleField("Confirm Email", confirmEmail) { confirmEmail = it }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        )

        SimpleField("Phone Number", phone) { phone = it }

        if (errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.Red, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                when {
                    name.isBlank() || email.isBlank() || password.isBlank() -> {
                        errorMessage = "Please fill all required fields."
                    }

                    email != confirmEmail -> {
                        errorMessage = "Email does not match."
                    }

                    password != confirmPassword -> {
                        errorMessage = "Password does not match."
                    }

                    else -> {
                        onAccountCreated(name, email, password)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66))
        ) {
            Text("Create new Account", color = Color.Black)
        }
    }
}

@Composable
fun InterestsScreen(onNext: (List<String>) -> Unit) {
    val topics = listOf(
        "Algorithms", "Data Structures", "Web Development",
        "Testing", "Databases", "Cloud", "AI", "Cyber Security"
    )

    var selected by remember { mutableStateOf(setOf<String>()) }

    AppBackground {
        Spacer(modifier = Modifier.height(50.dp))

        Text("Your", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text("Interests", fontSize = 38.sp, fontWeight = FontWeight.Bold)

        Text(
            "You may select up to 4 topics",
            fontSize = 15.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        topics.forEach { topic ->
            val isSelected = selected.contains(topic)

            Button(
                onClick = {
                    selected = if (isSelected) {
                        selected - topic
                    } else {
                        if (selected.size < 4) selected + topic else selected
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF00FF66) else Color(0xFF00A2FF)
                )
            ) {
                Text(topic, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onNext(selected.toList()) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66))
        ) {
            Text("Next", color = Color.Black)
        }
    }
}

@Composable
fun HomeScreen(
    profile: StudentProfile,
    onStartTask: () -> Unit,
    onProfile: () -> Unit,
    onHistory: () -> Unit,
    onUpgrade: () -> Unit,
    onBack: () -> Unit
) {
    AppBackground {
        Spacer(modifier = Modifier.height(45.dp))

        Text("Hello,", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(profile.name.ifBlank { "Your Name" }, fontSize = 34.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "Your interests: ${profile.interests.ifEmpty { listOf("Algorithms", "Testing") }.joinToString()}",
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        LearningCard(
            title = "Generated Test",
            subtitle = "Complete 3 learning questions and review your results",
            onClick = onStartTask
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(onClick = onProfile, modifier = Modifier.fillMaxWidth()) {
            Text("Profile")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = onHistory, modifier = Modifier.fillMaxWidth()) {
            Text("History")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = onUpgrade, modifier = Modifier.fillMaxWidth()) {
            Text("Upgrade Account")
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Login")
        }
    }
}

@Composable
fun TaskScreen(
    profile: StudentProfile,
    currentQuestionIndex: Int,
    selectedAnswer: String,
    onAnswerSelected: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var hintPrompt by remember { mutableStateOf("") }
    var hintResponse by remember { mutableStateOf("") }
    var loadingHint by remember { mutableStateOf(false) }

    var summaryPrompt by remember { mutableStateOf("") }
    var summaryResponse by remember { mutableStateOf("") }
    var loadingSummary by remember { mutableStateOf(false) }

    val question = quizQuestions[currentQuestionIndex]

    LaunchedEffect(currentQuestionIndex) {
        hintPrompt = ""
        hintResponse = ""
        loadingHint = false

        summaryPrompt = ""
        summaryResponse = ""
        loadingSummary = false
    }

    AppBackground {
        Spacer(modifier = Modifier.height(25.dp))

        Text("Generated Test", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text("Question ${currentQuestionIndex + 1} of ${quizQuestions.size}", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(18.dp))

        CardBox {
            Text("Question ${currentQuestionIndex + 1}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(question.question)

            Spacer(modifier = Modifier.height(10.dp))

            question.options.forEach { answer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAnswerSelected(answer) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAnswer == answer,
                        onClick = { onAnswerSelected(answer) }
                    )
                    Text(answer)
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                loadingHint = true
                hintPrompt =
                    "Generate a simple hint for this question without directly giving the answer: ${question.question}"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate AI Hint")
        }

        LaunchedEffect(loadingHint) {
            if (loadingHint) {
                delay(1000)
                hintResponse = when (currentQuestionIndex) {
                    0 -> "Imagine a stack of plates. The last plate placed on top is the first one taken off. This same rule applies to a stack."
                    1 -> "Think about people standing in a line. The person who joins first is usually served first. This is how a queue works."
                    2 -> "Think of a recipe or set of instructions. An algorithm is a clear step-by-step process used to solve a problem."
                    else -> "Think carefully about the main concept in the question."
                }
                loadingHint = false
            }
        }

        LLMBox(
            title = "LLM Utility 1: Hint",
            prompt = hintPrompt,
            response = hintResponse,
            loading = loadingHint
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                loadingSummary = true
                summaryPrompt =
                    "Create a short lesson summary related to this question for a beginner student interested in ${profile.interests.joinToString()}."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate AI Lesson Summary")
        }

        LaunchedEffect(loadingSummary) {
            if (loadingSummary) {
                delay(1000)
                summaryResponse = when (currentQuestionIndex) {
                    0 -> "A stack is a data structure where the newest item is removed first. This is called Last In First Out. It is used in undo actions, browser back buttons, and function calls."
                    1 -> "A queue is a data structure where the first item added is the first item removed. This is called First In First Out. It is used in waiting lines, printer jobs, and task scheduling."
                    2 -> "An algorithm is a step-by-step method for solving a problem. Programs use algorithms to process data, make decisions, and complete tasks."
                    else -> "This topic explains an important computing concept using simple examples."
                }
                loadingSummary = false
            }
        }

        LLMBox(
            title = "LLM Utility 2: Lesson Summary",
            prompt = summaryPrompt,
            response = summaryResponse,
            loading = loadingSummary
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNext,
            enabled = selectedAnswer.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66))
        ) {
            Text(
                if (currentQuestionIndex == quizQuestions.size - 1) "Submit Test" else "Next Question",
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@Composable
fun ResultsScreen(
    userAnswers: Map<Int, String>,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var explanationPrompt by remember { mutableStateOf("") }
    var explanationResponse by remember { mutableStateOf("") }
    var loadingExplanation by remember { mutableStateOf(false) }

    val score = quizQuestions.countIndexed { index, question ->
        userAnswers[index] == question.correctAnswer
    }

    AppBackground {
        Spacer(modifier = Modifier.height(45.dp))

        Text("Your Results", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text("Score: $score/${quizQuestions.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        quizQuestions.forEachIndexed { index, question ->
            val userAnswer = userAnswers[index] ?: "Not answered"
            val isCorrect = userAnswer == question.correctAnswer

            CardBox {
                Text("Question ${index + 1}", fontWeight = FontWeight.Bold)
                Text(question.question)

                Spacer(modifier = Modifier.height(8.dp))

                Text("Your Answer:", fontWeight = FontWeight.Bold)
                Text(userAnswer)

                Spacer(modifier = Modifier.height(6.dp))

                Text("Correct Answer:", fontWeight = FontWeight.Bold)
                Text(question.correctAnswer)

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    if (isCorrect) "Result: Correct" else "Result: Incorrect",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                loadingExplanation = true
                explanationPrompt =
                    "Explain the student's quiz results and give short study advice."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Explain My Results")
        }

        LaunchedEffect(loadingExplanation) {
            if (loadingExplanation) {
                delay(1000)
                explanationResponse = buildString {
                    append("You scored $score/${quizQuestions.size}.\n\n")

                    quizQuestions.forEachIndexed { index, question ->
                        val userAnswer = userAnswers[index] ?: "Not answered"
                        val isCorrect = userAnswer == question.correctAnswer

                        append("Question ${index + 1}: ")

                        if (isCorrect) {
                            append("Correct. You selected the right answer: ${question.correctAnswer}.\n\n")
                        } else {
                            append("Incorrect. You selected '$userAnswer', but the correct answer is '${question.correctAnswer}'.\n")

                            when (index) {
                                0 -> append("A stack follows Last In First Out, meaning the last item added is removed first.\n\n")
                                1 -> append("A queue follows First In First Out, meaning the first item added is removed first.\n\n")
                                2 -> append("An algorithm is a step-by-step method used to solve a problem.\n\n")
                            }
                        }
                    }
                }
                loadingExplanation = false
            }
        }

        LLMBox(
            title = "LLM Utility 3: Result Explanation",
            prompt = explanationPrompt,
            response = explanationResponse,
            loading = loadingExplanation
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66))
        ) {
            Text("Continue", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

inline fun <T> List<T>.countIndexed(predicate: (Int, T) -> Boolean): Int {
    var count = 0
    forEachIndexed { index, item ->
        if (predicate(index, item)) count++
    }
    return count
}

@Composable
fun SimpleField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    )
}

@Composable
fun LearningCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text(subtitle, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onClick) {
                Text("Open Test")
            }
        }
    }
}

@Composable
fun CardBox(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

@Composable
fun LLMBox(title: String, prompt: String, response: String, loading: Boolean) {
    if (response.isNotBlank() || loading) {
        Spacer(modifier = Modifier.height(10.dp))

        CardBox {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(8.dp))

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                Text("Generating response...")
            } else {
                Text(response)
            }
        }
    }
}