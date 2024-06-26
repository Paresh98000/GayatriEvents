package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterServiceProductForEvent
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductDisplayModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable
import com.pareshkumarsharma.gayatrievents.utilities.Database

internal class ServiceProductForEvent : AppCompatActivity() {


    internal val CurrentActivity = this

    private lateinit var adapterService: PSBSArrayAdapterServiceProductForEvent
    private lateinit var listViewServiceProduct: ListView
    private lateinit var existingServiceProducts : DataTable

    internal companion object{
        var SelectedServiceId = mutableListOf<String>()
        var SelectedProductId = mutableListOf<String>()
        var SelectedProductName = mutableListOf<String>()
        var SelectedProductPrice = mutableListOf<Float>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_product_for_event)

        findViewById<Button>(R.id.btnSaveSelected).setOnClickListener {
            SelectedProductId = adapterService.SelectedProductId
            SelectedProductName = adapterService.SelectedProductNames
            SelectedProductPrice = adapterService.SelectedProductPrices
            finish()
        }

        existingServiceProducts = Database.getServicesProductByServiceList(SelectedServiceId.joinToString(","))
        listViewServiceProduct = findViewById<ListView>(R.id.listview_ExistingServicesProduct)
        adapterService =
            PSBSArrayAdapterServiceProductForEvent(this, R.layout.listview_item_service_product_for_event, existingServiceProducts.Rows)
        adapterService.SelectedProductId = SelectedProductId
        listViewServiceProduct.adapter = adapterService
        listViewServiceProduct.setOnItemClickListener { adapterView, view, i, l ->
            NewEvent.Selected_Service_Product_Global_Id = existingServiceProducts.Rows[i][1]
            val builder = AlertDialog.Builder(this)
            builder.setTitle(existingServiceProducts.Rows[i][2])
            var product_text = ""
            val tbl_details_of_product = Database.getServicesProductDetails(existingServiceProducts.Rows[i][1])
            for (row in tbl_details_of_product.Rows){
                product_text += "> "+row[2] + "\n------------\n" + row[3] + "\n\n"
            }
            if(product_text.trim().length==0)
                product_text = existingServiceProducts.Rows[i][3]
            builder.setMessage(product_text)
//            builder.setPositiveButton(
//                "Edit",
//                DialogInterface.OnClickListener { dialogInterface, j ->
//                    NewServiceProduct.selectedServiceId = selectedServiceId
//                    NewServiceProduct.operation = 'U'
//                    NewServiceProduct.GlobalId = existingServiceProducts.Rows[i][1]
//                    NewServiceProduct.SPT = existingServiceProducts.Rows[i][2]
//                    NewServiceProduct.SPD = existingServiceProducts.Rows[i][3]
//                    NewServiceProduct.SPP = existingServiceProducts.Rows[i][4].toFloat()
//                    CurrentActivity.startActivity(Intent(CurrentActivity,NewServiceProduct::class.java))
//                })
//            builder.setNeutralButton(
//                "Details",
//                DialogInterface.OnClickListener { dialogInterface, j ->
//                    val inn = Intent(CurrentActivity, ServiceProductDetailsForEvent::class.java)
//                    CurrentActivity.startActivity(inn)
//                })
            builder.setNegativeButton(
                "Ok",
                DialogInterface.OnClickListener { dialogInterface, j -> dialogInterface.dismiss()
//                    finish()
                })
            builder.show()
        }
    }

    override fun onResume() {
        Thread(Runnable {
            APICalls.setContext(this)
            APICalls.cookies = mapOf<String, String>(
                Pair(
                    "token",
                    getSharedPreferences(
                        Database.SHAREDFILE,
                        MODE_PRIVATE
                    ).getString("token", "").toString()
                ),
                Pair(
                    "expires",
                    getSharedPreferences(
                        Database.SHAREDFILE,
                        MODE_PRIVATE
                    ).getString("expires", "").toString()
                )
            )
            if (APICalls.getExistingServiceProductOfCurrentUser(SelectedServiceId.joinToString())) {
                val res = APICalls.lastCallObject as Array<ServiceProductDisplayModel>
                for (i in 0..res.size - 1) {
                    val c = ContentValues()
                    c.put("GlobalId", res[i].GlobalId)
                    c.put("ServiceGlobalId", res[i].ServiceGlobalId)
                    c.put("Title", res[i].Title)
                    c.put("SmallDesc", res[i].Desc)
                    c.put("Price", res[i].Price)
                    c.put("CreationDate", res[i].CreationDate)
                    val tbl = Database.query("Select Id From Service Where GlobalId = '${res[i].GlobalId}'")
                    if(tbl.Rows.size>0 && !tbl.Columns.contains("Error")){
                        c.put("ServiceId", tbl.Rows[0][0].toInt())
                    }
                    if (Database.getRowCount(
                            "Service_Product",
                            "GlobalId",
                            c.getAsString("GlobalId").toString()
                        ) == 0
                    )
                        Database.insertTo("Service_Product", c, "Id")
                    else
                        Database.updateTo("Service_Product", c,"GlobalId=?",listOf(res[i].GlobalId).toTypedArray())
                }
                existingServiceProducts = Database.getServicesProductByServiceList(SelectedServiceId.joinToString(","))
                runOnUiThread {
                    listViewServiceProduct = findViewById<ListView>(R.id.listview_ExistingServicesProduct)
                    adapterService.updateData(existingServiceProducts.Rows)
                    adapterService.notifyDataSetChanged()
                    listViewServiceProduct.deferNotifyDataSetChanged()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        APICalls.lastCallMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }).start()

        super.onResume()
    }
}