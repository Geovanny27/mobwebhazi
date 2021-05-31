package hu.bme.aut.inventoryapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notifications",false)){
            val service = Intent(context, NotificationService::class.java)
            service.putExtra("reason", intent.getStringExtra("reason"))
            service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))

            context.startService(service)
        }
    }

}