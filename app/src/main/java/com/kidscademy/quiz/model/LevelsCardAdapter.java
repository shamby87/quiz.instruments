package com.kidscademy.quiz.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kidscademy.quiz.app.App;
import com.kidscademy.quiz.instruments.R;
import com.kidscademy.quiz.util.Assets;
import com.kidscademy.quiz.view.HexaIcon;

import java.util.Locale;

import js.log.Log;
import js.log.LogFactory;
import js.util.BitmapLoader;

/**
 * Recycle view adapter for level card from levels list activity.
 *
 * @author Iulian Rotaru
 */
public class LevelsCardAdapter extends RecyclerView.Adapter<LevelsCardAdapter.Holder> {
    private static final Log log = LogFactory.getLog(LevelsCardAdapter.class);

    private final Context context;
    private final Listener listener;
    private final LayoutInflater inflater;
    private final Level[] levels;

    public LevelsCardAdapter(Context context, Level[] levels) {
        super();
        log.trace("InstrumentsCardAdapter(Context, Level[])"); // NON-NLS
        //Params.notNull(levels, "Levels"); // NON-NLS
        this.context = context;
        this.listener = (Listener) context;
        this.inflater = LayoutInflater.from(context);
        this.levels = levels;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int position) {
        log.trace("onCreateViewHolder(ViewGroup, int)"); // NON-NLS
        View view = inflater.inflate(R.layout.compo_card_level, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        log.trace("onBindViewHolder(ViewHolder, int)"); // NON-NLS
        holder.bindPosition(position);
    }

    @Override
    public int getItemCount() {
        return levels.length;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView backgroundView;
        private final TextView nameText;
        private final TextView scoreText;
        private final TextView instrumentsCountText;
        private final TextView solvedInstrumentsText;
        private final HexaIcon actionView;
        private final ProgressBar progressBar;

        private Level level;

        Holder(View view) {
            super(view);

            backgroundView = view.findViewById(R.id.card_level_bg);

            nameText = view.findViewById(R.id.card_level_name);
            scoreText = view.findViewById(R.id.card_level_score);
            instrumentsCountText = view.findViewById(R.id.card_level_total);
            solvedInstrumentsText = view.findViewById(R.id.card_level_done);

            actionView = view.findViewById(R.id.card_level_action);
            actionView.setOnClickListener(this);

            progressBar = view.findViewById(R.id.card_level_progress);
        }

        void bindPosition(int position) {
            level = levels[position];
            final LevelState levelState = App.instance().storage().getLevelState(level.getIndex());

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
            nameText.setTag("level" + level.getIndex()); // NON-NLS

            instrumentsCountText.setText(String.format(Locale.getDefault(), "%d", instrumentsCount)); //NON-NLS
            solvedInstrumentsText.setText(String.format(Locale.getDefault(), "%d", solvedInstruments)); //NON-NLS
            scoreText.setText(String.format(Locale.getDefault(), "%d", levelState.getScore())); //NON-NLS

            progressBar.setProgressDrawable(Assets.getProgressDrawable(context, position));
            progressBar.setProgress(progress);
        }

        @Override
        public void onClick(View v) {
            listener.onLevelSelected(level);
        }
    }

    public interface Listener {
        void onLevelSelected(Level level);
    }
}
