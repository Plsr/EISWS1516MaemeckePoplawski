package de.rfunk.hochschulify.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.pojo.Course;
import de.rfunk.hochschulify.pojo.Entry;

/**
 * Created by Cheese on 12/01/16.
 */
public class CourseBookmarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface CourseAdapterInterface {
        public void onItemClick(int position);
    }

    private Context mContext;
    private List<Course> mCourses;
    private CourseAdapterInterface mListener;
    private static boolean mShowRemove;


    public CourseBookmarkAdapter(Context context, List<Course> courses, CourseAdapterInterface listener) {
        mContext = context;
        mCourses = courses;
        mListener = listener;
        mShowRemove = true;
    }

    public CourseBookmarkAdapter(Context context, List<Course> courses, CourseAdapterInterface listener, boolean showRemove) {
        mContext = context;
        mCourses = courses;
        mListener = listener;
        mShowRemove = showRemove;
    }

    public static class ViewHolderEntry extends RecyclerView.ViewHolder {

        TextView courseTitle;
        TextView universityName;
        TextView rating;
        CardView cardView;
        TextView remove;


        public ViewHolderEntry(View itemView) {
            super(itemView);

            courseTitle = (TextView) itemView.findViewById(R.id.course_title);
            universityName = (TextView) itemView.findViewById(R.id.university_name);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            remove = (TextView) itemView.findViewById(R.id.remove);
            rating = (TextView) itemView.findViewById(R.id.rating);

            if (!mShowRemove) remove.setVisibility(View.GONE);

            itemView.setTag(this);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_card, parent, false);

        return new ViewHolderEntry(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Course course = mCourses.get(position);
        TextView title = ((ViewHolderEntry) holder).courseTitle;
        TextView uniname = ((ViewHolderEntry) holder).universityName;
        CardView cardView = ((ViewHolderEntry) holder).cardView;
        TextView rating = ((ViewHolderEntry) holder).rating;

        title.setText(course.getName());
        uniname.setText(course.getUniversity().getName());
        if (course.getRecommendation() > -1) {
            rating.setText(((int) (course.getRecommendation() * 100)) + "%");
        } else {
            rating.setText("–");
            rating.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }
}
