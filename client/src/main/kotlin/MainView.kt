package com.iammoty.pego

import Tickets
import async
import checkSession
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogContent
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItemWithIcon
import com.ccfraser.muirwik.components.menu.mMenu
import com.ccfraser.muirwik.components.menu.mMenuItemWithIcon
import com.ccfraser.muirwik.components.styles.Breakpoint
import com.ccfraser.muirwik.components.styles.down
import com.ccfraser.muirwik.components.styles.up
import com.ccfraser.muirwik.components.transitions.mCollapse
import com.iammoty.pego.model.Role
import com.iammoty.pego.model.User
import kotlinext.js.js
import kotlinext.js.jsObject
import kotlinx.browser.window
import kotlinx.css.*
import kotlinx.css.properties.*
import logoutUser
import org.w3c.dom.Node
import react.*
import react.dom.div
import react.dom.findDOMNode
import styled.StyleSheet
import styled.css
import styled.styledDiv
import tickets
import updateBalance

interface MainFrameProps : RProps {
    var onThemeSwitch: () -> Unit
    var initialView: MainView
}

interface MainFrameState : RState {
    var view: MainView
    var drawerOpen: Boolean
    var loginRegisterDialogOpen: Boolean
    var loginRegisterDialogView: String
    var accountMenuOpen: Boolean
    var moneyMenuOpen: Boolean
    var user: User?
    var moneyAnchorElement: Node?
    var accountAnchorElement: Node?
}

class MainFrame(props: MainFrameProps) : RComponent<MainFrameProps, MainFrameState>(props) {
    override fun MainFrameState.init(props: MainFrameProps) {
        view = props.initialView
        drawerOpen = false
        loginRegisterDialogOpen = false
        loginRegisterDialogView = "Login"
        accountMenuOpen = false
        moneyMenuOpen = false
        moneyAnchorElement = null
        accountAnchorElement = null
        checkUserSession()
    }



    private val nameToIcon = hashMapOf(
        MainView.Home to "home",
        MainView.Tickets to "confirmation_number_icon"
    )

    private val nameToDivider = hashMapOf(
        MainView.Home to false,
        MainView.Tickets to false
    )

    private val roleToComponentList = hashMapOf(
        Role.PASSENGER to listOf(MainView.Home, MainView.Tickets),
        Role.CONTROLLER to listOf(MainView.Home),
        Role.UNKNOWN to listOf(MainView.Home)
    )

    private val drawerWidth = 180.px


