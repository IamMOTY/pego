import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.list.*
import com.ccfraser.muirwik.components.mAvatar
import com.ccfraser.muirwik.components.mIcon
import com.ccfraser.muirwik.components.mTypography
import com.iammoty.pego.AssignedUserProps
import com.iammoty.pego.model.User
import kotlinx.browser.window
import kotlinx.coroutines.delay
import react.*
import react.dom.div
import styled.css
import styled.styledDiv


interface TicketsState : RState {
    var ticketsIdList: List<Int>?
    var dialogOpen: Boolean
    var selectedTicketId: Int
}

class Tickets : RComponent<AssignedUserProps, TicketsState>() {
    override fun TicketsState.init() {
        ticketsIdList = null
//        checkTicketsList()
        dialogOpen = false
        selectedTicketId = 0
    }


    override fun RBuilder.render() {

        styledDiv {

            mIconButton("cached", onClick = { checkTicketsList() }) {
            }
            mIconButton("add_shopping_cart_icon", onClick = { doBuyTicket() }) {
                mTypography("Buy ticket")
            }

            mList {
                state.ticketsIdList?.forEach { x ->
                    mListItemWithIcon(iconName = "qr_code",primaryText = x.toString(), onClick = { setState{selectedTicketId = x; dialogOpen = true}})
                }
            }


        }
        dialog(state.dialogOpen)
    }

    private fun doBuyTicket() {
        async {
            props.user?.let { buyTicket(it.userId)}
            checkTicketsList()
            props.checkUserInfo()
        }.catch { err -> window.alert(err.toString()) }
    }

    private fun checkTicketsList() {
        async {
            while (props.user == null) {
               delay(1)
            }
            val list = props.user?.let { getUserTickets(it.userId) }
            println("Received list --- $list")
            setState {
                ticketsIdList = list
            }
            println("State list --- ${state.ticketsIdList}")
        }.catch  {
            window.alert("Error to load tickets, try again, ${it.toString()}")
        }
    }

    private fun RBuilder.dialog(open: Boolean) {

        mDialog(open, onClose = { _, _ -> setState { dialogOpen = false}}) {
            mDialogTitle("Show your qr")
            div {
                state.selectedTicketId
            }
        }
    }


}

fun RBuilder.tickets(checkUserInfo: () -> Unit, user: User) = child(Tickets::class) {
    attrs.checkUserInfo = checkUserInfo
    attrs.user = user
}