package foocafe.org.foocafe

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

import foocafe.org.foocafe.entities.Event

internal class EventListAdapter(private val events: List<Event>, private val eventListActivity: Activity) : RecyclerView.Adapter<EventListAdapter.ViewHolder>() {
    private val db: TinyDB
    private var UIDcache: String? = null
    var eventID: String? = null
        private set
    private var session: Session? = null

    private var check = false

    init {
        db = TinyDB(eventListActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.eventcard, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val toSendDesc = events[position].description
        val title = events[position].title
        val subTitle = events[position].subtitle
        val time = events[position].time
        val url = events[position].url

        val orginalFormat = SimpleDateFormat("yyyy-MM-dd")
        val targetFormat = SimpleDateFormat("EEEE, MMM d", Locale.ENGLISH)
        var date: Date? = null
        try {
            date = orginalFormat.parse(events[position].date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val formattedDate = targetFormat.format(date)

        holder.title.text = events[position].title
        holder.subtitle.text = events[position].subtitle
        holder.dateTime.text = String.format("%s %s", events[position].time, formattedDate)
        Glide.with(eventListActivity).load("http://www.foocafe.org" + events[position].image!!).into(holder.imageView)

        if (events[position].checkmark!!) {
            holder.imageView2.setImageResource(R.mipmap.checkmark)
            check = true
        }
        if (eventListActivity is EventListActivity) {
            holder.cardView.setOnClickListener { view ->
                val i = Intent(view.context, EventDescriptionActivity::class.java)
                i.putExtra("desc", toSendDesc)
                i.putExtra("date", formattedDate)
                i.putExtra("title", title)
                i.putExtra("subtitle", subTitle)
                i.putExtra("time", time)
                i.putExtra("url", url)
                view.context.startActivity(i)
                (view.context as Activity).finish()
            }
        } else if (eventListActivity is CheckInActivity) {
            holder.cardView.setOnClickListener { view ->
                session = Session(eventListActivity.getApplicationContext())

                UIDcache = eventListActivity.uiDcache

                if ((!events[position].checkmark!!) && !check) {
                    check = true                   // will act like a semafor and will also enable only one checkin
                    eventID = events[position].event

                    val alertDialogBuilder = AlertDialog.Builder(view.context)
                    alertDialogBuilder.setMessage("Do you want to check in to this event")
                    alertDialogBuilder.setPositiveButton("yes"
                    ) { arg0, arg1 ->
                        if (eventListActivity.checkin()) {
                            events[position].checkmark = true
                            holder.imageView2.setImageResource(R.mipmap.checkmark)
                            db.putListObject(UIDcache, events as ArrayList<Event>)
                        } else {
                            check = false
                        }
                    }
                    alertDialogBuilder.setNegativeButton("No") { dialog, which -> check = false }

                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.setCanceledOnTouchOutside(false)
                    alertDialog.show()
                } else if (events[position].checkmark!!) {
                    val alertDialogBuilder = AlertDialog.Builder(view.context)
                    alertDialogBuilder.setMessage("Do you want to undo the check in for this event")
                    alertDialogBuilder.setPositiveButton("yes"
                    ) { arg0, arg1 ->
                        events[position].checkmark = false
                        check = false
                        db.putListObject(UIDcache, events as ArrayList<Event>)
                        holder.imageView2.setImageResource(0)
                    }

                    alertDialogBuilder.setNegativeButton("No") { dialog, which -> }

                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.setCanceledOnTouchOutside(false)
                    alertDialog.show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var subtitle: TextView
        var imageView: ImageView
        var imageView2: ImageView
        var title: TextView
        var dateTime: TextView
        var cardView: CardView

        init {

            title = itemView.findViewById(R.id.title)
            imageView = itemView.findViewById(R.id.image)
            subtitle = itemView.findViewById(R.id.subtitle)
            dateTime = itemView.findViewById(R.id.dateTime)
            cardView = itemView.findViewById(R.id.card_view)
            imageView2 = itemView.findViewById(R.id.image2)
        }
    }
}