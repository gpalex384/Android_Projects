package com.algapo.mylista;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algapo.mylista.databinding.FragmentItemListBinding;
import com.algapo.mylista.databinding.ItemListContentBinding;

import com.algapo.mylista.LibroItem;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListFragment extends Fragment {

    public static List<LibroItem> ITEMS = new ArrayList<LibroItem>();
    public static Map<String, LibroItem> ITEM_MAP = new HashMap<String, LibroItem>();
    private FloatingActionButton mFloatButton;

    /**
     * Method to intercept global key events in the
     * item list fragment to trigger keyboard shortcuts
     * Currently provides a toast when Ctrl + Z and Ctrl + F
     * are triggered
     */
    ViewCompat.OnUnhandledKeyEventListenerCompat unhandledKeyEventListenerCompat = (v, event) -> {
        if (event.getKeyCode() == KeyEvent.KEYCODE_Z && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Undo (Ctrl + Z) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }
        return false;
    };

    private FragmentItemListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setBackgroundDrawable(getResources().getDrawable(R.drawable.banlibros));
        mFloatButton = binding.getRoot().findViewById(R.id.floatbutton);
        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                System.exit(0);
            }
        });

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat);

        //RecyclerView recyclerView = binding.itemList;

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        View itemDetailFragmentContainer = view.findViewById(R.id.item_detail_nav_container);
        //itemDetailFragmentContainer.setBackgroundResource(R.drawable.banlibros);

        /* Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        View.OnClickListener onClickListener = itemView -> {
            LibroItem item =
                    (LibroItem) itemView.getTag();
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
            if (itemDetailFragmentContainer != null) {
                Navigation.findNavController(itemDetailFragmentContainer)
                        .navigate(R.id.fragment_item_detail, arguments);
            } else {
                Navigation.findNavController(itemView).navigate(R.id.show_item_detail, arguments);
            }
        };

        /*
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        View.OnContextClickListener onContextClickListener = itemView -> {
            LibroItem item =
                    (LibroItem) itemView.getTag();
            Toast.makeText(
                    itemView.getContext(),
                    "Context click of item " + item.id,
                    Toast.LENGTH_LONG
            ).show();
            return true;
        };

        //setupRecyclerView(recyclerView, onClickListener, onContextClickListener);

        ConnectivityManager connectivityManager = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            getDatosVolley(onClickListener, onContextClickListener);
        } else {
            Toast.makeText(getContext(),"Error: No hay Internet ", Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View.OnClickListener onClickListener,
            View.OnContextClickListener onContextClickListener
    ) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(
                ITEMS,
                onClickListener,
                onContextClickListener
        ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void getDatosVolley(View.OnClickListener onClickListener,
                                View.OnContextClickListener onContextClickListener) {
        String url = "https://mybooks-6a979.web.app/json/mybooks_id.json";
        final Gson gson = new Gson();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray mJSONArray = response.getJSONArray("mybooks");//Nombre del json
                    ITEMS.clear();
                    LibroItem item;
                    for (int i = 0; i < mJSONArray.length(); i++) {
                        JSONObject mJSONObject = mJSONArray.getJSONObject(i);
                        item = gson.fromJson(mJSONObject.toString(), LibroItem.class);
                        ITEMS.add(item);//Añade cada ítem del JSON al arreglo de LibroItem
                        ITEM_MAP.put(Integer.toString(i), item);//Mapea con la posición i
                        RecyclerView recyclerView = binding.itemList;//Pinta la lista
                        assert recyclerView != null;
                        setupRecyclerView((RecyclerView) recyclerView, onClickListener, onContextClickListener);
                    }
                } catch (JSONException err) {
                    err.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
            }
        });
        queue.add(request);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<LibroItem> mValues;
        private final View.OnClickListener mOnClickListener;
        private final View.OnContextClickListener mOnContextClickListener;

        SimpleItemRecyclerViewAdapter(List<LibroItem> items,
                                      View.OnClickListener onClickListener,
                                      View.OnContextClickListener onContextClickListener) {
            mValues = items;
            mOnClickListener = onClickListener;
            mOnContextClickListener = onContextClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ItemListContentBinding binding =
                    ItemListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mAuthorView.setText(mValues.get(position).author);
            holder.mTitleView.setText(mValues.get(position).title);

            Picasso.get().load(mValues.get(position).url_image).into(holder.mImageView);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.itemView.setOnContextClickListener(mOnContextClickListener);
            }
            holder.itemView.setOnLongClickListener(v -> {
                // Setting the item id as the clip data so that the drop target is able to
                // identify the id of the content
                ClipData.Item clipItem = new ClipData.Item(mValues.get(position).id);
                ClipData dragData = new ClipData(
                        ((LibroItem) v.getTag()).title,
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        clipItem
                );

                if (Build.VERSION.SDK_INT >= 24) {
                    v.startDragAndDrop(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                } else {
                    v.startDrag(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                }
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mAuthorView;
            final TextView mTitleView;
            final ImageView mImageView;

            ViewHolder(ItemListContentBinding binding) {
                super(binding.getRoot());
                mAuthorView = binding.author;
                mTitleView = binding.title;
                mImageView = binding.portada;
            }

        }
    }
}