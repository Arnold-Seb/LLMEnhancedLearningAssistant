package com.example.llmenhancedlearningassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun LLMLearningAssistantApp() {
    var screen by remember { mutableStateOf(Screen.LOGIN) }
    var profile by remember { mutableStateOf(StudentProfile()) }
    var selectedAnswer by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }

    MaterialTheme {
        AnimatedContent(
            targetState = screen,
            label = "screen_animation"
        ) { currentScreen ->
            when (currentScreen) {
                Screen.LOGIN -> LoginScreen(
                    onLogin = {
                        profile = profile.copy(name = "Student")
                        screen = Screen.HOME
                    },
                    onCreateAccount = { screen = Screen.SETUP }
                )

                Screen.SETUP -> SetupScreen(
                    onAccountCreated = { name, email ->
                        profile = profile.copy(name = name, email = email)
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
                    onStartTask = { screen = Screen.TASK },
                    onBack = { screen = Screen.LOGIN }
                )

                Screen.TASK -> TaskScreen(
                    profile = profile,
                    selectedAnswer = selectedAnswer,
                    onAnswerSelected = { selectedAnswer = it },
                    onSubmit = {
                        resultMessage =
                            if (selectedAnswer == "A stack is Last In First Out") {
                                "Correct. A stack follows the Last In First Out principle."
                            } else {
                                "Incorrect. The correct answer is: A stack is Last In First Out."
                            }
                        screen = Screen.RESULTS
                    },
                    onBack = { screen = Screen.HOME }
                )

                Screen.RESULTS -> ResultsScreen(
                    selectedAnswer = selectedAnswer,
                    resultMessage = resultMessage,
                    onContinue = { screen = Screen.HOME },
                    onBack = { screen = Screen.TASK }
                )
            }
        }
    }
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
fun LoginScreen(onLogin: () -> Unit, onCreateAccount: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AppBackground {
        Spacer(modifier = Modifier.height(80.dp))

        Text("Welcome,", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Student!", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text("Let’s Start Learning!", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLogin,
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
fun SetupScreen(onAccountCreated: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AppBackground {
        Spacer(modifier = Modifier.height(35.dp))

        Text("Lets get you", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Setup!", fontSize = 36.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        SimpleField("Username", name) { name = it }
        SimpleField("Email", email) { email = it }
        SimpleField("Confirm Email", confirmEmail) { confirmEmail = it }
        SimpleField("Password", password) { password = it }
        SimpleField("Confirm Password", confirmPassword) { confirmPassword = it }
        SimpleField("Phone Number", phone) { phone = it }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = { onAccountCreated(name.ifBlank { "Student" }, email) },
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
fun HomeScreen(profile: StudentProfile, onStartTask: () -> Unit, onBack: () -> Unit) {
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
            title = "Generated Task 1",
            subtitle = "Small description for the generated learning task",
            onClick = onStartTask
        )

        Spacer(modifier = Modifier.height(25.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Login")
        }
    }
}

@Composable
fun TaskScreen(
    profile: StudentProfile,
    selectedAnswer: String,
    onAnswerSelected: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    var hintPrompt by remember { mutableStateOf("") }
    var hintResponse by remember { mutableStateOf("") }
    var loadingHint by remember { mutableStateOf(false) }

    var summaryPrompt by remember { mutableStateOf("") }
    var summaryResponse by remember { mutableStateOf("") }
    var loadingSummary by remember { mutableStateOf(false) }

    val answers = listOf(
        "A stack is Last In First Out",
        "A stack stores only images",
        "A stack is used only in databases"
    )

    AppBackground {
        Spacer(modifier = Modifier.height(25.dp))

        Text("Generated Task 1", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text("Generated by AI", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(18.dp))

        CardBox {
            Text("Question 1", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Which statement correctly describes a stack data structure?")

            Spacer(modifier = Modifier.height(10.dp))

            answers.forEach { answer ->
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
                    "Generate a simple hint for a student learning stack data structures. Do not give the answer directly."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate AI Hint")
        }

        LaunchedEffect(loadingHint) {
            if (loadingHint) {
                delay(1200)
                hintResponse =
                    "Think about a stack of plates. The last plate placed on top is usually the first one removed."
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
                    "Create a short lesson summary about stacks for a beginner student interested in ${profile.interests.joinToString()}."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate AI Lesson Summary")
        }

        LaunchedEffect(loadingSummary) {
            if (loadingSummary) {
                delay(1200)
                summaryResponse =
                    "A stack is a linear data structure where items are added and removed from the top. It follows Last In First Out. Stacks are useful in undo features, browser history, and function calls."
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
            onClick = onSubmit,
            enabled = selectedAnswer.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66))
        ) {
            Text("Submit", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@Composable
fun ResultsScreen(
    selectedAnswer: String,
    resultMessage: String,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var explanationPrompt by remember { mutableStateOf("") }
    var explanationResponse by remember { mutableStateOf("") }
    var loadingExplanation by remember { mutableStateOf(false) }

    AppBackground {
        Spacer(modifier = Modifier.height(45.dp))

        Text("Your Results", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text("Answered by AI", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(20.dp))

        CardBox {
            Text("Selected Answer:", fontWeight = FontWeight.Bold)
            Text(selectedAnswer.ifBlank { "No answer selected" })

            Spacer(modifier = Modifier.height(10.dp))

            Text("Result:", fontWeight = FontWeight.Bold)
            Text(resultMessage)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                loadingExplanation = true
                explanationPrompt =
                    "Explain why the answer '$selectedAnswer' is correct or incorrect for a beginner student."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Explain My Answer")
        }

        LaunchedEffect(loadingExplanation) {
            if (loadingExplanation) {
                delay(1200)
                explanationResponse =
                    if (selectedAnswer == "A stack is Last In First Out") {
                        "Your answer is correct because a stack removes the most recently added item first. This is known as the Last In First Out rule."
                    } else {
                        "Your answer is incorrect because a stack is not based on images or databases only. The key rule is Last In First Out."
                    }
                loadingExplanation = false
            }
        }

        LLMBox(
            title = "LLM Utility 3: Answer Explanation",
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
                Text("Open Task")
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
    if (prompt.isNotBlank() || response.isNotBlank() || loading) {
        Spacer(modifier = Modifier.height(10.dp))

        CardBox {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Prompt:", fontWeight = FontWeight.Bold)
            Text(prompt.ifBlank { "No prompt generated yet." })

            Spacer(modifier = Modifier.height(8.dp))

            Text("Response:", fontWeight = FontWeight.Bold)

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                Text("Loading AI response...")
            } else {
                Text(response.ifBlank { "No response yet." })
            }
        }
    }
}