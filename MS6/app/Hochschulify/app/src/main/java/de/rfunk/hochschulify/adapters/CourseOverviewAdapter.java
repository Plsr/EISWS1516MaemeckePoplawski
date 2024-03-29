package de.rfunk.hochschulify.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.pojo.Entry;

/**
 * Created by Cheese on 12/01/16.
 */
public class CourseOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface EntryAdapterInterface {
        public void onItemClick(int position);
    }

    private Context mContext;
    private List<Entry> mEntries;
    private EntryAdapterInterface mInterface;


    public CourseOverviewAdapter(Context context, List<Entry> entries, EntryAdapterInterface rInterface) {
        mContext = context;
        mEntries = entries;
        mInterface = rInterface;
    }

    public static class ViewHolderEntry extends RecyclerView.ViewHolder {

        TextView threadTitle;
        TextView threadBody;
        TextView threadComments;
        TextView threadAuthor;
        CardView cardView;
        ImageView verifiedIndicator;
        ImageView thumb;


        public ViewHolderEntry(View itemView) {
            super(itemView);

            threadTitle = (TextView) itemView.findViewById(R.id.thread_title_input);
            threadBody = (TextView) itemView.findViewById(R.id.thread_excerpt);
            threadComments = (TextView) itemView.findViewById(R.id.comments);
            threadAuthor = (TextView) itemView.findViewById(R.id.author);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            verifiedIndicator = (ImageView) itemView.findViewById(R.id.verifiedIndicator);
            thumb = (ImageView) itemView.findViewById(R.id.thumb);
            itemView.setTag(this);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_card_course, parent, false);

        return new ViewHolderEntry(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Entry entry = mEntries.get(position);
        TextView title = ((ViewHolderEntry) holder).threadTitle;
        TextView body = ((ViewHolderEntry) holder).threadBody;
        TextView comments = ((ViewHolderEntry) holder).threadComments;
        TextView author = ((ViewHolderEntry) holder).threadAuthor;
        CardView cardView = ((ViewHolderEntry) holder).cardView;
        ImageView verifiedIndicator = ((ViewHolderEntry) holder).verifiedIndicator;
        ImageView thumb = ((ViewHolderEntry) holder).thumb;

        title.setText(entry.getTitle());
        body.setText(entry.getText());
        comments.setText(entry.getSubCount().toString());
        author.setText(entry.getAuthor().getName());

        if (entry.getAuthor().isVerified()) {
            verifiedIndicator.setVisibility(View.VISIBLE);
        }

        if (entry.hasRecommendation()) {
            if (entry.isRecommendation() == false) thumb.setImageDrawable(mContext.getDrawable(R.drawable.thumbs_down_active));
            thumb.setVisibility(View.VISIBLE);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterface.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }
}
