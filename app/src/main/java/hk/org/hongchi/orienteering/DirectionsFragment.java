package hk.org.hongchi.orienteering;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hk.org.hongchi.orienteering.maps.MapDirection;

public class DirectionsFragment extends Fragment {
    private List<MapDirection> directions = new ArrayList<MapDirection>();

    private RecyclerView dirListView;
    private RecyclerView.Adapter<DirectionsListAdapter.ViewHolder> dirListAdapter;
    private LinearLayoutManager dirListManager;

    private MapDirection currentDirection;

    private TextView txtLocationName;
    private TextView txtLocationDesc;

    public DirectionsFragment() {
        currentDirection = new MapDirection("HKUST Library", "A place filled with books.", 22.33801, 114.26414);
    }

    public List<MapDirection> getDirections() {
        return directions;
    }

    public void setDirections(List<MapDirection> directions) {
        this.directions = directions;
    }

    public MapDirection getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(MapDirection currentDirection) {
        this.currentDirection = currentDirection;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (int i = 0; i < 30; i++) {
            directions.add(new MapDirection(UUID.randomUUID().toString().substring(0, 15), "Some nice thing.", 12, 14));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directions, container, false);
        dirListView = (RecyclerView) view.findViewById(R.id.dirRecyclerView);

        dirListManager = new LinearLayoutManager(view.getContext());
        dirListView.setLayoutManager(dirListManager);

        dirListAdapter = new DirectionsListAdapter();
        dirListView.setAdapter(dirListAdapter);

        txtLocationName = (TextView) view.findViewById(R.id.location_title);
        txtLocationDesc = (TextView) view.findViewById(R.id.location_desc);

        txtLocationName.setText(currentDirection.getName());
        txtLocationDesc.setText(currentDirection.getDescription());

        return view;
    }

    public interface DirectionsClickListener {
        public void onMapDirectionSelect(View trackItemView, MapDirection track,
                                         int position);
    }

    private class DirectionsListAdapter extends
            RecyclerView.Adapter<DirectionsListAdapter.ViewHolder> {

        @Override
        public DirectionsListAdapter.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_direction, parent, false);

            ViewHolder vh = new ViewHolder(v, (MainActivity) getActivity());
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MapDirection track = directions.get(position);
            TextView txtLocationName = (TextView) holder.dirItemView
                    .findViewById(R.id.location_title);
            TextView txtLocationDesc = (TextView) holder.dirItemView
                    .findViewById(R.id.location_desc);

            txtLocationName.setText(track.getName());
            txtLocationDesc.setText(track.getDescription());
        }

        @Override
        public int getItemCount() {
            return directions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener {
            public View dirItemView;
            public DirectionsClickListener trackClickListener;

            public ViewHolder(View dirItemView,
                              DirectionsClickListener dirClickListener) {
                super(dirItemView);

                this.dirItemView = dirItemView;
                this.trackClickListener = dirClickListener;

                dirItemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                trackClickListener.onMapDirectionSelect(v,
                        directions.get(getAdapterPosition()),
                        getAdapterPosition());
            }
        }
    }

}
