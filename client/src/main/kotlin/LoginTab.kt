import com.iammoty.pego.model.User
import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.form.MFormControlVariant
import com.ccfraser.muirwik.components.form.mFormGroup
import com.ccfraser.muirwik.components.mTextField
import com.ccfraser.muirwik.components.spacingUnits
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.paddingBottom
import kotlinx.html.InputType
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.StyleSheet
import styled.css

interface LoginTabState : RState {
    var idName: String
    var password: String
}

var usr:User

class LoginTab : RComponent<RProps, LoginTabState>() {
    override fun LoginTabState.init() {
        idName = ""
        password = ""
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
            mTextField(label = "Login", required = true, variant = formType) {
                css(themeStyles.textField)
            }
            mTextField(
                label = "Password",
                type = InputType.email,
                autoComplete = "current-password",
                required = true,
                variant = formType
            ) {
                css(themeStyles.textField)
            }

            mButton(variant = MButtonVariant.outlined, caption = "Submit", color = MColor.primary)
        }
    }
}

fun RBuilder.loginTab() = child(LoginTab::class) {}