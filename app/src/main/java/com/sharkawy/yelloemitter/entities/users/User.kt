package com.sharkawy.yelloemitter.entities.users

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class User(
    var username: String?,
    var phone: String?
):Parcelable