package com.hritikbhat.spotify_mvvm_app.models

import android.os.Parcel
import android.os.Parcelable

data class Playlist(
    val plid: Int,
    val plname: String,
    val pltype: Int,
    val aname:String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(plid)
        parcel.writeString(plname)
        parcel.writeInt(pltype)
        parcel.writeString(aname)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Playlist> {
        override fun createFromParcel(parcel: Parcel): Playlist {
            return Playlist(parcel)
        }

        override fun newArray(size: Int): Array<Playlist?> {
            return arrayOfNulls(size)
        }
    }
}