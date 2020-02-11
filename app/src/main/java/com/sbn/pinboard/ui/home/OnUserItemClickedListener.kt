package com.sbn.pinboard.ui.home

import com.sbn.model.User

interface OnUserItemClickedListener {
    fun onClick(item: User)
}