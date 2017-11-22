package grupo4.smarthome;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by estebankramer on 17/11/2017.
 */

public class RoomArrayAdapter extends ArrayAdapter<Room> {
    public RoomArrayAdapter(Activity context, Room[] objects) {
        super(context, R.layout.room_list_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.room_list_item, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.room_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Room room = getItem(position);
        holder.nameTextView.setText(room.getName());

        return convertView;
    }
}
