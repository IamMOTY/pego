package com.iammoty.pego

import LoginOrRegisterFailedException
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.form.MFormControlVariant
import com.ccfraser.muirwik.components.form.mFormControlLabel
import com.ccfraser.muirwik.components.form.mFormGroup
import com.ccfraser.muirwik.components.transitions.MTransitionProps
import com.ccfraser.muirwik.components.transitions.SimpleTransitionDuration
import com.ccfraser.muirwik.components.transitions.SlideTransitionDirection
import com.ccfraser.muirwik.components.transitions.mSlide
import com.iammoty.pego.model.Role
import com.iammoty.pego.model.User
import kotlinx.css.*
import kotlinx.html.InputType
import react.*
import react.dom.div
import register
import styled.StyleSheet
import styled.css
import styled.styledDiv


interface RegisterTabState : RState {
    var userId: String
    var displayName: String
    var email: String
    var password: String
    var isController: Boolean
    var errorMessage: String
    var snapbackOpen: Boolean
    var disabled: Boolean
}


class RegisterTab : RComponent<UserProps, RegisterTabState>() {
    override fun RegisterTabState.init(props: UserProps) {

        userId = ""
        displayName = ""
        email = ""
        password = ""
        isController = false
        errorMessage = ""
        snapbackOpen = false
        disabled = false
    }

    class SlideTransitionComponent(props: MTransitionProps) : RComponent<MTransitionProps, RState>(props) {
        override fun RBuilder.render() {
            mSlide(props.show, direction = SlideTransitionDirection.left, timeout = SimpleTransitionDuration(1500)) {
                props.children()
            }
        }
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
                variant = formType,
                disabled = state.disabled,
                onChange = { val value = it.targetInputValue; setState { userId = value } }) {
                css(themeStyles.textField)
            }
            mTextField(
                label = "Name",
                required = true,
                variant = formType,
                disabled = state.disabled,
                onChange = { val value = it.targetInputValue; setState { displayName = value } }) {
                css(themeStyles.textField)
            }
            mTextField(
                label = "Email",
                type = InputType.email,
                autoComplete = "email",
                required = true,
                disabled = state.disabled,
                variant = formType,
                onChange = { val value = it.targetInputValue; setState { email = value } }
            ) {
                css(themeStyles.textField)
            }
            mTextField(
                label = "Password",
                type = InputType.password,
                autoComplete = "current-password",
                required = true,
                disabled = state.disabled,
                variant = formType,
                onChange = { val value = it.targetInputValue; setState { password = value } }
            ) {
                css(themeStyles.textField)
            }
            styledDiv {
                mFormControlLabel(
                    "Register as controller", control = mSwitch(state.isController,
                        MOptionColor.primary,
                        addAsChild = false,
                        disabled = state.disabled,
                        onChange = { _, _ -> setState { isController = !isController } })
                )
            }

            mButton(
                variant = MButtonVariant.outlined,
                caption = "Submit",
                color = MColor.primary,
                disabled = state.disabled,
                onClick = {
                    doRegister()
                    it.preventDefault()
                }
            )

            mSnackbar(state.errorMessage, state.snapbackOpen) {
                attrs.transitionComponent = SlideTransitionComponent::class
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

    private fun doRegister() {
        setState {
            disabled = true
        }
        try {
            val user = register(state.userId, state.password, state.displayName, state.email, getRole())
            println("User is received - $user")
            registered(user)
        } catch (e: Exception) {
            registrationFailed(e)
        }

    }

    private fun getRole() = if (state.isController) Role.CONTROLLER else Role.PASSENGER

    private fun registered(user: User) {
        props.userAssigned(user)
    }

    private fun registrationFailed(err: Throwable) {
        if (err is LoginOrRegisterFailedException) {
            setState {
                errorMessage = err.message ?: ""
                snapbackOpen = true
                disabled = false
            }
        } else {
            console.log("Registration failed", err)
            setState {
                errorMessage = "Registration failed"
                snapbackOpen = true
            }
        }
    }


}

fun RBuilder.registerTab(onUserAssigned: (User) -> Unit) = child(RegisterTab::class) {
    attrs.userAssigned = onUserAssigned
}
