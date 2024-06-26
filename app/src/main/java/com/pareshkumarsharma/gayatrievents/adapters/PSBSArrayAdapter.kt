package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pareshkumarsharma.gayatrievents.panchang.MonthHindi
import com.pareshkumarsharma.gayatrievents.panchang.PakshaHindi
import com.pareshkumarsharma.gayatrievents.panchang.WeekDayHindi


internal class PSBSArrayAdapter(
    val c: Context,
    val r: Int,
    var data: Array<List<String>>, var colNames: List<String>
) : ArrayAdapter<List<String>>(c, r, data) {
    //var Identity = 0 // for panchang 1 for festivals

    override fun isEmpty(): Boolean {
        return data[0].isEmpty()
    }

    override fun getCount(): Int {
        return data[0].size
    }

    internal fun UpdateData(d: Array<List<String>>, cols: List<String>) {
        data = d
        colNames = cols
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // convertView which is recyclable view
        // convertView which is recyclable view
        var currentItemView = convertView

        // of the recyclable view is null then inflate the custom layout for the same

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(context).inflate(
                com.pareshkumarsharma.gayatrievents.R.layout.listview_item,
                parent,
                false
            )
        }

        val txt1 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txt1OfListViewItem)
        val txt2 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txt2OfListViewItem)

        txt1?.text = colNames[position] + ": "
        txt2?.text = data[0][position].toString().replace("#~#", "\n").replace(", "," ")

        if (txt2?.text.toString().trim().length == 0)
            txt2?.text = "--"

        when(colNames[position]){
            "Tithi" -> txt1?.text = "तिथी : "
            "Weekday" -> txt1?.text = "वार : "
            "Paksha" -> txt1?.text = "पक्ष : "
            "AmantMonth" -> txt1?.text = "महिना : "
            "Festivals"-> txt1?.text = "तहेवार : "
            "Sunrise" -> txt1?.text = "सुर्योदय : "
            "Sunset" -> txt1?.text = "सुर्यास्त : "
            "Nakshatra" -> txt1?.text = "नक्षत्र: "
            "Moonsign" -> txt1?.text = "चंद्र राशी: "
            "Sunsign" -> txt1?.text = "सुर्य राशी : "
            "Yoga" -> txt1?.text = "योग : "
            "Karan" -> txt1?.text = "करण : "
            "Moonrise" -> txt1?.text = "चंद्रोदय : "
            "Moonset" -> txt1?.text = "चंद्रास्त : "
            "VikramSamvat" -> txt1?.text = "विक्रम संवत : "
            "ShakSamvat" -> txt1?.text = "शक संवत : "
        }

        when (colNames[position]) {
            "Paksha" -> txt2?.text = PakshaHindi.get(txt2?.text.toString().toInt())
            "AmantMonth" -> {
                var monthInt = txt2?.text.toString().toInt()
                var monthStr = ""
                if (monthInt > 12) {
                    monthStr = MonthHindi.get(0) + " "
                    monthInt -= 12
                }
                monthStr += MonthHindi.get(monthInt)
                txt2?.setText(monthStr)
            }
            "Weekday" -> txt2?.text = WeekDayHindi.get(txt2?.text.toString().toInt())
            "Festivals" -> txt2?.text = txt2?.text.toString().replace(Regex("goo.gl/[a-zA-Z0-9]+"),"").replace("//","")
        }

        return currentItemView!!
    }
}