import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.form.MFormControlVariant
import com.ccfraser.muirwik.components.form.mFormControlLabel
import com.ccfraser.muirwik.components.form.mFormGroup
import kotlinx.css.*
import kotlinx.html.InputType
import react.*
import styled.StyleSheet
import styled.css
import styled.styledDiv


interface RegisterTabState : RState {
    var idName: String
    var displayName: String
    var email: String
    var password: String
    var isController: Boolean
}


class RegisterTab : RComponent<RProps, RegisterTabState>() {
    override fun RegisterTabState.init() {
        idName = ""
        displayName = ""
        email = ""
        password = ""
        isController = false
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
                id = "id-name",
                label = "Login",
                required = true,
                variant = formType,
                onChange = { val value = it.targetInputValue; setState { idName = value } }) {
                css(themeStyles.textField)
            }
            mTextField(
                id = "display-name",
                label = "Name",
                required = true,
                variant = formType,
                onChange = { val value = it.targetInputValue; setState { displayName = value } }) {
                css(themeStyles.textField)
            }
            mTextField(
                id = "email",
                label = "Email",
                type = InputType.password,
                autoComplete = "email",
                required = true,
                variant = formType,
                onChange = { val value = it.targetInputValue; setState { email = value } }
            ) {
                css(themeStyles.textField)
            }
            mTextField(
                id = "password",
                label = "Password",
                type = InputType.email,
                autoComplete = "current-password",
                required = true,
                variant = formType,
                onChange = { val value = it.targetInputValue; setState { password = value } }
            ) {
                css(themeStyles.textField)
            }
            styledDiv {
                css {
                    justifyContent = JustifyContent.center
                }
                mFormControlLabel(
                    "Register as controller", control = mSwitch(state.isController, MOptionColor.primary,
                        addAsChild = false, onChange = { _, _ -> setState { isController = !isController } })
                )
            }

            mButton(variant = MButtonVariant.outlined, caption = "Submit", color = MColor.primary)


        }

    }

}

fun RBuilder.registerTab() = child(RegisterTab::class) {}
