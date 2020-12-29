package com.iammoty.pego

import LoginOrRegisterFailedException
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.form.MFormControlVariant
import com.ccfraser.muirwik.components.form.mFormGroup
import com.iammoty.pego.model.User
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.paddingBottom
import kotlinx.html.InputType
import login
import react.*
import react.dom.div
import styled.StyleSheet
import styled.css

interface LoginTabState : RState {
    var userId: String
    var password: String
    var disabled: Boolean
    var errorMessage: String
    var snapbackOpen: Boolean
}


class LoginTab : RComponent<UserProps, LoginTabState>() {
    override fun LoginTabState.init() {
        userId = ""
        password = ""
        disabled = false
        snapbackOpen = false
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
            mTextField(label = "Login", required = true, disabled = state.disabled, variant = formType) {
                css(themeStyles.textField)
            }
            mTextField(
                label = "Password",
                type = InputType.email,
                autoComplete = "current-password",
                disabled = state.disabled,
                required = true,
                variant = formType
            ) {
                css(themeStyles.textField)
            }

            mButton(variant = MButtonVariant.outlined, caption = "Submit", disabled = state.disabled, color = MColor.primary, onClick = {
                doLogin()
                it.preventDefault()
            })

            mSnackbar(state.errorMessage, state.snapbackOpen) {
                attrs.transitionComponent = RegisterTab.SlideTransitionComponent::class
                attrs.action = div {
                    mIconButton(
                        "close",
                        onClick = { setState { errorMessage = ""; snapbackOpen = false } },
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
        try {
            val user = login(state.userId, state.password)
            loggedIn(user)
        } catch (e: Exception) {
            loginFailed(e)
        }
    }

    private fun loggedIn(user: User) {
        props.userAssigned(user)
    }

    private fun loginFailed(err: Throwable) {
        if (err is LoginOrRegisterFailedException) {
            setState {
                disabled = false
                errorMessage = err.message ?: ""
                snapbackOpen = true
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