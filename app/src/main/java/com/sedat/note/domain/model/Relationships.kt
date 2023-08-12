package com.sedat.note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "T_Relationships")
data class Relationships(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val rootID: Int,
    val subID: Int
)
