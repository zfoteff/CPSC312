/**
 * Program handles back end logic of the main activity screen of the Watch List app. Lets the user
 * view their videos to watch, as well as create new entries and edit existing entries
 * CPSC 312-01, Fall 2021
 * Programming Assignment #7
 *
 * @author Zac Foteff
 * @version v2.0 11/23/21
 */

package com.example.watchlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    CustomAdaptor adaptor;
    ActivityResultLauncher<Intent> launcher;
    ActivityResultLauncher<Intent> contextLauncher;
    private int index;
    private WatchListVideoDatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Opened watchlist main screen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new WatchListVideoDatabaseHelper(this);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            String title = intent.getStringExtra("title");
                            String type = intent.getStringExtra("type");
                            boolean watched = intent.getBooleanExtra("watched", false);
                            int drawable = intent.getIntExtra("drawable_id", R.drawable.placeholderimage);
                            WatchListVideo video = new WatchListVideo(title, type, watched, drawable);

                            Log.d(TAG, "onCreate: "+video.toString());
                            helper.insertVideo(video);
                            adaptor.notifyItemInserted(helper.getNumberOfElements()-1);
                        }
                    }
                });

        contextLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            String videoTitle = intent.getStringExtra("title");
                            String videoType = intent.getStringExtra("type");
                            boolean videoWatched = intent.getBooleanExtra("watched", false);
                            int videoDrawable = intent.getIntExtra("drawable_id", R.drawable.placeholderimage);

                            Log.d(TAG, "onCreate: "+videoTitle+" "+videoType+" "+videoWatched+" "+videoDrawable);
                            helper.updateVideo(new WatchListVideo(videoTitle, videoType, videoWatched, videoDrawable));
                            adaptor.notifyItemChanged(index);
                        }
                    }
                });

        //  Setup recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adaptor = new CustomAdaptor();
        recyclerView.setAdapter(adaptor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.addVideo) {
            Log.d(TAG, "onClick: Clicked create new video");
            Intent intent = new Intent(MainActivity.this, VideoDetailsActivity.class);
            launcher.launch(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    class CustomAdaptor extends RecyclerView.Adapter<CustomAdaptor.CustomViewHolder> {
        boolean multiSelect = false;
        List<WatchListVideo> selectedVideos = new ArrayList<>();
        ActionMode actionMode;
        ActionMode.Callback callbacks;

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView title;
            ImageView image;
            CardView cards;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);

                cards = itemView.findViewById(R.id.myCardView);
                title = itemView.findViewById(R.id.videoTitle);
                image = itemView.findViewById(R.id.videoImage);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void updateView(WatchListVideo v) {
                cards.setCardBackgroundColor(getResources().getColor(R.color.white));
                title.setText(v.toString());
                image.setImageResource(v.getDrawable());
            }

            public void selectVideo(WatchListVideo video) {
                if (multiSelect) {
                    if (selectedVideos.contains(video)) {
                        selectedVideos.remove(video);
                        cards.setCardBackgroundColor(getResources().getColor(R.color.white));
                    } else {
                        selectedVideos.add(video);
                        cards.setCardBackgroundColor(getResources().getColor(R.color.teal_700));
                    }

                    actionMode.setTitle(selectedVideos.size() + " item(s) selected");
                }
            }

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                index = getAdapterPosition();
                Intent intent = new Intent(MainActivity.this, VideoDetailsActivity.class);
                WatchListVideo selected = helper.selectVideo(index);
                selectVideo(selected);

                if (selected != null && !multiSelect) {
                    String title = selected.getTitle();
                    String type = selected.getType();
                    boolean watched = selected.hasWatched();
                    int imgID = selected.getDrawable();
                    intent.putExtra("title", title);
                    intent.putExtra("type", type);
                    intent.putExtra("watched", watched);
                    intent.putExtra("drawable_id", imgID);
                    index = getAdapterPosition();
                    contextLauncher.launch(intent);
                }
            }

            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick: ");
                //cards.setCardBackgroundColor(getResources().getColor(R.color.teal_700));

                index = getAdapterPosition();
                MainActivity.this.startActionMode(callbacks);
                selectVideo(helper.selectAllVideos().get(index));
                //adaptor.notifyItemRemoved(getAdapterPosition());
                return true;
            }
        }

        public CustomAdaptor() {
            super();
            callbacks = new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    multiSelect = true;
                    actionMode = mode;
                    MenuInflater menuInflater = getMenuInflater();
                    menuInflater.inflate(R.menu.cam_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.deleteVideo) {
                        helper.deleteVideos(selectedVideos);
                        actionMode.finish();
                        return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    multiSelect = false;
                    selectedVideos.clear();
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public void onBindViewHolder(@NonNull CustomAdaptor.CustomViewHolder holder, int position) {
            WatchListVideo video = helper.selectAllVideos().get(position);
            holder.updateView(video);
        }

        @Override
        public int getItemCount() {
            return helper.getNumberOfElements();
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.card_item_layout, parent, false);
            return new CustomViewHolder(view);
        }
    }
}