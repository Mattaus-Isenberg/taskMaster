package com.echokinetic.taskmaster.dummy;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echokinetic.taskmaster.R;
import com.echokinetic.taskmaster.Task;
import com.echokinetic.taskmaster.TaskState;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Task} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyTaskRecyclerViewAdapter extends RecyclerView.Adapter<MyTaskRecyclerViewAdapter.ViewHolder> {

    private final List<Task> mValues;
    private OnListFragmentInteractionListener mListener;
    Context mContext;

    public MyTaskRecyclerViewAdapter(List<Task> items, Context context, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dark_row_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mTitle.setText(mValues.get(position).getTitle());

        TaskState state = mValues.get(position).getState();
        if(state.equals(TaskState.NEW))
        {
            int unicode = 0x1F7E2;
            String emoji = new String(Character.toChars(unicode));
            holder.mState.setText(emoji);
        }
        else if(state.equals(TaskState.ASSIGNED))
        {
            int unicode = 0x1F7E1;
            String emoji = new String(Character.toChars(unicode));
            holder.mState.setText(emoji);
        }
        else if(state.equals(TaskState.IN_PROGRESS))
        {
            int unicode = 0x1F7E0;
            String emoji = new String(Character.toChars(unicode));
            holder.mState.setText(emoji);
        }
        else if(state.equals(TaskState.COMPLETE))
        {
            int unicode = 0x1F534;
            String emoji = new String(Character.toChars(unicode));
            holder.mState.setText(emoji);
        }
        else if(state.equals(TaskState.HIGH_PRIORITY))
        {
            int unicode = 0x2B50;
            String emoji = new String(Character.toChars(unicode));
            holder.mState.setText(emoji);
        }



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListFragmentInteraction(holder.mItem);
//                    Intent intent = new Intent(mContext, detailPage.class);
//                    intent.putExtra("title", mValues.get(position).getTitle());
//                    intent.putExtra("description", mValues.get(position).getBody());
//                    intent.putExtra("state", mValues.get(position).getState().toString());
//                    intent.putExtra("ID", mValues.get(position).getId());
//                    mContext.startActivity(intent);
                }

    });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void removeItem(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public List<Task> getData() {
        return mValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle;
        public final TextView mBody;
        public final TextView mState;
        public Task mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.title_Fragment);
            mBody = (TextView) view.findViewById(R.id.body_Fragment);
            mState = (TextView) view.findViewById(R.id.state_Fragment);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBody.getText() + "'";
        }
    }

    public interface OnListFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Task item);

    }
}
