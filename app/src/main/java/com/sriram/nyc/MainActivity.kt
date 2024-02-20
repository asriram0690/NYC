package com.sriram.nyc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


data class School(val school_name: String, val dbn: String)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchoolList()
        }
    }
}

@Composable
private fun SchoolList() {
    val context = LocalContext.current
    var schools by remember {
        mutableStateOf<List<School>>(emptyList())
    }

    LaunchedEffect(true) {
        fetchDataFromAPi(context)?.let { getSchools ->
            schools = getSchools
        }
    }

    LazyColumn {
        items(schools) { school -> SchoolItem(school) }
    }

}

@Composable
fun SchoolItem(school: School, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .background(Color.White)
            .padding(5.dp)
            .border(
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Text(
            text = "${school.school_name} - ${school.dbn}",
            fontSize = 16.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

suspend fun fetchDataFromAPi(context: android.content.Context): List<School> {
    return withContext(Dispatchers.IO) {
        val url = URL("https://data.cityofnewyork.us/resource/s3k6-pzi2.json")
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val reader = InputStreamReader(urlConnection.inputStream)
//            val itemType = object : TypeToken<List<School>>() {}.type
//
//            val itemSchools = Gson().fromJson<List<School>>(reader,itemType)
            Gson().fromJson(reader, Array<School>::class.java).toList()


        } finally {
            urlConnection.disconnect()
        }
    }
}

@Preview
@Composable
fun SchoolPreview() {
    SchoolItem(School("My School", "27Q314"))

}

