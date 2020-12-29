package com.iammoty.pego

import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.mTypography
import com.ccfraser.muirwik.components.spacingUnits
import kotlinx.css.*
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.img
import styled.StyleSheet
import styled.css
import styled.styledDiv


class Intro : RComponent<RProps, RState>() {

    private object ComponentStyles : StyleSheet("ComponentStyles", isStatic = true) {
        val typographyStyle by css {
             fontSize = 1.em
             paddingBottom = 2.spacingUnits
        }
    }

    override fun RBuilder.render() {

        styledDiv {
            css {
                padding(3.spacingUnits)
                textAlign = TextAlign.left
            }
            img("Muirwik Box", "/images/img.jpg") {}
            mTypography("Welcome to hell", MTypographyVariant.h3) { css { paddingBottom = 3.spacingUnits }}


        }
    }
}

fun RBuilder.intro() = child(Intro::class) {}
