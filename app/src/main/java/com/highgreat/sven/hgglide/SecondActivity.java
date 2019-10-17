package com.highgreat.sven.hgglide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class SecondActivity extends AppCompatActivity {


    String[] url = {"https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=4155045779," +
            "3430902485&fm=27&gp=0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524570152282&di" +
                    "=b4fde3c810cbf0dc6f3841238c0b4181&imgtype=0&src=http%3A%2F" +
                    "%2Fimglf2.ph.126.net%2FoGTeWO7J8VYNaEHok--SmA%3D%3D%2F6599321566470093089.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524570152282&di" +
                    "=541514e8c837032cbc184915e4d66139&imgtype=0&src=http%3A%2F%2Fimg1.gtimg" +
                    ".com%2Fent%2Fpics%2Fhv1%2F74%2F146%2F2023%2F131582879.jpg"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ImageAdapter adapter = new ImageAdapter();
        recyclerView.setAdapter(adapter);
    }

    private final class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.test_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.title.setText(
                    String.valueOf(position));
            holder.imageView.setTag(position);
            Glide.with(SecondActivity.this)
                    .load(url[position % url.length])
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        public final class ViewHolder extends RecyclerView.ViewHolder {

            private final ImageView imageView;
            private final TextView title;

            ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.text);
                imageView = itemView.findViewById(R.id.icon);
            }
        }
    }

}
