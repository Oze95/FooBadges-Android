package foocafe.org.foocafe

import android.app.Activity
import android.app.Dialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide

import foocafe.org.foocafe.entities.Badge

internal class BadgesAdapter(private val badges: List<Badge>, private val badgeActivity: Activity) : RecyclerView.Adapter<BadgesAdapter.ViewHolder>() {
    private val TAG = "tag"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.badgecard, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.i(TAG, "onBindViewHolder: ")
        Glide.with(badgeActivity).load("http://www.foocafe.org" + badges[position].image!!).into(holder.imageView)

        holder.cardView.setOnClickListener { v ->
            val dialog = Dialog(v.context)

            dialog.setContentView(R.layout.badge_dialog)
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val badgeName = dialog.findViewById<TextView>(R.id.badgeNameText)
            val desc = dialog.findViewById<TextView>(R.id.descriptionText)
            val criteria = dialog.findViewById<TextView>(R.id.criteriaText)
            val image = dialog.findViewById<ImageView>(R.id.imageView)

            badgeName.text = badges[position].name
            desc.text = badges[position].description
            criteria.text = badges[position].criteria
            Glide.with(badgeActivity).load("http://www.foocafe.org" + badges[position].image!!).into(image)

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return badges.size
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var cardView: CardView

        init {
            imageView = itemView.findViewById(R.id.image)
            cardView = itemView.findViewById(R.id.card_view)
        }
    }
}