package foocafe.org.foocafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private final Activity eventListActivity;
    private List<Event> events;
    private TinyDB db;
    private String UIDcache;
    private String eventID;
    private boolean loggedIn = false;
    private Session session;

    private boolean check =false;

    EventListAdapter(List<Event> events, Activity eventListActivity) {
        this.events = events;
        this.eventListActivity = eventListActivity;
        db = new TinyDB(eventListActivity);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.eventcard, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final String toSendDesc = events.get(position).description;
        final String title = events.get(position).title;
        final String subTitle = events.get(position).subtitle;
        final String time = events.get(position).time;
        final String url = events.get(position).url;
        final int pos = position;

        DateFormat orginalFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat targetFormat = new SimpleDateFormat("EEEE, MMM d", Locale.ENGLISH);
        Date date = null;
        try {
            date = orginalFormat.parse(events.get(position).date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final String formattedDate = targetFormat.format(date);


        holder.title.setText(events.get(position).title);
        holder.subtitle.setText(events.get(position).subtitle);
        holder.dateTime.setText(events.get(position).time +" " + formattedDate);
        Glide.with(eventListActivity).load("http://www.foocafe.org" + events.get(position).image).into(holder.imageView);

        if(events.get(position).checkmark){
            holder.imageView2.setImageResource(R.mipmap.checkmark);
            check = true;

        }
        if(eventListActivity instanceof EventListActivity) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), EventDescriptionActivity.class);
                    i.putExtra("desc", toSendDesc);
                    i.putExtra("date", formattedDate);
                    i.putExtra("title",title);
                    i.putExtra("subtitle",subTitle);
                    i.putExtra("time",time);
                    i.putExtra("url",url);
                    view.getContext().startActivity(i);
                    ((Activity) view.getContext()).finish();



                }
            });
        } else if(eventListActivity instanceof CheckInActivity){
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    session = new Session(eventListActivity.getApplicationContext());

                    UIDcache = ((CheckInActivity) eventListActivity).getUIDcache();

                        if(!events.get(pos).checkmark && !check ){
                            check = true;                   // will act like a semafor and will also enable only one checkin
                            eventID = events.get(pos).event;
                            //ask user if they want to checkin
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                            alertDialogBuilder.setMessage("Do you want to check in to this event");
                            alertDialogBuilder.setPositiveButton("yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            if(((CheckInActivity) eventListActivity).checkin()){
                                                events.get(pos).checkmark=true;
                                                holder.imageView2.setImageResource(R.mipmap.checkmark);
                                                db.putListObject(UIDcache, (ArrayList<Event>) events);
                                            } else{
                                                check =false;
                                            }
                                        }
                                    });

                            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    check = false;
                                    return;
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();

                        } else if (events.get(pos).checkmark){

                            // ask if uncheck
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                            alertDialogBuilder.setMessage("Do you want to undo the check in for this event");
                            alertDialogBuilder.setPositiveButton("yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            events.get(pos).checkmark=false;
                                            check =false;
                                            db.putListObject(UIDcache, (ArrayList<Event>) events);
                                            holder.imageView2.setImageResource(0);
                                        }
                                    });

                            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();

                        }


                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public String getEventID(){return eventID;}

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView subtitle;
        ImageView imageView,imageView2;
        TextView title;
        TextView dateTime;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            dateTime = (TextView) itemView.findViewById(R.id.dateTime);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            imageView2 = (ImageView) itemView.findViewById(R.id.image2);
        }
    }
}
