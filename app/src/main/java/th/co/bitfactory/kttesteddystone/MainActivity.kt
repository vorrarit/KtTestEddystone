package th.co.bitfactory.kttesteddystone

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.NumberFormat


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.name
    }

    var bScanning = false
    var mMessageListener: MessageListener? = null
    var bFound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        txtIsFound.text = "Not Found"

        mMessageListener = object:MessageListener() {
            override fun onFound(message: Message?) {
                Log.d(TAG, "Found message: ${String(message?.content!!)}")
                bFound = true
                txtIsFound.text = "Found"
            }

            override fun onLost(message: Message?) {
                Log.d(TAG, "Lost message: ${String(message?.content!!)}")
                bFound = false
//                txtDistance.text = "---"
                txtIsFound.text = "Lost"
            }

            override fun onBleSignalChanged(message: Message?, bleSignal: BleSignal?) {
                Log.d(TAG, "Ble Signal: RSSI ${bleSignal?.rssi}, TX Power ${bleSignal?.txPower}")
            }

            override fun onDistanceChanged(message: Message?, distance: Distance?) {
                Log.d(TAG, "Ble Signal: Metres ${distance?.meters}, Accuracy ${distance?.accuracy}")
                txtDistance.text = NumberFormat.getInstance().format(distance?.meters)
            }
        }

        fab.setOnClickListener { view ->

            if (bScanning) {
                mMessageListener?.let {
                    Nearby.getMessagesClient(this).unsubscribe(it)
                    bScanning = false
                    Snackbar.make(view, "Stop Scanning", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()

                }
            } else {
                mMessageListener?.let {
                    Nearby.getMessagesClient(this).subscribe(
                            it,
                            SubscribeOptions.Builder()
                                    .setStrategy(Strategy.BLE_ONLY)
                                    .build()
                    )
                    bScanning = true
                    Snackbar.make(view, "Start Scanning", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()

                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