    override fun RBuilder.render() {
        mCssBaseline()


        themeContext.Consumer { theme ->
            styledDiv {
                css {
                    flexGrow = 1.0
                    width = 100.pct
                    zIndex = 1
                    overflow = Overflow.hidden
                    position = Position.relative
                    display = Display.flex
                }

                styledDiv {
                    // App Frame
                    css {
                        overflow = Overflow.hidden; position = Position.relative; display = Display.flex; width =
                        100.pct
                    }

                    mAppBar(position = MAppBarPosition.absolute) {
                        css {
                            transition += Transition("width", 195.ms, Timing.materialStandard, 0.ms)
                            zIndex = theme.zIndex.drawer + 1
                            if (state.drawerOpen) width = 100.pct - drawerWidth
                        }
                        mToolbar(disableGutters = !state.drawerOpen) {
                            if (!state.drawerOpen) {
                                mIconButton(
                                    "menu",
                                    color = MColor.inherit,
                                    onClick = { setState { drawerOpen = true } })
                            }

                            mToolbarTitle("PeGo - ${state.user?.displayName}")
                            if (state.user == null) {
                                mIconButton(
                                    "account_circle",
                                    onClick = { setState { loginRegisterDialogOpen = true } }) { }
                            } else {
                                assignedAppBarItems()
                            }
                        }
                    }

                    loginRegisterDialog()

                    val p: MPaperProps = jsObject { }
                    p.asDynamic().style = js {
                        position = "relative"
                        transition = "width 195ms cubic bezier(0.4, 0, 0.6, 1) 0ms"
                        if (!state.drawerOpen) {
                            overflowX = "hidden"
                            width = 7.spacingUnits.value
                        } else {
                            width = drawerWidth + 1.px
                        }
//                        display = "block";
                        height = "100%"
                        minHeight = "100vh"
                    }
//                    mHidden(mdUp = true) {
                    mDrawer(state.drawerOpen, MDrawerAnchor.left, MDrawerVariant.permanent, paperProps = p,
                        onClose = { setState { drawerOpen = !drawerOpen } }) {
                        styledDiv {
                            css {
                                display = Display.flex; alignItems = Align.center; justifyContent =
                                JustifyContent.flexEnd; toolbarJsCssToPartialCss(theme.mixins.toolbar)
                            }
                            mIconButton("chevron_left", onClick = { setState { drawerOpen = false } })
                        }
//                            spacer()
                        mDivider()
                        demoItems()
                        mListItemWithIcon("lightbulb_outline", "Switch theme", onClick = {
                            props.onThemeSwitch()
                        })
//                        }
                    }
                    // Main content area
                    styledDiv {
                        css {
                            height = 100.pct
                            flexGrow = 1.0; minWidth = 0.px
                            backgroundColor = Color(theme.palette.background.default)
                        }
                        spacer()
                        styledDiv {
                            css {
                                media(theme.breakpoints.down(Breakpoint.sm)) {
                                    height = 100.vh - 57.px
                                }
                                media(theme.breakpoints.up(Breakpoint.sm)) {
                                    height = 100.vh - 65.px
                                }

                                overflowY = Overflow.auto
                                padding(2.spacingUnits)
                                backgroundColor = Color(theme.palette.background.default)
                            }
                            when (state.view) {
                                MainView.Home -> {
                                    intro()
                                }
                                MainView.Tickets -> {
                                    state.user?.let { tickets(::checkUserSession, it) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun RBuilder.assignedAppBarItems() {
        mIconButton("attach_money", onClick = { setState { moneyMenuOpen = true } }) {
            mTypography(text = state.user?.balance.toString()) {
            }
            ref {
                state.moneyAnchorElement = findDOMNode(it)
            }
        }
        div {
            mMenu(
                state.moneyMenuOpen,
                anchorElement = state.moneyAnchorElement,
                onClose = { _, _ -> setState { moneyMenuOpen = false } }) {

                mMenuItemWithIcon("attach_money", state.user?.balance.toString())
                mMenuItemWithIcon("add", "Top up balance", onClick = {
                    async {
                        with(state) {
                            val newUser = user?.let { it1 -> updateBalance(it1.userId) }
                            setState {
                                user = newUser
                                moneyMenuOpen = false
                                moneyMenuOpen = true
                            }
                            println(user)
                        }
                    }.catch { err -> window.alert(err.toString()) }
                })
            }
        }
        mIconButton("account_circle", onClick = { setState { accountMenuOpen = true } }) {

            ref {
                state.accountAnchorElement = findDOMNode(it)
            }
        }
        div {
            mMenu(
                state.accountMenuOpen,
                anchorElement = state.accountAnchorElement,
                onClose = { _, _ -> setState { accountMenuOpen = false } }) {

                mMenuItemWithIcon(
                    "exit_to_app",
                    "Logout",
                    onClick = { logoutUser(); setState { accountMenuOpen = false;user = null } })
            }
        }
    }

    private fun RBuilder.loginRegisterDialog() {
        themeContext.Consumer { theme ->
            val themeStyles = object : StyleSheet("ComponentStyles", isStatic = true) {
                val tabRoot by css {
                    textTransform = TextTransform.none
                    fontWeight = FontWeight(theme.typography.fontWeightRegular.toString())
                    marginRight = 4.spacingUnits
                    hover {
                        color = Color("#40a9ff")
                        opacity = 1
                    }
                    focus {
                        color = Color("#40a9ff")
                    }
                }
                val tabSelected by css {
                    color = Color("#1890ff")
                    fontWeight = FontWeight(theme.typography.fontWeightMedium.toString())
                }
            }

            fun RBuilder.customTab(label: String, value: String) {
                mTab(label, value) {
                    css {
                        +themeStyles.tabRoot
                        if (state.loginRegisterDialogView == value) {
                            +themeStyles.tabSelected
                        }
                    }
                    attrs.asDynamic().disableRipple = true
                }
            }



            div {
                mDialog(
                    state.loginRegisterDialogOpen and (state.user == null),
                    onClose = { _, _ -> setState { loginRegisterDialogOpen = false } }) {
                    mDialogContent {
                        css {
                            width = 50.spacingUnits
//                            justifyContent = JustifyContent.center
                        }
                        mTabs(state.loginRegisterDialogView, textColor = MTabTextColor.primary, onChange = { _, value ->
                            setState {
                                loginRegisterDialogView =
                                    value as String
                            }
                        }) {
                            css {
                                justifyContent = JustifyContent.center
                                borderBottom = "1px solid ${MTabTextColor.inherit}"
                            }
                            customTab("Login", "Login")
                            customTab("Register", "Register")
                        }
                        styledDiv {
                            css {
                                justifyContent = JustifyContent.center
                            }
                            when (state.loginRegisterDialogView) {
                                "Login" -> loginTab { onUserAssigned(it) }
                                "Register" -> registerTab { onUserAssigned(it) }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun RBuilder.demoItems() {
        fun RBuilder.addListItem(caption: MainView) {
//            mListItem(caption, onClick = {setState {currentView = caption}})
            mListItemWithIcon(
                nameToIcon[caption]!!,
                caption.toString(),
                divider = nameToDivider[caption]!!,
                onClick = { setState { view = caption; drawerOpen = false } }) {
                css {
                    if (caption == state.view) {
                        descendants {
                            color = Colors.Blue.shade500
                        }
                    }
                }
            }

        }

        themeContext.Consumer { theme ->

            mList {

                roleToComponentList[state.user?.role ?: Role.UNKNOWN]?.forEach { addListItem(it) }
            }
        }
    }

    private fun onUserAssigned(newUser: User) {
        setState {
            user = newUser
//            view = MainView.Home
        }
    }

    private fun checkUserSession() {
        async {
            val user = checkSession()
            onUserAssigned(user)
        }.catch {
            setState {
                view = MainView.Home
            }
        }
    }
}

fun RBuilder.spacer() {
    themeContext.Consumer { theme ->
        val themeStyles = object : StyleSheet("ComponentStyles", isStatic = true) {
            val toolbar by css {
                toolbarJsCssToPartialCss(theme.mixins.toolbar)
            }
        }

        // This puts in a spacer to get below the AppBar.
        styledDiv {
            css(themeStyles.toolbar)
        }
        mDivider { }
    }
}

enum class MainView {
    Home,
    Tickets,
    Check
}

fun RBuilder.mainFrame(initialView: String, onThemeSwitch: () -> Unit) = child(MainFrame::class) {
    attrs.onThemeSwitch = onThemeSwitch
    attrs.initialView = MainView.valueOf(initialView)
}

