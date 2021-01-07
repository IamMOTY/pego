package com.iammoty.pego

import LoginOrRegisterFailedException
import async
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.form.MFormControlVariant
import com.ccfraser.muirwik.components.form.mFormGroup
import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import com.ccfraser.muirwik.components.lab.alert.mAlert
import com.iammoty.pego.model.User
import kotlinx.css.*
import kotlinx.html.InputType
import login
import react.*
import styled.StyleSheet
import styled.css
import styled.styledDiv

interface LoginTabState : RState {
    var userId: String
    var password: String
    var disabled: Boolean
    var errorMessage: String
    var snackbarOpen: Boolean
}


class LoginTab : RComponent<UserProps, LoginTabState>() {
    override fun LoginTabState.init() {
        userId = ""
        password = ""
        disabled = false
        snackbarOpen = false
        errorMessage = ""
    }

    override fun RBuilder.render() {
        val themeStyles = object : StyleSheet("ComponentStyles", isStatic = true) {
            val textField by css {
                marginLeft = 1.spacingUnits
                marginRight = 1.spacingUnits
            }
        }
        mFormGroup {
            css {
                paddingBottom = 3.spacingUnits
            }
            val formType = MFormControlVariant.outlined
            mTextField(
                label = "Login",
                required = true,
                disabled = state.disabled,
                variant = formType,
                onChange = { val value = it.targetInputValue; setState { userId = value } }) {
                css(themeStyles.textField)
            }
            mTextField(
                label = "Password",
                type = InputType.email,
                autoComplete = "current-password",
                disabled = state.disabled,
                required = true,
                variant = formType,
                onChange = { val value = it.targetInputValue; setState { password = value } }
            ) {
                css(themeStyles.textField)
            }

            mButton(
                variant = MButtonVariant.outlined,
                caption = "Submit",
                disabled = state.disabled,
                color = MColor.primary,
                onClick = {
                    doLogin()
                    it.preventDefault()
                })

            mSnackbar(message = state.errorMessage, state.snackbarOpen) {
                attrs.transitionComponent = RegisterTab.SlideTransitionComponent::class
                attrs.action = styledDiv {
                    css {
                        display = Display.flex
                        justifyContent = JustifyContent.center
                    }
                    mAlert(title="Login failed", message = state.errorMessage, severity = MAlertSeverity.error)
                    mIconButton(
                        "close",
                        onClick = { setState { errorMessage = ""; snackbarOpen = false } },
                        color = MColor.inherit
                    )
                }
            }
        }
    }

    private fun doLogin() {
        setState {
            disabled = true
        }
        async {
            with(state) {
                val user = login(userId, password)
                println(user)
                loggedIn(user)
            }
        }.catch { err -> loginFailed(err) }
    }

    private fun loggedIn(user: User) {
        props.userAssigned(user)
    }

    private fun loginFailed(err: Throwable) {
        if (err is LoginOrRegisterFailedException) {
            setState {
                disabled = false
                errorMessage = err.message.toString()
                snackbarOpen = true
                disabled = false
            }
        } else {
            console.error("Login failed", err)
            setState {
                disabled = false
                errorMessage = "Login failed: please reload page and try again"
            }
        }
    }
}

fun RBuilder.loginTab(onUserAssigned: (User) -> Unit) = child(LoginTab::class) {
    attrs.userAssigned = onUserAssigned
}