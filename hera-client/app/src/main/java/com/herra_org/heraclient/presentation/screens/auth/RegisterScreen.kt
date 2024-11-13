package com.herra_org.heraclient.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.herra_org.heraclient.presentation.components.LoadingDialog
import com.herra_org.heraclient.presentation.view_models.auth.AuthEvent
import com.herra_org.heraclient.presentation.view_models.auth.AuthViewModel
import com.herra_org.heraclient.presentation.view_models.auth.UIEvent
import com.herra_org.heraclient.utils.NavGraphs
import com.herra_org.heraclient.utils.RouteConfig
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val state = viewModel.state.value
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UIEvent.NavigateToLogin -> {
                    navController.navigate(RouteConfig.Login.route) {
                        popUpTo(RouteConfig.Register.route) { inclusive = true }
                    }
                }
                is UIEvent.NavigateToHome -> {
                    navController.navigate(NavGraphs.MAIN) {
                        popUpTo(NavGraphs.AUTH) { inclusive = true }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it.trim() },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it.trim() },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = password != confirmPassword && confirmPassword.isNotEmpty(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (isFormValid(email, password, confirmPassword, firstName, lastName)) {
                            handleRegister(viewModel, email, password, firstName, lastName)
                        }
                    }
                ),
                supportingText = if (password != confirmPassword && confirmPassword.isNotEmpty()) {
                    { Text("Passwords don't match") }
                } else null
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    handleRegister(viewModel, email, password, firstName, lastName)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = isFormValid(email, password, confirmPassword, firstName, lastName)
            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account?")
                TextButton(
                    onClick = {
                        navController.navigate(RouteConfig.Login.route) {
                            popUpTo(RouteConfig.Register.route) { inclusive = true }
                        }
                    }
                ) {
                    Text("Login")
                }
            }
        }

        // Error Snackbar
        if (state.error.isNotEmpty()) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = {
                        viewModel.clearError()  // Add this function to ViewModel
                    }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(state.error)
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }
}

private fun isFormValid(
    email: String,
    password: String,
    confirmPassword: String,
    firstName: String,
    lastName: String
): Boolean {
    return email.isNotEmpty() &&
            isEmailValid(email) &&
            password.isNotEmpty() &&
            password.length >= 8 &&
            password == confirmPassword &&
            firstName.isNotEmpty() &&
            lastName.isNotEmpty()
}

private fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}


private fun handleRegister(
    viewModel: AuthViewModel,
    email: String,
    password: String,
    firstName: String,
    lastName: String
) {
    if (password.length < 8) {
        viewModel.setError("Password must be at least 8 characters long")
        return
    }

    if (!isEmailValid(email)) {
        viewModel.setError("Please enter a valid email address")
        return
    }

    viewModel.onEvent(
        AuthEvent.Register(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName
        )
    )
}