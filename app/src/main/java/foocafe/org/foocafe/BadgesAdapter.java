package foocafe.org.foocafe;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import foocafe.org.foocafe.entities.Badge;

class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.ViewHolder> {
    private List<Badge> badges;
    private Activity badgeActivity;
    private String TAG = "tag";

    BadgesAdapter(List<Badge> badges, Activity badgeActivity) {
        this.badges = badges;
        this.badgeActivity = badgeActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.badgecard, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Log.i(TAG, "onBindViewHolder: ");
        Glide.with(badgeActivity).load("http://www.foocafe.org" + badges.get(position).image).into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(v.getContext());

                dialog.setContentView(R.layout.badge_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView badgeName = dialog.findViewById(R.id.badgeNameText);
                TextView desc = dialog.findViewById(R.id.descriptionText);
                TextView criteria = dialog.findViewById(R.id.criteriaText);
                ImageView image = dialog.findViewById(R.id.imageView);

                badgeName.setText(badges.get(position).name);
                desc.setText(badges.get(position).description);
                criteria.setText(badges.get(position).criteria);
                Glide.with(badgeActivity).load("http://www.foocafe.org" + badges.get(position).image).into(image);

                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}