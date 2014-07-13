package info.bati11.mywearapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import info.bati11.mywearapplication.R;
import info.bati11.mywearapplication.models.PassedCount;

public class PassedCountAdapter extends ArrayAdapter<PassedCount> {

    private LayoutInflater mLayoutInflater;
    public PassedCountAdapter(Context context, List<PassedCount> items) {
        super(context, 0, items);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PassedCount item = getItem(position);
        View row;
        if (convertView == null) {
            row = mLayoutInflater.inflate(R.layout.list_item_passed_count, parent, false);
        } else {
            row = convertView;
        }
        TextView count = (TextView)row.findViewById(R.id.count);
        count.setText(Integer.toString(item.getCount()));
        TextView date = (TextView)row.findViewById(R.id.date);
        date.setText(item.getLabel());
        return row;
    }
}
