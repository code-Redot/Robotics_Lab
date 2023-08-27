package com.example.robotics_lab

import android.os.Parcel
import android.os.Parcelable

data class Equipment(
    val equipmentId: String? = null,
    val equipmentName: String? = null,
    val manufacturer: String? = null,
    val modelNumber: String? = null,
    val equipmentDescription: String? = null,
    val photos: ArrayList<String>? = null
) : Parcelable {
    constructor() : this(null, null, null, null, null, null)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(equipmentId)
        parcel.writeString(equipmentName)
        parcel.writeString(manufacturer)
        parcel.writeString(modelNumber)
        parcel.writeString(equipmentDescription)
        parcel.writeStringList(photos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Equipment> {
        override fun createFromParcel(parcel: Parcel): Equipment {
            return Equipment(parcel)
        }

        override fun newArray(size: Int): Array<Equipment?> {
            return arrayOfNulls(size)
        }
    }
}
