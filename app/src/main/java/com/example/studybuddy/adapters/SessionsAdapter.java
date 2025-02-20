package com.example.studybuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.studybuddy.R;
import com.example.studybuddy.models.Session;
import java.util.List;

public class SessionsAdapter extends ArrayAdapter<Session> {
    private Context context;
    private List<Session> sessions;

    public SessionsAdapter(Context context, List<Session> sessions) {
        super(context, 0, sessions);
        this.context = context;
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_session, parent, false);
        }

        Session session = sessions.get(position);

        TextView titleText = convertView.findViewById(R.id.sessionTitleText);
        TextView timeText = convertView.findViewById(R.id.sessionTimeText);
        TextView locationText = convertView.findViewById(R.id.sessionLocationText);

        titleText.setText(session.getTitle());
        timeText.setText(String.format("%s - %s",
                session.getStartTime(), session.getEndTime()));
        locationText.setText(session.getLocation());

        return convertView;
    }

    @Override
    public int getCount() {
        return sessions.size();
    }
}