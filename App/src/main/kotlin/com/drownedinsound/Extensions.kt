package com.drownedinsound

import android.text.Html


fun String.fromHtml() : String = Html.fromHtml(this).trim().toString()
