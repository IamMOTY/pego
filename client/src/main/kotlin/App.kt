package com.iammoty.pego

import com.ccfraser.muirwik.components.Colors
import com.ccfraser.muirwik.components.mCssBaseline
import com.ccfraser.muirwik.components.mThemeProvider
import com.ccfraser.muirwik.components.styles.ThemeOptions
import com.ccfraser.muirwik.components.styles.createMuiTheme
import com.iammoty.pego.model.User
import react.*

interface AppState: RState {
    var themeColor: String
}

class App(props: RProps) : RComponent<RProps, AppState>(props) {
    override fun AppState.init() {
        themeColor = "light"
    }
    override fun RBuilder.render() {
        mCssBaseline()

        @Suppress("UnsafeCastFromDynamic")
        val themeOptions: ThemeOptions = js("({palette: { type: 'placeholder', primary: {main: 'placeholder'}}})")
        themeOptions.palette?.type = state.themeColor
        themeOptions.palette?.primary.main = Colors.Blue.shade500.toString()

        mThemeProvider(createMuiTheme(themeOptions)) {
            mainFrame("Home") { setState { themeColor = if (themeColor == "dark") "light" else "dark" } }
        }
    }
}

fun RBuilder.app() = child(App::class) {}

class UserProps : RProps {
    var userAssigned: (User) -> Unit = {}
}

class AssignedUserProps : RProps {
    var checkUserInfo: () -> Unit = {}
    var user: User? = null
}