package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.holoners.R;
import com.squareup.picasso.Picasso;

import model.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<Music> musicList = new ArrayList<>();
    private ArrayList<Music> filteredList = new ArrayList<>(); // Add filtered list
    private MusicFilter musicFilter;

    public MusicAdapter(Context context, ArrayList<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
        this.filteredList = new ArrayList<>(musicList); // Initialize filtered list
    }

    @Override
    public int getCount() {
        return filteredList.size(); // Use filtered list size
    }

    @Override
    public Object getItem(int i) {
        return filteredList.get(i); // Use filtered list
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setMusicList(ArrayList<Music> musicList) {
        this.musicList = musicList;
        this.filteredList = new ArrayList<>(musicList);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;

        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.item_music, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            itemView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) itemView.getTag();

        Music music = (Music) getItem(i);
        viewHolder.bind(music);
        return itemView;
    }

    private static class ViewHolder {
        private TextView txtTitle, txtBranch;
        private ImageView imgPhoto;

        ViewHolder(View view) {
            txtTitle = view.findViewById(R.id.txt_title);
            txtBranch = view.findViewById(R.id.txt_branch);
            imgPhoto = view.findViewById(R.id.img_photo);
        }

        void bind(Music music) {
            txtTitle.setText(music.getTitle());
            txtBranch.setText(music.getBranch());

            Picasso.get()
                    .load(music.getImage())
                    .error(R.drawable.user) // Gambar dari drawable jika terjadi kesalahan
                    .into(imgPhoto);
        }
    }

    @Override
    public Filter getFilter() {
        if (musicFilter == null) {
            musicFilter = new MusicFilter(this, musicList);
        }
        return musicFilter;
    }

    private static class MusicFilter extends Filter {
        private final MusicAdapter adapter;
        private final List<Music> originalList;
        private final List<Music> filteredList;

        MusicFilter(MusicAdapter adapter, List<Music> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new ArrayList<>(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();

                for (final Music music : originalList) {
                    if (music.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(music);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.filteredList.clear();
            adapter.filteredList.addAll((List<Music>) filterResults.values);
            adapter.notifyDataSetChanged();
        }
    }
}
