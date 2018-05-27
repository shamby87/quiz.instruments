package com.kidscademy.instruments.quiz.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.instruments.quiz.App;
import com.kidscademy.instruments.quiz.R;

import java.util.List;

import js.log.Log;
import js.log.LogFactory;
import js.util.BitmapLoader;

public final class LevelInstrumentsAdapter extends RecyclerView.Adapter<LevelInstrumentsAdapter.Holder> {
    private static final Log log = LogFactory.getLog(LevelInstrumentsAdapter.class);

    private Context context;
    private Listener listener;
    private LayoutInflater inflater;
    private Level level;
    private List<Integer> instrumentIndices;

    public LevelInstrumentsAdapter(Context context, Level level) {
        super();
        this.context = context;
        this.listener = (Listener) context;
        this.inflater = LayoutInflater.from(context);
        this.level = level;
        this.instrumentIndices = level.getInstrumentIndices();
    }

    @Override
    public LevelInstrumentsAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        log.trace("onCreateViewHolder(ViewGroup, int)");
        View view = inflater.inflate(R.layout.item_level_instruments, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LevelInstrumentsAdapter.Holder holder, int position) {
        log.trace("onBindViewHolder(ViewHolder, int)");
        holder.bindPosition(position);
    }

    @Override
    public int getItemCount() {
        return instrumentIndices.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iconView;
        private TextView nameView;
        private ImageView checkView;

        private int position;
        private Instrument instrument;

        public Holder(View view) {
            super(view);
            iconView = view.findViewById(R.id.item_level_brands_icon);
            nameView = view.findViewById(R.id.item_level_brands_name);
            checkView = view.findViewById(R.id.item_level_brands_check);
            iconView.setOnClickListener(this);
        }

        public void bindPosition(int position) {
            this.position = position;
            instrument = App.storage().getInstrument(instrumentIndices.get(position));
            boolean isSolved = level.getState().isSolvedInstrument(position);

            BitmapLoader loader = new BitmapLoader(context, instrument.getPicturePath(), iconView, 2);
            loader.start();

            String display = isSolved ? instrument.getDisplay() : "UNSOLVED";
            nameView.setText(display);

            checkView.setVisibility(isSolved ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            listener.onInstrumentSelected(position, instrument);
        }
    }

    public static interface Listener {
        void onInstrumentSelected(int position, Instrument instrument);
    }
}