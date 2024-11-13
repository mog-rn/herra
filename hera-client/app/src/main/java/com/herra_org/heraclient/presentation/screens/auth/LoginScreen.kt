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
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state = viewModel.state.value
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UIEvent.NavigateToHome -> {
                    navController.navigate(NavGraphs.MAIN) {
                        popUpTo(NavGraphs.AUTH) { inclusive = true }
                    }
                }
                else -> Unit
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
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = email.isNotEmpty() && !isEmailValid(email)
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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (isLoginFormValid(email, password)) {
                            handleLogin(viewModel, email, password)
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    navController.navigate(RouteConfig.ForgotPassword.route)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    handleLogin(viewModel, email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = isLoginFormValid(email, password)
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?")
                TextButton(
                    onClick = {
                        navController.navigate(RouteConfig.Register.route) {
                            popUpTo(RouteConfig.Login.route) { inclusive = true }
                        }
                    }
                ) {
                    Text("Register")
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
                        viewModel.clearError()
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

private fun isLoginFormValid(
    email: String,
    password: String
): Boolean {
    return email.isNotEmpty() &&
            isEmailValid(email) &&
            password.isNotEmpty() &&
            password.length >= 8
}

private fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun handleLogin(
    viewModel: AuthViewModel,
    email: String,
    password: String
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
        AuthEvent.Login(
            email = email,
            password = password
        )
    )
}