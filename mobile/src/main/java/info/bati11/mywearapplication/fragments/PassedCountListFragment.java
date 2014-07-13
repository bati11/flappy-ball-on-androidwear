package info.bati11.mywearapplication.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import info.bati11.mywearapplication.adapters.PassedCountAdapter;
import info.bati11.mywearapplication.models.PassedCount;

public class PassedCountListFragment extends ListFragment {

    private Handler handler;
    private List<PassedCount> listItems;
    private PassedCountAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new Handler();

        listItems = new ArrayList<PassedCount>();
        setItems();
        adapter = new PassedCountAdapter(getActivity(), listItems);
        setListAdapter(adapter);
    }

    private void setItems() {
        listItems.clear();
        PassedCount item = PassedCount.get(getActivity());
        listItems.add(item);
    }

    @Override
    public void onDestroyView() {
        setListAdapter(null);
        super.onDestroyView();
    }

    public void notifyDataSetChanged() {
        setItems();
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
