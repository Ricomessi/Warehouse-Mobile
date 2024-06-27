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

import model.Article;
import model.Talent;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<Article> articleList = new ArrayList<>();
    private ArrayList<Article> filteredList = new ArrayList<>();
    private ArticleFilter articleFilter;

    public ArticleAdapter(Context context, ArrayList<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
        this.filteredList = new ArrayList<>(articleList);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Object getItem(int i) {
        return filteredList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setArticleList(ArrayList<Article> articleList) {
        this.articleList = articleList;
        this.filteredList = new ArrayList<>(articleList);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;

        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.item_article, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            itemView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) itemView.getTag();

        Article article = (Article) getItem(i);
        viewHolder.bind(article);
        return itemView;
    }

    private static class ViewHolder {
        private TextView txtName, txtBranch;
        private ImageView imgPhoto;

        ViewHolder(View view) {
            txtName = view.findViewById(R.id.txt_title);
            txtBranch = view.findViewById(R.id.txt_branch);
            imgPhoto = view.findViewById(R.id.img_photo);
        }

        void bind(Article article) {
            txtName.setText(article.getTitle());
            txtBranch.setText(article.getBranch());

            Picasso.get()
                    .load(article.getImage())
                    .error(R.drawable.user) // Gambar dari drawable jika terjadi kesalahan
                    .into(imgPhoto);
        }
    }

    @Override
    public Filter getFilter() {
        if (articleFilter == null) {
            articleFilter = new ArticleFilter(this, articleList);
        }
        return articleFilter;
    }

    private static class ArticleFilter extends Filter {
        private final ArticleAdapter adapter;
        private final List<Article> originalList;
        private final List<Article> filteredList;

        ArticleFilter(ArticleAdapter adapter, List<Article> originalList) {
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

                for (final Article article : originalList) {
                    if (article.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(article);
                    }
                }
            }

            results.values = filteredList;
            results.count= filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.filteredList.clear();
            adapter.filteredList.addAll((List<Article>) filterResults.values);
            adapter.notifyDataSetChanged();
        }
    }
}