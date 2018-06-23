package com.kidscademy.quiz.instruments.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.R;
import com.kidscademy.app.AppBase;
import com.kidscademy.commons.Recommended;

import js.format.Format;
import js.format.LongDate;
import js.log.Log;
import js.log.LogFactory;
import js.util.BitmapLoader;

/**
 * List adapter for recommended apps.
 *
 * @author Iulian Rotaru
 */
public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.Holder> {
    private static final Log log = LogFactory.getLog(SharingAdapter.class);

    private final LayoutInflater inflater;
    private final Listener listener;
    private final Recommended[] recommended;

    public RecommendedAdapter(Context context, Recommended[] recommended) {
        super();

        this.inflater = LayoutInflater.from(context);
        this.listener = (Listener) context;
        this.recommended = recommended;
    }

    @Override
    public RecommendedAdapter.Holder onCreateViewHolder(ViewGroup viewGroup, int position) {
        log.trace("onCreateViewHolder(ViewGroup, int)");
        View view = inflater.inflate(R.layout.item_recommended, viewGroup, false);
        RecommendedAdapter.Holder holder = new RecommendedAdapter.Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecommendedAdapter.Holder holder, int position) {
        log.trace("onBindViewHolder(ViewHolder, int)");
        holder.bindPosition(position);
    }

    @Override
    public int getItemCount() {
        return recommended.length;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Format dateFormat;

        private final TextView nameText;
        private final TextView descriptionText;
        private final TextView versionText;
        private final TextView lastUpdate;
        private final ImageView iconView;

        private Recommended app;

        public Holder(View view) {
            super(view);

            this.dateFormat = new LongDate();

            nameText = view.findViewById(R.id.recommended_app_name);
            descriptionText = view.findViewById(R.id.recommended_app_description);
            versionText = view.findViewById(R.id.recommended_app_version);
            lastUpdate = view.findViewById(R.id.recommended_app_update);
            iconView = view.findViewById(R.id.recommended_app_icon);

            view.setOnClickListener(this);
        }

        public void bindPosition(int position) {
            app = recommended[position];

            nameText.setText(app.getName());
            descriptionText.setText(app.getDescription());
            versionText.setText(app.getVersion());
            lastUpdate.setText(dateFormat.format(app.getLastUpdate()));

            BitmapLoader loader = new BitmapLoader(AppBase.storage().getFile(app.getIconPath()), iconView);
            loader.start();
        }

        @Override
        public void onClick(View v) {
            listener.onRecommendedAppSelected(app);
        }
    }

    public static interface Listener {
        void onRecommendedAppSelected(Recommended app);
    }
}
