package com.floow.josueherrero.floowapp.ui.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.floow.josueherrero.floowapp.R;
import com.floow.josueherrero.floowapp.ui.list.items.Path;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JosuéManuel on 22/06/2017.
 *
 * This is the fragment, it just displays the path list.
 */

public final class ListFragment extends Fragment
        implements ListListener {

    @BindView(R.id.recyclerView) RecyclerView barLocationRecyclerView;

    private ListAdapter barListAdapter;
    private Callback callback;

    public static ListFragment newInstance() {
        return  new ListFragment();
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_bar_location, container, false);
        ButterKnife.bind(this,view);
        barListAdapter = new ListAdapter(new ArrayList<Path>(), this);
        barLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        barLocationRecyclerView.setAdapter(barListAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.callback = (Callback) this.getActivity();
        } catch (ClassCastException var3) {
            throw new ClassCastException(context.toString() + " must implement " + this.callback.getClass().getSimpleName());
        }
    }

    @Override
    public void onViewCreated(
            final View view,
            final @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        callback.onListCreated();
    }

    public void setBarListItems(final List<Path> dataItems) {
        if (barListAdapter!=null) {
            barListAdapter.setData(dataItems);
        }
    }

    @Override
    public void onPathSelected(final int position) {
        callback.onPrintPath(position);
    }

    public interface Callback {

        void onPrintPath(int position);

        void onListCreated();

    }

}