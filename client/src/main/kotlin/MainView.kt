import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogContent
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItemWithIcon
import com.ccfraser.muirwik.components.styles.Breakpoint
import com.ccfraser.muirwik.components.styles.down
import com.ccfraser.muirwik.components.styles.up
import kotlinext.js.js
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.Transition
import kotlinx.css.properties.ms
import react.*
import react.dom.div
import styled.StyleSheet
import styled.css
import styled.styledDiv

interface MainFrameProps : RProps {
    var onThemeSwitch: () -> Unit
    var initialView: String
}

interface MainFrameState : RState {
    var view: String
    var drawerOpen: Boolean
    var loginRegisterDialogOpen: Boolean
    var loginRegisterDialogView: String
}

class MainFrame(props: MainFrameProps) : RComponent<MainFrameProps, MainFrameState>(props) {
    override fun MainFrameState.init(props: MainFrameProps) {
        view = props.initialView
        drawerOpen = false
        loginRegisterDialogOpen = false
        loginRegisterDialogView = "Login"
    }

    private val nameToComponent = hashMapOf(
        "Intro" to RBuilder::intro,
    )

    private val nameToIcon = hashMapOf(
        "Intro" to "home"
    )

    private val nameToDivider = hashMapOf(
        "Intro" to false
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

                            mToolbarTitle("PeGo - ${state.drawerOpen}")
                            mIconButton("account_circle", onClick = { setState { loginRegisterDialogOpen = true } }) { }
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
//                    mHidden(smDown = true, implementation = MHiddenImplementation.css) {
//                        mDrawer(true, MDrawerAnchor.left, MDrawerVariant.permanent, paperProps = p) {
//                            spacer()
//                            demoItems()
//                        }
//                    }
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
                            nameToComponent[state.view]?.invoke(this)
                        }
                    }
                }
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
                    state.loginRegisterDialogOpen,
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
                                "Login" -> loginTab()
                                "Register" -> registerTab()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun RBuilder.demoItems() {
        fun RBuilder.addListItem(caption: String) {
//            mListItem(caption, onClick = {setState {currentView = caption}})
            // We want to get rid of the extra right padding, so must use the longer version as below
            mListItemWithIcon(
                nameToIcon[caption]!!,
                caption,
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

                nameToComponent.keys.forEach { addListItem(it) }
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


fun RBuilder.mainFrame(initialView: String, onThemeSwitch: () -> Unit) = child(MainFrame::class) {
    attrs.onThemeSwitch = onThemeSwitch
    attrs.initialView = initialView
}
