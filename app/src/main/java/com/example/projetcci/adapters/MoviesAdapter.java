package com.example.projetcci.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projetcci.R;
import com.example.projetcci.activities.MovieDetailActivity;
import com.example.projetcci.models.Movie;

import java.io.Serializable;
import java.util.List;

import static com.example.projetcci.utils.Constants.IMAGE_BASE_URL;

/**
 * Adapter for list pages
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movies;

    /**
     * Set up the adapter with the necessary configuration
     * @param context of application
     * @param movies list of movies retrieved from API
     */
    public MoviesAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    /**
     * Create movie card in the list
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    /**
     * Fill views with the needed data
     */
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    /**
     * @return the amount of items in the list
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * The view of every item that is displayed in the grid/list.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView poster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_movie_title);
            title.setSelected(true);
            poster = itemView.findViewById(R.id.item_movie_poster);
            //Send informations in the detail activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    intent = new Intent(context , MovieDetailActivity.class);
                    intent.putExtra("MOVIE_DETAILS", (Serializable) movies.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }


        /**
         * Fill views with the needed data
         * @param movie object
         */
        public void bind(Movie movie) {
            title.setText(movie.getTitle());
            Glide.with(context)
                    .load(IMAGE_BASE_URL + movie.getPosterPath())
                    .apply(RequestOptions.placeholderOf(R.color.gray))
                    .into(poster);
        }
    }
}