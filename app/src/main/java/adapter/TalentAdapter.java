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

import model.Talent;

import java.util.ArrayList;
import java.util.List;

public class TalentAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<Talent> talentList = new ArrayList<>();
    private ArrayList<Talent> filteredList = new ArrayList<>(); // Add filtered list
    private TalentFilter talentFilter;

    public TalentAdapter(Context context, ArrayList<Talent> talentList) {
        this.context = context;
        this.talentList = talentList;
        this.filteredList = new ArrayList<>(talentList); // Initialize filtered list
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

    public void setTalentList(ArrayList<Talent> talentList) {
        this.talentList = talentList;
        this.filteredList = new ArrayList<>(talentList);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;

        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.item_talent, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            itemView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) itemView.getTag();

        Talent talent = (Talent) getItem(i);
        viewHolder.bind(talent);
        return itemView;
    }

    private static class ViewHolder {
        private TextView txtName, txtBranch;
        private ImageView imgPhoto;

        ViewHolder(View view) {
            txtName = view.findViewById(R.id.txt_name);
            txtBranch = view.findViewById(R.id.txt_branch);
            imgPhoto = view.findViewById(R.id.img_photo);
        }

        void bind(Talent talent) {
            txtName.setText(talent.getName());
            txtBranch.setText(talent.getBranch());

            Picasso.get()
                    .load(talent.getImage())
                    .error(R.drawable.user) // Gambar dari drawable jika terjadi kesalahan
                    .into(imgPhoto);
        }
    }
    @Override
    public Filter getFilter() {
        if (talentFilter == null) {
            talentFilter = new TalentFilter(this, talentList);
        }
        return talentFilter;
    }

    private static class TalentFilter extends Filter {
        private final TalentAdapter adapter;
        private final List<Talent> originalList;
        private final List<Talent> filteredList;

        TalentFilter(TalentAdapter adapter, List<Talent> originalList) {
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

                for (final Talent talent : originalList) {
                    if (talent.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(talent);
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
            adapter.filteredList.addAll((List<Talent>) filterResults.values);
            adapter.notifyDataSetChanged();
        }
    }
}
