package com.floow.josueherrero.floowapp.ui.list;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.floow.josueherrero.floowapp.R;
import com.floow.josueherrero.floowapp.ui.list.items.Path;
import com.floow.josueherrero.floowapp.utils.DateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JosuéManuel on 17/06/2017.
 *
 * This is the adapter for the path list, when an item is clicked it shows the additional information.
 * Every item has a button for printing its path on the map.
 */

public final class ListAdapter extends RecyclerView.Adapter<ListAdapter.RowViewHolder> {

    private List<Path> pathLocationDataItemList;
    private ListListener pathListListener;

    public ListAdapter(
            final List<Path> barLocationDataItemList,
            final ListListener barListListener) {

        this.pathLocationDataItemList = barLocationDataItemList;
        this.pathListListener = barListListener;
    }

    @Override
    public RowViewHolder onCreateViewHolder(
            final ViewGroup parent,
            final int viewType) {

        return new RowViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(
            final RowViewHolder holder,
            final int position) {

        (holder).bind(pathLocationDataItemList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return pathLocationDataItemList == null ? 0 : pathLocationDataItemList.size();
    }

    public void setData(final List<Path> barLocationDataItemList) {
        this.pathLocationDataItemList = barLocationDataItemList;
        notifyDataSetChanged();
    }

    class RowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.name_textview) TextView nameTextView;
        @BindView(R.id.starting_time_data) TextView startingDateTextView;
        @BindView(R.id.end_time_data) TextView endDateTextView;
        @BindView(R.id.detail_container) RelativeLayout detailContainer;
        @BindView(R.id.print_button) Button printButton;

        private int position = -1;

        public RowViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bind(
                final Path path,
                final int position) {

            this.position = position;
            if (!TextUtils.isEmpty(path.getName())) {
                nameTextView.setText(path.getName());
            }
            if (!TextUtils.isEmpty(Long.toString(path.getStartTime()))) {
                startingDateTextView.setText(DateUtil.formatDate(path.getStartTime()));
            }
            if (!TextUtils.isEmpty(Long.toString(path.getEndTime()))) {
                endDateTextView.setText(DateUtil.formatDate(path.getEndTime()));
            }
            if (path.isExpanded()) {
                detailContainer.setVisibility(View.VISIBLE);
            } else {
                detailContainer.setVisibility(View.GONE);
            }
            printButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pathListListener.onPathSelected(position);
                }
            });
        }

        @Override
        public void onClick(final View v) {
            if (pathListListener != null
                    && position != -1) {

                if (pathLocationDataItemList.get(position).isExpanded()) {
                    detailContainer.setVisibility(View.GONE);
                    pathLocationDataItemList.get(position).setExpanded(false);
                } else {
                    detailContainer.setVisibility(View.VISIBLE);
                    for (Path path: pathLocationDataItemList) {
                        path.setExpanded(false);
                    }
                    pathLocationDataItemList.get(position).setExpanded(true);
                    notifyDataSetChanged();
                }
            }
        }

    }

}