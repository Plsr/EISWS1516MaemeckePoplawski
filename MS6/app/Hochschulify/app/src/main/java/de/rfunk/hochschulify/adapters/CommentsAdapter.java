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
import de.rfunk.hochschulify.pojo.Course;
import de.rfunk.hochschulify.pojo.Entry;

/**
 * Created by Cheese on 14/01/16.
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Entry> mEntries;
    private CommentsAdapterInterface mListener;


    public interface CommentsAdapterInterface {
        public void onItemClick(int position);
    }

    public CommentsAdapter (Context context, List<Entry> entries, CommentsAdapterInterface listener) {
        mContext = context;
        mEntries = entries;
        mListener = listener;
    }

    public static class ViewHolderEntry extends RecyclerView.ViewHolder {

        TextView entryText;
        TextView entryAuthor;
        TextView answer;
        ImageView verifyIcon;



        public ViewHolderEntry(View itemView) {
            super(itemView);

            entryText = (TextView) itemView.findViewById(R.id.content);
            entryAuthor = (TextView) itemView.findViewById(R.id.author);
            answer = (TextView) itemView.findViewById(R.id.answer);
            verifyIcon = (ImageView) itemView.findViewById(R.id.verifiedIndicatorComment);


            itemView.setTag(this);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card, parent, false);

        return new ViewHolderEntry(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Entry entry = mEntries.get(position);
        TextView text = ((ViewHolderEntry) holder).entryText;
        TextView author = ((ViewHolderEntry) holder).entryAuthor;
        TextView answer = ((ViewHolderEntry) holder).answer;
        ImageView verificationIcon = ((ViewHolderEntry) holder).verifyIcon;


        text.setText(entry.getText());
        author.setText(entry.getAuthor().getName());

        if (entry.getAuthor().isVerified()) {
            verificationIcon.setVisibility(View.VISIBLE);
        }


        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }
}
