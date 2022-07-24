package com.algapo.mylista;

import android.content.ClipData;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.DragEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.algapo.mylista.databinding.FragmentItemDetailBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListFragment}
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The placeholder content this fragment is presenting.
     */
    private LibroItem mItem;
    private CollapsingToolbarLayout mToolbarLayout;
    private TextView mTextView;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private final View.OnDragListener dragListener = (v, event) -> {
        if (event.getAction() == DragEvent.ACTION_DROP) {
            ClipData.Item clipDataItem = event.getClipData().getItemAt(0);
            mItem = ItemListFragment.ITEM_MAP.get(clipDataItem.getText().toString());
            updateContent();
        }
        return true;
    };
    private FragmentItemDetailBinding binding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the placeholder content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = ItemListFragment.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
        reproducirSnd(R.raw.caminantenohaycamino);

    }

    @Override
    public void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        mFab = rootView.findViewById(R.id.fab);
        mToolbarLayout = rootView.findViewById(R.id.toolbar_layout);
        mTextView = binding.itemDetail;

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Este libro se publicó en: "
                        + mItem.publication_date, Snackbar.LENGTH_LONG).show();
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.banlibros));

        // Show the placeholder content as text in a TextView & in the toolbar if available.
        updateContent();
        rootView.setOnDragListener(dragListener);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateContent() {
        if (mItem != null) {
            mTextView.setText(mItem.description);
            if (mToolbarLayout != null) {
                mToolbarLayout.setTitle(mItem.title);
            }
        }
    }

    public void reproducirSnd(int snd) {
        try {
            mediaPlayer = MediaPlayer.create(getActivity(), snd);
            mediaPlayer.setVolume(0.5f, 0.5f);
            mediaPlayer.start();
        }
        catch (Exception exc){}
    }
}