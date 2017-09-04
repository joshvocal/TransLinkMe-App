package me.joshvocal.translinkme_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.joshvocal.translinkme_app.R;
import me.joshvocal.translinkme_app.model.Bus;

/**
 * Created by josh on 9/4/17.
 */

public class BusStopDetailsAdapter extends
        RecyclerView.Adapter<BusStopDetailsAdapter.BusStopDetailsHolder> {

    private List<Bus> mBusList;
    private Context mContext;

    public BusStopDetailsAdapter(Context context, List<Bus> busList) {
        mContext = context;
        mBusList = busList;
    }

    private String formatArrivalTimeEstimates(List<String> arrivalTimeEstimates) {
        StringBuilder formattedArrivalTimeEstimates = new StringBuilder();

        for (String arrivalTime : arrivalTimeEstimates) {

            if (arrivalTime.contains(" ")) {
                formattedArrivalTimeEstimates
                        .append(arrivalTime.substring(0, arrivalTime.indexOf(" ")))
                        .append(" ");
            } else {
                formattedArrivalTimeEstimates
                        .append(arrivalTime)
                        .append(" ");
            }
        }

        return formattedArrivalTimeEstimates.toString();
    }

    @Override
    public BusStopDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus_stop_details, parent, false);
        return new BusStopDetailsHolder(view);
    }

    @Override
    public void onBindViewHolder(BusStopDetailsHolder holder, int position) {
        Bus currentBus = mBusList.get(position);

        holder.mBusStopDetailsRouteName.setText(currentBus.getRouteName());
        holder.mBusStopDetailsRouteNumber.setText(currentBus.getRouteNumber());
        holder.mBusStopDetailsBusEstimates.setText(
                formatArrivalTimeEstimates(currentBus.getArrivalTimeEstimates()));
    }

    @Override
    public int getItemCount() {
        return mBusList.size();
    }

    public class BusStopDetailsHolder extends RecyclerView.ViewHolder {

        private TextView mBusStopDetailsRouteNumber;
        private TextView mBusStopDetailsRouteName;
        private TextView mBusStopDetailsBusEstimates;

        public BusStopDetailsHolder(View itemView) {
            super(itemView);

            mBusStopDetailsRouteNumber = itemView.findViewById(R.id.bus_stop_details_route_number);
            mBusStopDetailsRouteName = itemView.findViewById(R.id.bus_stop_details_route_name);
            mBusStopDetailsBusEstimates = itemView.findViewById(R.id.bus_stop_details_estimates);
        }
    }
}
