package com.kidscademy.quiz.instruments.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kidscademy.quiz.instruments.App;
import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.instruments.util.Assets;
import com.kidscademy.quiz.instruments.view.HexaIcon;

import js.log.Log;
import js.log.LogFactory;
import js.util.BitmapLoader;

public class LevelsCardAdapter extends RecyclerView.Adapter<LevelsCardAdapter.Holder> {
    private static final Log log = LogFactory.getLog(LevelsCardAdapter.class);

    private Context context;
    private Listener listener;
    private LayoutInflater inflater;
    private Level[] levels;

    public LevelsCardAdapter(Context context, Level[] levels) {
        super();
        log.trace("InstrumentsCardAdapter(Context, Level[])");
        this.context = context;
        assert context instanceof Listener;
        this.listener = (Listener) context;
        this.inflater = LayoutInflater.from(context);
        this.levels = levels;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int position) {
        log.trace("onCreateViewHolder(ViewGroup, int)");
        View view = inflater.inflate(R.layout.compo_card_level, viewGroup, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        log.trace("onBindViewHolder(ViewHolder, int)");
        holder.bindPosition(position);
    }

    @Override
    public int getItemCount() {
        return levels.length;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView backgroundView;
        private TextView nameText;
        private TextView scoreText;
        private TextView instrumentsCountText;
        private TextView solvedInstrumentsText;
        private HexaIcon actionView;
        private ProgressBar progressBar;

        private Level level;

        public Holder(View view) {
            super(view);

            backgroundView = view.findViewById(R.id.card_level_bg);

            nameText = view.findViewById(R.id.card_level_name);
            scoreText = view.findViewById(R.id.card_level_score);
            instrumentsCountText = view.findViewById(R.id.card_level_total);
            solvedInstrumentsText = view.findViewById(R.id.card_level_done);

            actionView = view.findViewById(R.id.card_level_action);
            actionView.setOnClickListener(this);

            progressBar = (ProgressBar) view.findViewById(R.id.card_level_progress);
        }

        public void bindPosition(int position) {
            level = levels[position];
            final LevelState levelState = App.storage().getLevelState(level.getIndex());

            final int instrumentsCount = level.getInstrumentsCount();
            final int solvedInstruments = levelState.getSolvedInstrumentsCount();
            final int progress = 100 * solvedInstruments / instrumentsCount;

            BitmapLoader loader = new BitmapLoader(context.getResources(), Assets.getLevelBackgroundId(position), backgroundView);
            loader.start();

            if (levelState.isUnlocked()) {
                actionView.setBackgroundColor(ContextCompat.getColor(context, Assets.getColor(position)));
                actionView.setVisibility(View.VISIBLE);
            } else {
                actionView.setVisibility(View.INVISIBLE);
            }

            nameText.setText(Assets.getLevelName(context, level.getIndex()));
            instrumentsCountText.setText(Integer.toString(instrumentsCount));
            solvedInstrumentsText.setText(Integer.toString(solvedInstruments));
            scoreText.setText(Integer.toString(levelState.getScore()));

            progressBar.setProgressDrawable(Assets.getProgressDrawable(context, position));
            progressBar.setProgress(progress);
        }

        @Override
        public void onClick(View v) {
            listener.onLevelSelected(level);
        }
    }

    public static interface Listener {
        void onLevelSelected(Level level);
    }
}
