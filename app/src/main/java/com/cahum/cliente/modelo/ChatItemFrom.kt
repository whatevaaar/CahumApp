package com.cahum.cliente.modelo

import com.cahum.cliente.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_row_from.view.*

class ChatItemFrom(private val texto: String): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.chat_row_from
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.chat_text_from.text = texto
        }
    }