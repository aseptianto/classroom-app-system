package hk.org.hongchi.orienteering;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.keysolutions.ddpclient.android.DDPBroadcastReceiver;
import com.keysolutions.ddpclient.android.DDPStateSingleton;

import java.util.List;

import hk.org.hongchi.orienteering.models.Place;

public class PlacesFragment extends Fragment {
    private RecyclerView dirListView;
    private RecyclerView.Adapter<DirectionsListAdapter.ViewHolder> dirListAdapter;
    private LinearLayoutManager dirListManager;

    private Place current, destination;

    private TextView txtLocationName;
    private TextView txtLocationDesc;

    private Button btnQuiz;

    private DDPBroadcastReceiver ddpReceiver;

    private Callbacks callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        callback = (Callbacks) getActivity();

        View view = inflater.inflate(R.layout.fragment_directions, container, false);
        dirListView = (RecyclerView) view.findViewById(R.id.dirRecyclerView);

        dirListManager = new LinearLayoutManager(view.getContext());
        dirListView.setLayoutManager(dirListManager);

        dirListAdapter = new DirectionsListAdapter();
        dirListView.setAdapter(dirListAdapter);

        txtLocationName = (TextView) view.findViewById(R.id.location_title);
        txtLocationDesc = (TextView) view.findViewById(R.id.location_desc);

        btnQuiz = (Button) view.findViewById(R.id.quizButton);
        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), QuizActivity.class);
                i.putExtra("place", current);

                startActivity(i);
            }
        });

        return view;
    }

    public Place getDestination() {
        return destination;
    }

    public void unlockDestination(Place current) {
        List<Place> places = DDPService.getInstance().getPlaces();
        int index = places.indexOf(current) + 1;
        if (index >= places.size() || index < 0)
            index = 0;

        this.current = current;
        this.destination = places.get(index);

        updateDestination();

    }

    public void unlockDestination(int index) {
        unlockDestination(DDPService.getInstance().getPlaces().get(index));
    }

    private void updateDestination() {
        txtLocationName.setText(current.getName());
        txtLocationDesc.setText(current.getDirections());

        callback.onDestinationUpdate(current, destination);

        dirListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        ddpReceiver = new DDPBroadcastReceiver(DDPService.getInstance(), getActivity()) {
            @Override
            protected void onDDPConnect(DDPStateSingleton ddp) {
                super.onDDPConnect(ddp);

            }

            @Override
            protected void onSubscriptionUpdate(String changeType,
                                                String subscriptionName, String docId) {
                if (subscriptionName.equals("places")) {
                    if (destination == null)
                        unlockDestination(0);
                    else
                        dirListAdapter.notifyDataSetChanged();
                }
            }
        };
        DDPService.getInstance().connectIfNeeded();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (ddpReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity())
                    .unregisterReceiver(ddpReceiver);
            ddpReceiver = null;
        }
    }

    public interface Callbacks {
        void onDestinationUpdate(Place current, Place dest);
    }

    public interface DirectionsClickListener {
        void onPlaceSelect(View trackItemView, Place track,
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
            Place track = DDPService.getInstance().getPlaces().get(position);
            TextView txtLocationName = (TextView) holder.placeItemView
                    .findViewById(R.id.location_title);
            TextView txtLocationDesc = (TextView) holder.placeItemView
                    .findViewById(R.id.location_desc);

            txtLocationName.setText(track.getName());
            txtLocationDesc.setText(track.getDirections());

            if (current == track) {
                holder.placeItemView.setSelected(true);
                holder.placeItemView.setBackgroundResource(R.drawable.side_nav_bar);
            }
        }

        @Override
        public int getItemCount() {
            return DDPService.getInstance().getPlaces().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener {
            public View placeItemView;
            public DirectionsClickListener placeClickListener;

            public ViewHolder(View placeItemView,
                              DirectionsClickListener dirClickListener) {
                super(placeItemView);

                this.placeItemView = placeItemView;
                this.placeClickListener = dirClickListener;

                placeItemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                placeClickListener.onPlaceSelect(v,
                        DDPService.getInstance().getPlaces().get(getAdapterPosition()),
                        getAdapterPosition());
            }
        }
    }

}
